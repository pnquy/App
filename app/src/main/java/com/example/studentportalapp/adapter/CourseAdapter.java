package com.example.studentportalapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.R;
import com.example.studentportalapp.model.ActivityItem;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private final List<ActivityItem> items;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public CourseAdapter(List<ActivityItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityItem item = items.get(position);
        holder.tvAuthor.setText(item.getAuthor());
        holder.tvDate.setText(item.getDate());
        holder.tvTitle.setText(item.getTitle());
        holder.tvViews.setText(item.getViews() + " views");

        if (item.getFileName() != null && !item.getFileName().isEmpty()) {
            holder.tvFileName.setText(item.getFileName());
            if(holder.layoutFile != null) holder.layoutFile.setVisibility(View.VISIBLE);
            else holder.tvFileName.setVisibility(View.VISIBLE);
        } else {
            if(holder.layoutFile != null) holder.layoutFile.setVisibility(View.GONE);
            else holder.tvFileName.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor, tvDate, tvTitle, tvFileName, tvViews;
        LinearLayout layoutFile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvViews = itemView.findViewById(R.id.tvViews);

            if (tvFileName.getParent() instanceof LinearLayout) {
                layoutFile = (LinearLayout) tvFileName.getParent();
                if(layoutFile.getId() == View.NO_ID) layoutFile = null;
            }
        }
    }
}