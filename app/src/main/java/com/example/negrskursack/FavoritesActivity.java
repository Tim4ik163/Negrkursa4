package com.example.negrskursack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoritesItemAdapter adapter;
    private List<Music> favoriteMusicList;
    private ImageView backButton, closeButton, musicNote1, musicNote2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Инициализация UI элементов
        backButton = findViewById(R.id.back_button);
        closeButton = findViewById(R.id.close_button);
        musicNote1 = findViewById(R.id.music_note_1);
        musicNote2 = findViewById(R.id.music_note_2);

        // Обработчики кликов
        backButton.setOnClickListener(v -> finish());
        closeButton.setOnClickListener(v -> finish());
        musicNote1.setOnClickListener(v -> startActivity(new Intent(FavoritesActivity.this, MainActivity.class)));
        musicNote2.setOnClickListener(v -> startActivity(new Intent(FavoritesActivity.this, ListActivity.class)));

        // Настройка RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Загрузка избранных треков
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        favoriteMusicList = dbHelper.getFavoriteMusic();

        // Установка адаптера
        adapter = new FavoritesItemAdapter(this, favoriteMusicList);
        recyclerView.setAdapter(adapter);
    }
}