package com.example.studentportalapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportalapp.R;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.BaiTap;
import com.example.studentportalapp.data.Entity.Diem;
import com.example.studentportalapp.data.Entity.HocVien;
import com.example.studentportalapp.data.Entity.NopBai;
import com.example.studentportalapp.data.Entity.ThongBao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class SubmissionsAdapter extends RecyclerView.Adapter<SubmissionsAdapter.ViewHolder> {

    private final Context context;
    private final List<NopBai> list;
    private final OnItemClickListener listener;
    private final AppDatabase db;
    private final String currentMaGV;

    public interface OnItemClickListener {
        void onItemClick(NopBai nb);
    }

    public SubmissionsAdapter(Context context, List<NopBai> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.db = AppDatabase.getDatabase(context);
        SharedPreferences prefs = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        this.currentMaGV = prefs.getString("KEY_USER_ID", "");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_submission, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NopBai nb = list.get(position);

        holder.tvId.setText("Mã HV: " + nb.MaHV);
        holder.tvDate.setText(nb.NgayNop);
        holder.tvNote.setText("Ghi chú: " + (nb.GhiChu != null && !nb.GhiChu.isEmpty() ? nb.GhiChu : "(Không có ghi chú)"));

        if (nb.FileName != null && !nb.FileName.isEmpty()) {
            holder.tvFileName.setText(nb.FileName);
            holder.layoutFile.setVisibility(View.VISIBLE);
        } else {
            holder.layoutFile.setVisibility(View.GONE);
        }

        // 1. Load tên học viên và ĐIỂM CŨ (nếu có)
        Executors.newSingleThreadExecutor().execute(() -> {
            HocVien hv = db.hocVienDao().getByIdSync(nb.MaHV);
            
            if (context instanceof androidx.lifecycle.LifecycleOwner) {
                ((android.app.Activity) context).runOnUiThread(() -> {
                    if (hv != null) holder.tvName.setText(hv.getTenHV());
                    
                    db.diemDao().getByHocVienBaiTap(nb.MaHV, nb.MaBT).observe((androidx.lifecycle.LifecycleOwner) context, existingDiem -> {
                        if (existingDiem != null) {
                            holder.etGrade.setText(String.valueOf(existingDiem.SoDiem));
                            holder.etFeedback.setText(existingDiem.NhanXet);
                            holder.btnSaveGrade.setText("Cập nhật điểm");
                        } else {
                            holder.etGrade.setText("");
                            holder.etFeedback.setText("");
                            holder.btnSaveGrade.setText("Lưu điểm & Nhận xét");
                        }
                    });
                });
            }
        });

        // 2. Xử lý nút Lưu điểm
        holder.btnSaveGrade.setOnClickListener(v -> {
            String gradeStr = holder.etGrade.getText().toString().trim();
            String feedback = holder.etFeedback.getText().toString().trim();

            if (gradeStr.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập điểm!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double grade = Double.parseDouble(gradeStr);
                Diem diemObj = new Diem();
                diemObj.MaHV = nb.MaHV;
                diemObj.MaBT = nb.MaBT;
                diemObj.MaGV = currentMaGV;
                diemObj.SoDiem = grade;
                diemObj.NhanXet = feedback;

                Executors.newSingleThreadExecutor().execute(() -> {
                    db.diemDao().insert(diemObj);
                    
                    // GỬI THÔNG BÁO CHO HỌC VIÊN KHI CÓ ĐIỂM
                    BaiTap bt = db.baiTapDao().getByIdSync(nb.MaBT);
                    if (bt != null) {
                        ThongBao tb = new ThongBao();
                        tb.NoiDung = "Giáo viên đã chấm điểm bài: " + bt.TenBT + " (Điểm: " + grade + ")";
                        tb.NgayTao = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
                        tb.NguoiNhan = nb.MaHV; // Gửi riêng cho học viên này
                        db.thongBaoDao().insert(tb);
                    }

                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            Toast.makeText(context, "Đã lưu điểm và thông báo cho học viên!", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Điểm không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(v -> listener.onItemClick(nb));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvDate, tvNote, tvName, tvFileName;
        EditText etGrade, etFeedback;
        Button btnSaveGrade;
        View layoutFile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_submitter_name);
            tvId = itemView.findViewById(R.id.tv_submitter_id);
            tvDate = itemView.findViewById(R.id.tv_submit_date);
            tvNote = itemView.findViewById(R.id.tv_submit_note);
            tvFileName = itemView.findViewById(R.id.tv_submission_file_name);
            layoutFile = itemView.findViewById(R.id.layout_submission_file);
            
            etGrade = itemView.findViewById(R.id.et_grade);
            etFeedback = itemView.findViewById(R.id.et_feedback);
            btnSaveGrade = itemView.findViewById(R.id.btn_save_grade);
        }
    }
}
