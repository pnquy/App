package com.example.studentportalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportalapp.adapter.TeacherRankAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.databinding.ActivityAdminBinding;
import com.example.studentportalapp.model.TeacherRankItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private AppDatabase db;
    private ActivityResultLauncher<String> filePickerLauncher;

    private SimplePieChart pieChart;
    private TextView tvCountStudent, tvCountTeacher, tvTotalClasses, tvTotalAssignments;
    private RecyclerView rvTopTeachers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getDatabase(getApplicationContext());

        pieChart = findViewById(R.id.pieChartUser);
        tvCountStudent = findViewById(R.id.tvCountStudent);
        tvCountTeacher = findViewById(R.id.tvCountTeacher);
        tvTotalClasses = findViewById(R.id.tvTotalClasses);
        tvTotalAssignments = findViewById(R.id.tvTotalAssignments);
        rvTopTeachers = findViewById(R.id.rvTopTeachers);

        if (rvTopTeachers != null) {
            rvTopTeachers.setLayoutManager(new LinearLayoutManager(this));
        }

        View btnExportSQL = findViewById(R.id.btnExportSQL);
        View btnImportSQL = findViewById(R.id.btnImportSQL);
        View btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> showLogoutDialog());

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        performImport(uri);
                    }
                }
        );

        btnImportSQL.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Cảnh báo")
                    .setMessage("Import dữ liệu sẽ xóa toàn bộ dữ liệu hiện tại và thay thế bằng file mới. Bạn có chắc chắn không?")
                    .setPositiveButton("Chọn File", (dialog, which) -> {
                        filePickerLauncher.launch("*/*");
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        binding.btnCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AddUserActivity.class);
            startActivity(intent);
        });

        binding.btnManageTeacher.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, TeacherManageActivity.class);
            startActivity(intent);
        });

        binding.btnManageStudent.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, StudentManageActivity.class);
            startActivity(intent);
        });

        binding.btnManageClass.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ClassManageActivity.class));
        });

        btnExportSQL.setOnClickListener(v -> {
            Toast.makeText(this, "Đang xuất dữ liệu...", Toast.LENGTH_SHORT).show();
            executor.execute(() -> {
                try {
                    String path = DatabaseExporter.exportToSQL(AdminActivity.this, db);
                    runOnUiThread(() -> {
                        new AlertDialog.Builder(AdminActivity.this)
                                .setTitle("Xuất File Thành Công!")
                                .setMessage("File SQL đã được lưu tại:\n\n" + path + "\n\nBạn có thể kết nối máy tính để lấy file này.")
                                .setPositiveButton("OK", null)
                                .show();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(AdminActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
            });
        });

        loadAdminStats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAdminStats();
    }

    private void loadAdminStats() {
        executor.execute(() -> {
            int countHV = db.taiKhoanDao().countUsersByRole("HOCVIEN");
            int countGV = db.taiKhoanDao().countUsersByRole("GIAOVIEN");
            int countClass = db.lopHocDao().countAllClasses();
            int countAssign = db.baiTapDao().countAllAssignments();

            List<TeacherRankItem> topTeachers = db.giaoVienDao().getTopTeachersByClassCount();

            runOnUiThread(() -> {
                if (pieChart != null) {
                    pieChart.setData(countHV, countGV);
                }
                if (tvCountStudent != null) tvCountStudent.setText("Học viên: " + countHV);
                if (tvCountTeacher != null) tvCountTeacher.setText("Giáo viên: " + countGV);
                if (tvTotalClasses != null) tvTotalClasses.setText(String.valueOf(countClass));
                if (tvTotalAssignments != null) tvTotalAssignments.setText(String.valueOf(countAssign));

                if (rvTopTeachers != null && topTeachers != null && !topTeachers.isEmpty()) {
                    TeacherRankAdapter rankAdapter = new TeacherRankAdapter(topTeachers);
                    rvTopTeachers.setAdapter(rankAdapter);
                }
            });
        });
    }

    private void performImport(Uri uri) {
        Toast.makeText(this, "Đang khôi phục dữ liệu...", Toast.LENGTH_SHORT).show();
        executor.execute(() -> {
            try {
                DatabaseImporter.importFromSQL(this, db, uri);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Import thành công!", Toast.LENGTH_LONG).show();
                    loadAdminStats();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi Import: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performLogout() {
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}