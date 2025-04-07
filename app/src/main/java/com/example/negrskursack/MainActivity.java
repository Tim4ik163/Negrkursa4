package com.example.negrskursack;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ImageView albumArt;
    private SeekBar seekBar;
    private TextView titleText, artistText, currentTime, totalTime;
    private DatabaseHelper dbHelper;
    private List<Music> musicList;
    private int currentTrackIndex = 0;
    private Handler seekBarHandler;
    private Button playPauseBtn, prevBtn, nextBtn;
    private boolean isPlaying = false;
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1;

    private final Runnable seekBarUpdater = new Runnable() {
        @Override
        public void run() {
            if (isPlaying) {
                Intent intent = new Intent(MusicService.ACTION_GET_PROGRESS);
                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
                seekBarHandler.postDelayed(this, 200);
            }
        }
    };

    private final BroadcastReceiver musicUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case MusicService.ACTION_UPDATE:
                        boolean playing = intent.getBooleanExtra("isPlaying", false);
                        int progress = intent.getIntExtra("progress", 0);
                        int duration = intent.getIntExtra("duration", 0);
                        updatePlayerUI(playing, progress, duration);
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBarHandler = new Handler();
        initViews();
        setupButtons();
        initDatabase();
        setupSeekBar();

        IntentFilter filter = new IntentFilter(MusicService.ACTION_UPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(musicUpdateReceiver, filter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_CODE_POST_NOTIFICATIONS
            );
        }
    }

    private void initViews() {
        seekBar = findViewById(R.id.seekBar);
        albumArt = findViewById(R.id.albumArt);
        titleText = findViewById(R.id.songTitle);
        artistText = findViewById(R.id.songArtist);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);

        playPauseBtn = findViewById(R.id.playPauseBtn);
        prevBtn = findViewById(R.id.prevBtn);
        nextBtn = findViewById(R.id.nextBtn);
    }

    private void setupButtons() {
        playPauseBtn.setOnClickListener(v -> {
            if (!isPlaying) {
                playCurrentTrack();
            } else {
                pauseMusic();
            }
        });

        prevBtn.setOnClickListener(v -> playPreviousTrack());
        nextBtn.setOnClickListener(v -> playNextTrack());
    }

    private void initDatabase() {
        dbHelper = new DatabaseHelper(this);
        if (dbHelper.getAllMusic().isEmpty()) {
            dbHelper.addMusic("Neumodel - Numb", "Neumodel", R.raw.neumodel_numb, R.drawable.default_album_art);
            dbHelper.addMusic("Date - Tank Ludi", "Date", R.raw.date_tank_ludi, R.drawable.default_album_art);
            dbHelper.addMusic("Alblak - 52", "Alblak", R.raw.alblak_52, R.drawable.default_album_art);
        }
        musicList = dbHelper.getAllMusic();
    }

    private void playCurrentTrack() {
        if (musicList.isEmpty()) {
            Toast.makeText(this, "No music tracks available", Toast.LENGTH_SHORT).show();
            return;
        }

        Music currentMusic = musicList.get(currentTrackIndex);
        titleText.setText(currentMusic.getTitle());
        artistText.setText(currentMusic.getArtist());
        albumArt.setImageResource(currentMusic.getImageResource());

        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.ACTION_PLAY);
        intent.putExtra("music_resource_id", currentMusic.getMp3ResourceId());
        startService(intent);
    }

    private void pauseMusic() {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.ACTION_PAUSE);
        startService(intent);
    }

    private void playNextTrack() {
        currentTrackIndex = (currentTrackIndex + 1) % musicList.size();
        playCurrentTrack();
    }

    private void playPreviousTrack() {
        currentTrackIndex = (currentTrackIndex - 1 + musicList.size()) % musicList.size();
        playCurrentTrack();
    }

    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateTimeText(progress, seekBar.getMax());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopSeekBarUpdates();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent intent = new Intent(MainActivity.this, MusicService.class);
                intent.setAction(MusicService.ACTION_SEEK);
                intent.putExtra("progress", seekBar.getProgress());
                startService(intent);

                if (isPlaying) {
                    startSeekBarUpdates();
                }
            }
        });
    }

    private void updatePlayerUI(boolean playing, int progress, int duration) {
        runOnUiThread(() -> {
            isPlaying = playing;
            playPauseBtn.setText(playing ? "Pause" : "Play");

            if (duration > 0) {
                if (seekBar.getMax() != duration) {
                    seekBar.setMax(duration);
                }
                if (!seekBar.isPressed()) {
                    seekBar.setProgress(progress);
                }
                updateTimeText(progress, duration);
            }

            if (playing) {
                startSeekBarUpdates();
            } else {
                stopSeekBarUpdates();
            }
        });
    }

    private void updateTimeText(int currentPosition, int totalDuration) {
        currentTime.setText(formatTime(currentPosition));
        totalTime.setText(formatTime(totalDuration));
    }

    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void startSeekBarUpdates() {
        seekBarHandler.removeCallbacks(seekBarUpdater);
        seekBarHandler.post(seekBarUpdater);
    }

    private void stopSeekBarUpdates() {
        seekBarHandler.removeCallbacks(seekBarUpdater);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        seekBarHandler.removeCallbacksAndMessages(null);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(musicUpdateReceiver);
    }
}