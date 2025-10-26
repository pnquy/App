package com.example.studentportalapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.studentportalapp.R;
import com.example.studentportalapp.model.ActivityItem;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private final ArrayList<ActivityItem> items;

    public CourseAdapter(ArrayList<ActivityItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityItem item = items.get(position);
        holder.tvAuthor.setText(item.getAuthor());
        holder.tvDate.setText(item.getDate());
        holder.tvTitle.setText(item.getTitle());
        holder.tvFileName.setText(item.getFileName());
        holder.tvViews.setText(item.getViews() + " views");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor, tvDate, tvTitle, tvFileName, tvViews;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvViews = itemView.findViewById(R.id.tvViews);
        }
    }
}
