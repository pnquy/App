package com.example.studentportalapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.AssignmentActivity;
import com.example.studentportalapp.CourseActivity;
import com.example.studentportalapp.GradeActivity;
import com.example.studentportalapp.R;
import com.example.studentportalapp.ViewSubmissionsActivity;
import com.example.studentportalapp.data.Entity.ThongBao;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<ThongBao> list;

    public NotificationAdapter(List<ThongBao> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (list == null || position >= list.size()) return;
        
        ThongBao tb = list.get(position);
        if (tb == null) return;

        holder.tvContent.setText(tb.NoiDung != null ? tb.NoiDung : "");
        holder.tvDate.setText(tb.NgayTao != null ? tb.NgayTao : "");
        
        if (tb.IsRead) {
            holder.layoutClick.setAlpha(0.6f);
        } else {
            holder.layoutClick.setAlpha(1.0f);
        }

        // Xử lý sự kiện click chuyển trang
        holder.layoutClick.setOnClickListener(v -> {
            Context context = v.getContext();
            try {
                if (tb.LoaiTB == null || tb.LoaiTB.isEmpty()) {
                    Toast.makeText(context, "Thông báo không có dữ liệu điều hướng", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = null;
                switch (tb.LoaiTB) {
                    case "ASSIGNMENT":
                        if (tb.TargetId != null) {
                            SharedPreferences.Editor editor = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE).edit();
                            editor.putString("CURRENT_CLASS_ID", tb.TargetId);
                            editor.apply();
                            intent = new Intent(context, AssignmentActivity.class);
                        }
                        break;
                    case "LECTURE":
                        if (tb.TargetId != null) {
                            SharedPreferences.Editor editor = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE).edit();
                            editor.putString("CURRENT_CLASS_ID", tb.TargetId);
                            editor.apply();
                            // SỬA: Link tới màn hình Bài Giảng (CourseActivity)
                            intent = new Intent(context, CourseActivity.class);
                        }
                        break;
                    case "GRADE":
                        intent = new Intent(context, GradeActivity.class);
                        if (tb.TargetId != null) {
                            intent.putExtra("TARGET_CLASS_ID", tb.TargetId);
                        }
                        break;
                    case "SUBMISSION":
                        if (tb.TargetId != null) {
                            intent = new Intent(context, ViewSubmissionsActivity.class);
                            intent.putExtra("MA_BT", tb.TargetId);
                            intent.putExtra("TEN_BT", "Chi tiết bài nộp");
                        }
                        break;
                }

                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Không tìm thấy trang liên kết", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvDate;
        View layoutClick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_noti_content);
            tvDate = itemView.findViewById(R.id.tv_noti_date);
            layoutClick = itemView;
        }
    }
}
