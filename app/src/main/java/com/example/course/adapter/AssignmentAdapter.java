package com.example.course.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.course.R;
import com.example.course.model.Assignment;

import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private final Context context;
    private final List<Assignment> assignments;

    public AssignmentAdapter(Context context, List<Assignment> assignments) {
        this.context = context;
        this.assignments = assignments;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_assignment, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment item = assignments.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvPoints.setText(item.getPoints());
        holder.tvAuthor.setText(item.getAuthor());
        holder.tvDate.setText(item.getDate());
        holder.btnAction.setText(item.getButtonText());
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

