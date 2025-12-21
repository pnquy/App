package com.example.studentportalapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportalapp.R;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.BaiTap;
import com.example.studentportalapp.data.Entity.Diem;

import java.util.List;
import java.util.concurrent.Executors;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.ViewHolder> {

    private final Context context;
    private final List<Diem> listDiem;
    private final AppDatabase db;

    public GradeAdapter(Context context, List<Diem> listDiem) {
        this.context = context;
        this.listDiem = listDiem;
        this.db = AppDatabase.getDatabase(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grade, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Diem diem = listDiem.get(position);

        // Hiển thị điểm số và nhận xét
        holder.tvScore.setText(String.valueOf(diem.SoDiem));
        holder.tvFeedback.setText("Nhận xét: " + (diem.NhanXet != null ? diem.NhanXet : "(Không có nhận xét)"));

        // Lấy tên bài tập từ Database (vì trong bảng DIEM chỉ lưu MaBT)
        Executors.newSingleThreadExecutor().execute(() -> {
            BaiTap bt = db.baiTapDao().getByIdSync(diem.MaBT);
            if (bt != null) {
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        holder.tvTitle.setText(bt.TenBT);
                    });
                }
            } else {
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        holder.tvTitle.setText("Mã BT: " + diem.MaBT);
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listDiem.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvScore, tvFeedback;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_grade_title);
            tvScore = itemView.findViewById(R.id.tv_grade_score);
            tvFeedback = itemView.findViewById(R.id.tv_grade_feedback);
        }
    }
}
