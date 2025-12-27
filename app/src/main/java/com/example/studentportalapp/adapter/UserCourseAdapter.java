package com.example.studentportalapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.R;
import com.example.studentportalapp.data.Entity.LopHoc;
import com.example.studentportalapp.model.CourseViewItem; // Import model mới
import java.util.List;

public class UserCourseAdapter extends RecyclerView.Adapter<UserCourseAdapter.ViewHolder> {

    private Context context;
    private List<CourseViewItem> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(LopHoc lopHoc);
    }

    public UserCourseAdapter(Context context, List<CourseViewItem> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void updateList(List<CourseViewItem> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseViewItem item = list.get(position);
        LopHoc lop = item.lopHoc;

        holder.tvName.setText(lop.TenLH);

        String tenGV = (lop.MaGV == null) ? "Chưa phân công" : lop.MaGV;
        holder.tvCode.setText(lop.MaLH + " | GV: " + tenGV);

        holder.progressBar.setProgress(item.progressValue);

        holder.tvPercent.setText(item.progressText);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(lop));
    }

    @Override
    public int getItemCount() { return list == null ? 0 : list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCode, tvPercent;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCourseName);
            tvCode = itemView.findViewById(R.id.tvCourseCode);
            progressBar = itemView.findViewById(R.id.progressBar);
            tvPercent = ((ViewGroup) progressBar.getParent()).findViewWithTag("percentText");
            tvPercent = itemView.findViewById(R.id.tvPercent);
        }
    }
}