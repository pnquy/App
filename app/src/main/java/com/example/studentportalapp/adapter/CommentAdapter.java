package com.example.studentportalapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportalapp.R;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.BinhLuan;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private final List<BinhLuan> list;
    private final AppDatabase db;

    public CommentAdapter(Context context, List<BinhLuan> list) {
        this.list = list;
        this.db = AppDatabase.getDatabase(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BinhLuan bl = list.get(position);
        holder.tvName.setText(bl.TenNguoiGui);
        holder.tvContent.setText(bl.NoiDung);
        holder.tvDate.setText(bl.NgayTao);

        // SỬA: Đổi màu nền nếu là bình luận của giáo viên
        AppDatabase.databaseWriteExecutor.execute(() -> {
            String role = db.taiKhoanDao().getRoleById(bl.MaNguoiGui);
            ((android.app.Activity) holder.itemView.getContext()).runOnUiThread(() -> {
                if ("GIAOVIEN".equals(role)) {
                    holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_teacher_comment));
                } else {
                    holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_file_link));
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvContent, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_comment_name);
            tvContent = itemView.findViewById(R.id.tv_comment_content);
            tvDate = itemView.findViewById(R.id.tv_comment_date);
        }
    }
}
