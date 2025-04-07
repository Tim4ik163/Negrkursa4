package com.example.negrskursack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ViewHolder> {
    private List<String> itemList;
    private Context context;

    public ListItemAdapter(Context context, List<String> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = itemList.get(position);
        holder.itemText.setText(item);
        holder.initialText.setText(String.valueOf(item.charAt(0)).toUpperCase());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemText, initialText;
        ImageView checkIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.item_text);
            checkIcon = itemView.findViewById(R.id.check_icon);
            initialText = itemView.findViewById(R.id.initial_text);
        }
    }
}