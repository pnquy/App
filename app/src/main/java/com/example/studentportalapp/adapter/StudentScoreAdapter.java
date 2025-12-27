package com.example.studentportalapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.R;
import com.example.studentportalapp.model.StudentScoreItem;
import java.util.List;

public class StudentScoreAdapter extends RecyclerView.Adapter<StudentScoreAdapter.ViewHolder> {
    private List<StudentScoreItem> list;

    public StudentScoreAdapter(List<StudentScoreItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tái sử dụng layout item_stats_assignment.xml vì cấu trúc gần giống
        // Hoặc bạn có thể tạo layout mới nếu muốn
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stats_assignment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentScoreItem item = list.get(position);
        holder.tvName.setText(item.assignmentName);

        // Hiển thị: Điểm bạn | TB lớp
        holder.tvAvg.setText(String.format("Bạn: %.1f  |  Lớp: %.1f", item.myScore, item.classAverage));

        // Tái sử dụng tvCount để hiện đánh giá
        if (item.myScore >= item.classAverage) {
            holder.tvCount.setText("▲ Cao hơn TB");
            holder.tvCount.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Xanh
        } else {
            holder.tvCount.setText("▼ Thấp hơn TB");
            holder.tvCount.setTextColor(android.graphics.Color.parseColor("#F44336")); // Đỏ
        }
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