package com.example.negrskursack;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.content.res.AssetFileDescriptor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MusicService extends Service {
    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_PAUSE = "PAUSE";
    public static final String ACTION_SEEK = "SEEK";
    public static final String ACTION_UPDATE = "UPDATE";
    public static final String ACTION_GET_PROGRESS = "GET_PROGRESS";
    public static final String ACTION_SET_REPEAT = "SET_REPEAT";

    private MediaPlayer mediaPlayer;
    private int currentResourceId = -1;
    private int lastPlaybackPosition = 0;
    private Handler handler;
    private Runnable progressUpdater;
    private boolean isRepeatEnabled = false;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_PLAY:
                    handlePlayAction(intent.getIntExtra("music_resource_id", -1));
                    break;
                case ACTION_PAUSE:
                    pausePlayback();
                    break;
                case ACTION_SEEK:
                    seekTo(intent.getIntExtra("progress", 0));
                    break;
                case ACTION_SET_REPEAT:
                    isRepeatEnabled = intent.getBooleanExtra("repeat", false);
                    break;
                case ACTION_GET_PROGRESS:
                    sendCurrentProgress();
                    break;
            }
        }
        return START_STICKY;
    }

    private void handlePlayAction(int resId) {
        if (resId == -1) return;

        if (currentResourceId != resId) {
            lastPlaybackPosition = 0;
            currentResourceId = resId;
            prepareAndPlay(resId);
        } else {
            resumePlayback();
        }
    }

    private void prepareAndPlay(int resId) {
        try {
            releaseMediaPlayer();

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
            );

            AssetFileDescriptor afd = getResources().openRawResourceFd(resId);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.seekTo(lastPlaybackPosition);
                mp.start();
                startProgressUpdates();
                sendUpdateBroadcast(true);
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                if (isRepeatEnabled) {
                    mp.seekTo(0);
                    mp.start();
                } else {
                    lastPlaybackPosition = 0;
                    sendUpdateBroadcast(false);
                }
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("MusicService", "Error: " + what + ", " + extra);
                sendUpdateBroadcast(false);
                return true;
            });

            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            Log.e("MusicService", "Error preparing media", e);
            releaseMediaPlayer();
        }
    }

    private void resumePlayback() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(lastPlaybackPosition);
            mediaPlayer.start();
            startProgressUpdates();
            sendUpdateBroadcast(true);
        }
    }

    private void pausePlayback() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            lastPlaybackPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            stopProgressUpdates();
            sendUpdateBroadcast(false);
        }
    }

    private void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
            lastPlaybackPosition = position;
            sendUpdateBroadcast(mediaPlayer.isPlaying());
        }
    }

    private void sendCurrentProgress() {
        if (mediaPlayer != null) {
            sendUpdateBroadcast(mediaPlayer.isPlaying());
        }
    }

    private void startProgressUpdates() {
        handler.removeCallbacks(progressUpdater);
        progressUpdater = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    sendUpdateBroadcast(true);
                    handler.postDelayed(this, 200);
                }
            }
        };
        handler.post(progressUpdater);
    }

    private void stopProgressUpdates() {
        handler.removeCallbacks(progressUpdater);
    }

    private void sendUpdateBroadcast(boolean isPlaying) {
        Intent intent = new Intent(ACTION_UPDATE);
        intent.putExtra("isPlaying", isPlaying);
        if (mediaPlayer != null) {
            intent.putExtra("progress", mediaPlayer.getCurrentPosition());
            intent.putExtra("duration", mediaPlayer.getDuration());
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void releaseMediaPlayer() {
        stopProgressUpdates();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        releaseMediaPlayer();
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}