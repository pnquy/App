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

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        loadHocVien();
    }

    private void loadHocVien() {
        db.hocVienDao().getAll().observe(this, listHV -> {

            executor.execute(() -> {
                List<com.example.studentportalapp.model.StudentItem> displayList = new ArrayList<>();

                if (listHV != null) {
                    for (HocVien hv : listHV) {
                        List<String> classIds = db.thamGiaDao().getClassIdsByStudent(hv.getMaHV());

                        String classNames = "";
                        if (classIds != null && !classIds.isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < classIds.size(); i++) {
                                sb.append(classIds.get(i));
                                if (i < classIds.size() - 1) sb.append(", ");
                            }
                            classNames = sb.toString();
                        }

                        displayList.add(new com.example.studentportalapp.model.StudentItem(hv, classNames));
                    }
                }

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

    private void showEditDialog(HocVien hv) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_student, null);

        TextInputEditText etTen = view.findViewById(R.id.etEditTenHV);
        TextInputEditText etEmail = view.findViewById(R.id.etEditEmailHV);
        TextInputEditText etLop = view.findViewById(R.id.etEditLopHV);

        if (hv.getTenHV() != null) etTen.setText(hv.getTenHV());
        if (hv.getEmail() != null) etEmail.setText(hv.getEmail());

        etLop.setText("Đang tải danh sách lớp...");
        etLop.setEnabled(false);
        etLop.setFocusable(false);

        executor.execute(() -> {
            List<String> classes = db.thamGiaDao().getClassIdsByStudent(hv.getMaHV());

            String displayStr;
            if (classes == null || classes.isEmpty()) {
                displayStr = "Chưa tham gia lớp nào";
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < classes.size(); i++) {
                    sb.append(classes.get(i));
                    if (i < classes.size() - 1) sb.append(", ");
                }
                displayStr = sb.toString();
            }

            runOnUiThread(() -> etLop.setText(displayStr));
        });

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