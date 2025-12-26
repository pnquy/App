package com.example.studentportalapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.studentportalapp.model.CourseViewItem;
import com.example.studentportalapp.data.Entity.BaiTap;
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
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
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

        db = AppDatabase.getDatabase(getApplicationContext());

        SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        currentUserId = prefs.getString("KEY_USER_ID", "");
        currentUserRole = prefs.getString("KEY_ROLE", "");
        currentUserName = prefs.getString("KEY_NAME", "User");

        tvWelcome = findViewById(R.id.tvWelcomeUser);
        recyclerView = findViewById(R.id.recyclerUserCourses);
        View btnLogout = findViewById(R.id.quickActionLogout);
        View btnGrades = findViewById(R.id.quickActionGrades);
        View btnTKB = findViewById(R.id.quickActionTKB);
        View btnNoti = findViewById(R.id.btnNoti);
        View btnSeeAll = findViewById(R.id.btnSeeAllCourses);
        if (btnSeeAll != null) {
            btnSeeAll.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, AllCoursesActivity.class);
                startActivity(intent);
            });
        }
        if (tvWelcome != null) {
            tvWelcome.setText(currentUserName);
        }

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            loadUserCourses();
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> showLogoutDialog());
        }

        // Xử lý sự kiện click mở màn hình Xem Điểm
        if (btnGrades != null) {
            btnGrades.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, GradeActivity.class);
                startActivity(intent);
            });
        }

        if (btnTKB != null) {
            btnTKB.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ToDoActivity.class);
                startActivity(intent);
            });
        }

        // SỬA: Chuyển hướng sang màn hình thông báo thay vì hiện Toast
        if (btnNoti != null) {
            btnNoti.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);
            });
        }
    }

    private void loadUserCourses() {
        executor.execute(() -> {
            List<LopHoc> rawList = new ArrayList<>();
            List<CourseViewItem> displayList = new ArrayList<>();

            // 1. Lấy danh sách lớp
            if ("HOCVIEN".equals(currentUserRole)) {
                rawList = db.thamGiaDao().getClassesByStudent(currentUserId);
            } else if ("GIAOVIEN".equals(currentUserRole)) {
                rawList = db.lopHocDao().getClassesByTeacher(currentUserId);
            }

            // 2. Tính toán thống kê cho từng lớp
            for (LopHoc lop : rawList) {
                String progressText = "";
                int progressValue = 0;

                if ("HOCVIEN".equals(currentUserRole)) {
                    // --- LOGIC HỌC VIÊN: Số bài đã làm / Tổng số bài ---
                    int totalAssignments = db.baiTapDao().countAssignmentsInClass(lop.MaLH);
                    int submittedCount = db.nopBaiDao().countSubmissionsByStudentInClass(currentUserId, lop.MaLH);

                    if (totalAssignments > 0) {
                        progressValue = (int) (((float) submittedCount / totalAssignments) * 100);
                        progressText = submittedCount + "/" + totalAssignments + " bài tập";
                    } else {
                        progressText = "Chưa có bài tập";
                        progressValue = 0;
                    }

                } else {
                    // --- LOGIC GIÁO VIÊN: % Lớp nộp bài tập mới nhất ---
                    BaiTap latestBT = db.baiTapDao().getLatestAssignment(lop.MaLH);

                    if (latestBT != null) {
                        int totalStudents = db.thamGiaDao().countStudentsByClass(lop.MaLH);
                        int totalSubmissions = db.nopBaiDao().countSubmissionsForAssignment(latestBT.MaBT);

                        if (totalStudents > 0) {
                            progressValue = (int) (((float) totalSubmissions / totalStudents) * 100);
                            progressText = progressValue + "% đã nộp (" + latestBT.TenBT + ")";
                        } else {
                            progressText = "Chưa có học viên";
                            progressValue = 0;
                        }
                    } else {
                        progressText = "Chưa giao bài tập";
                        progressValue = 0;
                    }
                }

                displayList.add(new CourseViewItem(lop, progressText, progressValue));
            }

            // 3. Cập nhật UI
            runOnUiThread(() -> {
                if (recyclerView == null) return;

                // Lưu ý: UserCourseAdapter giờ nhận List<CourseViewItem>
                UserCourseAdapter adapter = new UserCourseAdapter(HomeActivity.this, displayList, lopHoc -> {
                    // Logic click giữ nguyên
                    SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    // Lưu lịch sử truy cập (cho AllCoursesActivity dùng)
                    SharedPreferences historyPrefs = getSharedPreferences("AccessHistory", Context.MODE_PRIVATE);
                    historyPrefs.edit().putLong("LAST_ACCESS_" + lopHoc.MaLH, System.currentTimeMillis()).apply();

                    editor.putString("CURRENT_CLASS_ID", lopHoc.MaLH);
                    editor.putString("CURRENT_CLASS_NAME", lopHoc.TenLH);
                    editor.apply();

                    Intent intent = new Intent(HomeActivity.this, CourseActivity.class);
                    startActivity(intent);
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
