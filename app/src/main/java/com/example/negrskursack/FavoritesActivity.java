package com.example.negrskursack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoritesItemAdapter adapter;
    private List<Music> favoriteMusicList; // Список объектов Music
    private ImageView backButton, closeButton, musicNote1, musicNote2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        backButton = findViewById(R.id.back_button);
        closeButton = findViewById(R.id.close_button);
        musicNote1 = findViewById(R.id.music_note_1);
        musicNote2 = findViewById(R.id.music_note_2);

        backButton.setOnClickListener(v -> finish());
        closeButton.setOnClickListener(v -> finish());

        musicNote1.setOnClickListener(v -> startActivity(new Intent(FavoritesActivity.this, MainActivity.class)));
        musicNote2.setOnClickListener(v -> startActivity(new Intent(FavoritesActivity.this, ListActivity.class)));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        favoriteMusicList = dbHelper.getFavoriteMusic(); // Получаем список избранных треков

        adapter = new FavoritesItemAdapter(this, favoriteMusicList); // Передаем список объектов Music
        recyclerView.setAdapter(adapter);
    }
}