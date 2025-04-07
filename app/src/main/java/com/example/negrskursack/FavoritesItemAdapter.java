package com.example.negrskursack;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FavoritesItemAdapter extends RecyclerView.Adapter<FavoritesItemAdapter.ViewHolder> {
    private Context context;
    private List<Music> favoriteMusicList;

    public FavoritesItemAdapter(Context context, List<Music> favoriteMusicList) {
        this.context = context;
        this.favoriteMusicList = favoriteMusicList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favorite_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Music music = favoriteMusicList.get(position);
        holder.titleTextView.setText(music.getTitle());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("music_resource_id", music.getMp3ResourceId());
            intent.putExtra("music_title", music.getTitle());
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // Используйте этот флаг
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return favoriteMusicList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.favorite_item_title);
        }
    }
}