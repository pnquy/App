package com.example.studentportalapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.studentportalapp.R;
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

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Task task = taskList.get(position);

        holder.tvTaskTitle.setText(task.getTitle());
        holder.tvTaskSubject.setText(task.getSubject());
        holder.tvTaskDue.setText(task.getDueDate());

        return convertView;
    }

    static class ViewHolder {
        TextView tvTaskTitle;
        TextView tvTaskSubject;
        TextView tvTaskDue;
    }
}
