package com.example.studentportalapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportalapp.adapter.HocVienAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.HocVien;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentManageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HocVienAdapter adapter;
    private List<HocVien> hocVienList = new ArrayList<>();
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_manage);

        recyclerView = findViewById(R.id.recyclerHocVien);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = AppDatabase.getDatabase(getApplicationContext());

        // Nút quay lại
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Tải dữ liệu
        loadHocVien();
    }

    // Thay thế toàn bộ hàm loadHocVien() cũ bằng đoạn này:
    private void loadHocVien() {
        // Quan sát danh sách học viên
        db.hocVienDao().getAll().observe(this, listHV -> {

            // Chạy background để lấy thông tin lớp cho từng học viên
            executor.execute(() -> {
                List<com.example.studentportalapp.model.StudentItem> displayList = new ArrayList<>();

                if (listHV != null) {
                    for (HocVien hv : listHV) {
                        // 1. Tìm các lớp học viên này tham gia
                        List<String> classIds = db.thamGiaDao().getClassIdsByStudent(hv.getMaHV());

                        // 2. Nối thành chuỗi (VD: "CNTT1, CNTT2")
                        String classNames = "";
                        if (classIds != null && !classIds.isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < classIds.size(); i++) {
                                sb.append(classIds.get(i));
                                if (i < classIds.size() - 1) sb.append(", ");
                            }
                            classNames = sb.toString();
                        }

                        // 3. Tạo item hiển thị
                        displayList.add(new com.example.studentportalapp.model.StudentItem(hv, classNames));
                    }
                }

                // 4. Cập nhật UI
                runOnUiThread(() -> {
                    adapter = new HocVienAdapter(this, displayList, new HocVienAdapter.OnItemClickListener() {
                        @Override
                        public void onEdit(HocVien hv) {
                            showEditDialog(hv);
                        }

                        @Override
                        public void onDelete(HocVien hv) {
                            showDeleteConfirmDialog(hv);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                });
            });
        });
    }

    // ================== CÁC HÀM DIALOG ĐÃ SỬA ==================

    private void showEditDialog(HocVien hv) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_student, null);

        TextInputEditText etTen = view.findViewById(R.id.etEditTenHV);
        TextInputEditText etEmail = view.findViewById(R.id.etEditEmailHV);
        TextInputEditText etLop = view.findViewById(R.id.etEditLopHV);

        // Đổ dữ liệu Tên & Email (Có sẵn trong object)
        if (hv.getTenHV() != null) etTen.setText(hv.getTenHV());
        if (hv.getEmail() != null) etEmail.setText(hv.getEmail());

        // --- PHẦN SỬA ĐỔI QUAN TRỌNG ---
        // Vì MaLH không còn trong HocVien, ta phải query từ bảng THAMGIA
        etLop.setText("Đang tải danh sách lớp...");
        etLop.setEnabled(false); // Khóa không cho sửa
        etLop.setFocusable(false);

        executor.execute(() -> {
            // Lấy danh sách các mã lớp mà học viên này tham gia
            List<String> classes = db.thamGiaDao().getClassIdsByStudent(hv.getMaHV());

            // Chuyển danh sách thành chuỗi (Ví dụ: "LH01, LH02")
            String displayStr;
            if (classes == null || classes.isEmpty()) {
                displayStr = "Chưa tham gia lớp nào";
            } else {
                // Nối các mã lớp lại với nhau bằng dấu phẩy
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < classes.size(); i++) {
                    sb.append(classes.get(i));
                    if (i < classes.size() - 1) sb.append(", ");
                }
                displayStr = sb.toString();
            }

            // Cập nhật lên giao diện (phải dùng runOnUiThread)
            runOnUiThread(() -> etLop.setText(displayStr));
        });
        // --------------------------------

        builder.setView(view);

        builder.setPositiveButton("Lưu thông tin", (dialog, which) -> {
            String newTen = etTen.getText() != null ? etTen.getText().toString().trim() : "";
            String newEmail = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

            hv.setTenHV(newTen);
            hv.setEmail(newEmail);

            executor.execute(() -> {
                db.hocVienDao().update(hv);
                runOnUiThread(() ->
                        Toast.makeText(StudentManageActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                );
            });
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteConfirmDialog(HocVien hv) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_delete, null);

        TextView tvMessage = view.findViewById(R.id.tvConfirmMessage);

        // Sửa câu thông báo: Bỏ phần "khỏi lớp..." vì học viên không còn gắn cứng với 1 lớp
        tvMessage.setText("Bạn có chắc muốn xóa học viên " + hv.getTenHV() + " khỏi hệ thống? (Học viên sẽ bị xóa khỏi tất cả các lớp)");

        builder.setView(view);

        builder.setPositiveButton("Xóa ngay", (dialog, which) -> {
            executor.execute(() -> {
                db.hocVienDao().delete(hv);
                runOnUiThread(() ->
                        Toast.makeText(StudentManageActivity.this, "Đã xóa học viên!", Toast.LENGTH_SHORT).show()
                );
            });
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}