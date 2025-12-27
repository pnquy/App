package com.example.studentportalapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.R;
import com.example.studentportalapp.model.TeacherRankItem;
import java.util.List;

public class TeacherRankAdapter extends RecyclerView.Adapter<TeacherRankAdapter.ViewHolder> {
    private List<TeacherRankItem> list;

    public TeacherRankAdapter(List<TeacherRankItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_rank, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TeacherRankItem item = list.get(position);
        holder.tvRank.setText(String.valueOf(position + 1));
        holder.tvName.setText(item.TenGV);
        holder.tvCount.setText(item.Count + " lớp");

        // Highlight Top 1
        if (position == 0) {
            holder.tvRank.setTextColor(android.graphics.Color.parseColor("#FFD700")); // Màu vàng
            holder.tvRank.setTextSize(20);
        }
    }

    @Override
    public int getItemCount() { return list == null ? 0 : list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvName, tvCount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRankNumber);
            tvName = itemView.findViewById(R.id.tvTeacherNameRank);
            tvCount = itemView.findViewById(R.id.tvCountRank);
        }
    }
}