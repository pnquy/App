package com.example.studentportalapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.R;
import com.example.studentportalapp.data.Entity.BaiTap;
import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private final Context context;
    private final List<BaiTap> assignments;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(BaiTap baiTap);
    }

    public AssignmentAdapter(Context context, List<BaiTap> assignments, OnItemClickListener listener) {
        this.context = context;
        this.assignments = assignments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_assignment, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        BaiTap item = assignments.get(position);
        holder.tvTitle.setText(item.TenBT);
        holder.tvPoints.setText("Hạn nộp: " + item.Deadline);

        if (item.FileName != null && !item.FileName.isEmpty()) {
            holder.tvAuthor.setText("File: " + item.FileName);
        } else {
            holder.tvAuthor.setText("Không có file");
        }

        holder.tvDate.setText("");
        holder.btnAction.setText("Chi tiết");

        holder.btnAction.setOnClickListener(v -> listener.onItemClick(item));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPoints, tvAuthor, tvDate;
        Button btnAction;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPoints = itemView.findViewById(R.id.tvPoints);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}