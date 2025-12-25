package com.example.studentportalapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.R;
import com.example.studentportalapp.model.StatsStudentItem;
import java.util.List;

public class StatsStudentAdapter extends RecyclerView.Adapter<StatsStudentAdapter.ViewHolder> {

    private final List<StatsStudentItem> list;

    public StatsStudentAdapter(List<StatsStudentItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stats_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatsStudentItem item = list.get(position);
        holder.tvName.setText(item.hocVien.getTenHV());
        holder.tvId.setText(item.hocVien.getMaHV());

        if (item.hasScore) {
            holder.tvScore.setText(String.valueOf(item.score));
            holder.tvScore.setTextColor(android.graphics.Color.parseColor("#FF9800"));
        } else {
            holder.tvScore.setText("-");
            holder.tvScore.setTextColor(android.graphics.Color.GRAY);
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvId, tvScore;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvStudentName);
            tvId = itemView.findViewById(R.id.tvStudentID);
            tvScore = itemView.findViewById(R.id.tvScore);
        }
    }
}