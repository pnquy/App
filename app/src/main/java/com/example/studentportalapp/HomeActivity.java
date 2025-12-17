package com.example.studentportalapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportalapp.adapter.UserCourseAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.LopHoc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends BaseActivity {

    private TextView tvWelcome;
    private RecyclerView recyclerView;

    // Database
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Biến lưu thông tin phiên đăng nhập
    private String currentUserId;
    private String currentUserRole;
    private String currentUserName;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Khởi tạo Database
        db = AppDatabase.getDatabase(getApplicationContext());

        // 2. Lấy thông tin User từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        currentUserId = prefs.getString("KEY_USER_ID", "");
        currentUserRole = prefs.getString("KEY_ROLE", "");
        currentUserName = prefs.getString("KEY_NAME", "User");

        // 3. Ánh xạ View (Khớp với layout activity_home.xml MỚI)
        tvWelcome = findViewById(R.id.tvWelcomeUser);
        recyclerView = findViewById(R.id.recyclerUserCourses);

        // --- SỬA LỖI Ở ĐÂY: Dùng đúng ID của layout mới ---
        View btnLogout = findViewById(R.id.quickActionLogout);
        View btnNoti = findViewById(R.id.btnNoti);

        // 4. Set dữ liệu hiển thị
        if (tvWelcome != null) {
            tvWelcome.setText(currentUserName);
        }

        // 5. Cấu hình RecyclerView (1 cột dọc cho đẹp)
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            loadUserCourses();
        }

        // 6. Xử lý sự kiện Click (Kiểm tra null để tránh Crash)
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> showLogoutDialog());
        }

        if (btnNoti != null) {
            btnNoti.setOnClickListener(v ->
                    Toast.makeText(this, "Không có thông báo mới", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void loadUserCourses() {
        executor.execute(() -> {
            List<LopHoc> listLop = new ArrayList<>();

            if ("HOCVIEN".equals(currentUserRole)) {
                listLop = db.thamGiaDao().getClassesByStudent(currentUserId);
            } else if ("GIAOVIEN".equals(currentUserRole)) {
                listLop = db.lopHocDao().getClassesByTeacher(currentUserId);
            }

            List<LopHoc> finalList = listLop;

            runOnUiThread(() -> {
                // Kiểm tra null lần nữa cho chắc chắn
                if (recyclerView == null) return;

                if (finalList == null || finalList.isEmpty()) {
                    // Có thể hiện 1 textview thông báo trống nếu muốn
                    Toast.makeText(HomeActivity.this, "Chưa có lớp học nào.", Toast.LENGTH_SHORT).show();
                }

                UserCourseAdapter adapter = new UserCourseAdapter(HomeActivity.this, finalList, lopHoc -> {
                    Toast.makeText(HomeActivity.this, "Đã chọn: " + lopHoc.TenLH, Toast.LENGTH_SHORT).show();
                });
                recyclerView.setAdapter(adapter);
            });
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn muốn đăng xuất khỏi tài khoản?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserCourses();
    }
}