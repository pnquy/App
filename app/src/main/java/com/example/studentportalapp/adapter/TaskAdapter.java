package com.example.studentportalapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.studentportalapp.R;
import com.example.studentportalapp.SubmitAssignmentActivity;
import com.example.studentportalapp.ViewSubmissionsActivity;
import com.example.studentportalapp.model.Task;

import java.util.List;

public class TaskAdapter extends BaseAdapter {

    private Context context;
    private List<Task> taskList;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
            holder = new ViewHolder();

            holder.tvTaskTitle = convertView.findViewById(R.id.tv_task_title);
            holder.tvTaskSubject = convertView.findViewById(R.id.tv_task_subject);
            holder.tvTaskDue = convertView.findViewById(R.id.tv_task_due);
            holder.btnSubmit = convertView.findViewById(R.id.btn_submit);
            holder.tvSubmissionCount = convertView.findViewById(R.id.tv_submission_count);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Task task = taskList.get(position);

        holder.tvTaskTitle.setText(task.getTitle());
        holder.tvTaskSubject.setText(task.getSubject());
        holder.tvTaskDue.setText(task.getDueDate());

        if ("HOCVIEN".equals(task.getUserRole())) {
            holder.btnSubmit.setVisibility(View.VISIBLE);
            holder.tvSubmissionCount.setVisibility(View.GONE);
            holder.btnSubmit.setOnClickListener(v -> {
                Intent intent = new Intent(context, SubmitAssignmentActivity.class);
                intent.putExtra("MA_BT", task.getMaBT());
                context.startActivity(intent);
            });
        } else if ("GIAOVIEN".equals(task.getUserRole())) {
            holder.btnSubmit.setVisibility(View.GONE);
            holder.tvSubmissionCount.setVisibility(View.VISIBLE);
            String submissionText = "Đã nộp: " + task.getSubmissionCount() + "/" + task.getTotalStudents();
            holder.tvSubmissionCount.setText(submissionText);
            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ViewSubmissionsActivity.class);
                intent.putExtra("MA_BT", task.getMaBT());
                intent.putExtra("TEN_BT", task.getTitle());
                context.startActivity(intent);
            });
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvTaskTitle, tvTaskSubject, tvTaskDue, tvSubmissionCount;
        Button btnSubmit;
    }
}
