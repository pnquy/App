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

    private void loadHocVien() {
        // Sử dụng observe để lắng nghe thay đổi từ Database
        db.hocVienDao().getAll().observe(this, list -> {
            // Cập nhật danh sách mới
            hocVienList = list;

            // Nếu danh sách null thì gán rỗng để tránh lỗi
            if (hocVienList == null) hocVienList = new ArrayList<>();

            // Khởi tạo Adapter
            adapter = new HocVienAdapter(this, hocVienList, new HocVienAdapter.OnItemClickListener() {
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
    }

    // ================== CÁC HÀM DIALOG (Dùng XML riêng) ==================

    private void showEditDialog(HocVien hv) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 1. Nạp layout từ file dialog_edit_student.xml
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_student, null);

        // 2. Ánh xạ các View trong Dialog
        // Lưu ý: Dùng TextInputEditText vì trong XML bạn dùng TextInputLayout
        TextInputEditText etTen = view.findViewById(R.id.etEditTenHV);
        TextInputEditText etEmail = view.findViewById(R.id.etEditEmailHV);
        TextInputEditText etLop = view.findViewById(R.id.etEditLopHV);

        // 3. Đổ dữ liệu cũ vào các ô
        if (hv.getTenHV() != null) etTen.setText(hv.getTenHV());
        if (hv.getEmail() != null) etEmail.setText(hv.getEmail());
        if (hv.getMaLH() != null) etLop.setText(hv.getMaLH());

        builder.setView(view);

        // 4. Xử lý nút Lưu
        builder.setPositiveButton("Lưu thay đổi", (dialog, which) -> {
            // Lấy dữ liệu mới
            String newTen = etTen.getText() != null ? etTen.getText().toString().trim() : "";
            String newEmail = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String newLop = etLop.getText() != null ? etLop.getText().toString().trim() : "";

            // Cập nhật object
            hv.setTenHV(newTen);
            hv.setEmail(newEmail);
            hv.setMaLH(newLop);

            // Lưu vào Database (chạy nền)
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

        // 1. Nạp layout từ file dialog_confirm_delete.xml
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_delete, null);

        // 2. Ánh xạ TextView để set thông báo
        TextView tvMessage = view.findViewById(R.id.tvConfirmMessage);
        tvMessage.setText("Bạn có chắc muốn xóa học viên " + hv.getTenHV() + " khỏi lớp " + hv.getMaLH() + "?");

        builder.setView(view);

        // 3. Xử lý nút Xóa
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