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
import com.example.studentportalapp.model.CourseViewItem;

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
        View btnProfile = findViewById(R.id.quickActionProfile);
        View btnSeeAllCourses = findViewById(R.id.btnSeeAllCourses);

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

        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }

        if (btnSeeAllCourses != null) {
            btnSeeAllCourses.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, AllCoursesActivity.class);
                startActivity(intent);
            });
        }

        if (btnNoti != null) {
            btnNoti.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);
            });
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

            List<CourseViewItem> courseViewItems = new ArrayList<>();
            if (listLop != null) {
                for (LopHoc lop : listLop) {
                    String progressText = "";
                    int progressValue = 0;

                    if ("HOCVIEN".equals(currentUserRole)) {
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
                        com.example.studentportalapp.data.Entity.BaiTap latestBT = db.baiTapDao().getLatestAssignment(lop.MaLH);
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
                    courseViewItems.add(new CourseViewItem(lop, progressText, progressValue));
                }
            }

            runOnUiThread(() -> {
                if (recyclerView == null) return;

                if (courseViewItems.isEmpty()) {
                    Toast.makeText(HomeActivity.this, "Chưa có lớp học nào.", Toast.LENGTH_SHORT).show();
                }

                UserCourseAdapter adapter = new UserCourseAdapter(HomeActivity.this, courseViewItems, lopHoc -> {
                    SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
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