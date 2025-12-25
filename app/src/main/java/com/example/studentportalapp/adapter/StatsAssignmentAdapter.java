package com.example.studentportalapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.R;
import com.example.studentportalapp.model.StatsAssignmentItem;
import java.util.List;

public class StatsAssignmentAdapter extends RecyclerView.Adapter<StatsAssignmentAdapter.ViewHolder> {

    private final List<StatsAssignmentItem> list;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(StatsAssignmentItem item);
    }

    public StatsAssignmentAdapter(List<StatsAssignmentItem> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stats_assignment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatsAssignmentItem item = list.get(position);
        holder.tvName.setText(item.baiTap.TenBT);
        holder.tvCount.setText("Đã chấm: " + item.submissionCount);

        if (item.submissionCount > 0) {
            holder.tvAvg.setText(String.format("TB: %.2f", item.averageScore));
        } else {
            holder.tvAvg.setText("Chưa có điểm");
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAvg, tvCount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAssignmentName);
            tvAvg = itemView.findViewById(R.id.tvAvgScore);
            tvCount = itemView.findViewById(R.id.tvSubmissionCount);
        }
    }
}