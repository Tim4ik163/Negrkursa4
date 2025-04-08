package com.example.negrskursack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;
    private DatabaseHelper dbHelper;
    private ImageView backButton, closeButton, musicNote1, heartIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        backButton = findViewById(R.id.back_button);
        closeButton = findViewById(R.id.close_button);
        musicNote1 = findViewById(R.id.music_note_1);
        heartIcon = findViewById(R.id.heart_icon);
        dbHelper = new DatabaseHelper(this);

        backButton.setOnClickListener(v -> finish());
        closeButton.setOnClickListener(v -> finish());
        musicNote1.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, MainActivity.class);
            startActivity(intent);
        });
        heartIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadMusicData();
    }

    private void loadMusicData() {
        List<Music> musicList = dbHelper.getAllMusic();
        musicAdapter = new MusicAdapter(this, musicList);
        recyclerView.setAdapter(musicAdapter);

        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Music music) {
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                intent.putExtra("selected_music_id", music.getId()); // Передаем ID трека
                startActivity(intent);
            }
        });
    }
}