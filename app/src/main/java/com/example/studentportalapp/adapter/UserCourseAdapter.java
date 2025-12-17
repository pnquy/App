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
import java.util.List;
import java.util.Random;

public class UserCourseAdapter extends RecyclerView.Adapter<UserCourseAdapter.ViewHolder> {

    private Context context;
    private List<LopHoc> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(LopHoc lopHoc);
    }

    public UserCourseAdapter(Context context, List<LopHoc> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Layout mới đẹp hơn
        View view = LayoutInflater.from(context).inflate(R.layout.item_course_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LopHoc lop = list.get(position);

        // 1. Set Tên Môn Học
        holder.tvName.setText(lop.TenLH);

        // 2. Set Mã Lớp | Tên GV (Gộp chung vào 1 dòng như thiết kế mới)
        String tenGV = (lop.MaGV == null) ? "Chưa phân công" : lop.MaGV;
        holder.tvCode.setText(lop.MaLH + " | GV: " + tenGV);

        // 3. Giả lập thanh tiến độ (Random cho đẹp vì chưa có database điểm danh)
        // Sau này có thể tính % dựa trên số buổi điểm danh
        int progress = new Random().nextInt(40) + 30; // Random từ 30% - 70%
        holder.progressBar.setProgress(progress);

        // Nếu trong layout bạn có TextView hiển thị % thì set ở đây (nếu không thì bỏ qua)
        // holder.tvPercent.setText(progress + "%");

        holder.itemView.setOnClickListener(v -> listener.onItemClick(lop));
    }

    @Override
    public int getItemCount() { return list == null ? 0 : list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCode;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ đúng với ID trong item_course_user.xml MỚI
            tvName = itemView.findViewById(R.id.tvCourseName);
            tvCode = itemView.findViewById(R.id.tvCourseCode);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}