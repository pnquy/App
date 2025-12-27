package com.example.studentportalapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.adapter.UserCourseAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.BaiTap; // Import thêm
import com.example.studentportalapp.data.Entity.LopHoc;
import com.example.studentportalapp.model.CourseViewItem; // Import thêm
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class AllCoursesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserCourseAdapter adapter;
    private AppDatabase db;
    private List<CourseViewItem> fullList = new ArrayList<>();
    private String currentUserId, userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_courses);

        db = AppDatabase.getDatabase(getApplicationContext());
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUserId = prefs.getString("KEY_USER_ID", "");
        userRole = prefs.getString("KEY_ROLE", "");

        MaterialToolbar toolbar = findViewById(R.id.toolbarAllCourses);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.rvAllCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SearchView searchView = findViewById(R.id.searchViewCourses);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        loadData();
    }

    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<LopHoc> rawList;

            if ("HOCVIEN".equals(userRole)) {
                rawList = db.thamGiaDao().getClassesByStudent(currentUserId);
            } else {
                rawList = db.lopHocDao().getClassesByTeacher(currentUserId);
            }

            List<CourseViewItem> calculatedList = new ArrayList<>();

            for (LopHoc lop : rawList) {
                String progressText = "";
                int progressValue = 0;

                if ("HOCVIEN".equals(userRole)) {
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
                calculatedList.add(new CourseViewItem(lop, progressText, progressValue));
            }

            SharedPreferences accessPrefs = getSharedPreferences("AccessHistory", MODE_PRIVATE);
            Collections.sort(calculatedList, (o1, o2) -> {
                long t1 = accessPrefs.getLong("LAST_ACCESS_" + o1.lopHoc.MaLH, 0);
                long t2 = accessPrefs.getLong("LAST_ACCESS_" + o2.lopHoc.MaLH, 0);
                return Long.compare(t2, t1);
            });

            fullList = calculatedList;

            runOnUiThread(() -> {
                adapter = new UserCourseAdapter(this, fullList, this::onCourseClick);
                recyclerView.setAdapter(adapter);
            });
        });
    }

    private void filter(String text) {
        List<CourseViewItem> filteredList = new ArrayList<>();
        String query = text.toLowerCase();

        for (CourseViewItem item : fullList) {
            if (item.lopHoc.TenLH.toLowerCase().contains(query) ||
                    item.lopHoc.MaLH.toLowerCase().contains(query)) {
                filteredList.add(item);
            }
        }
        if (adapter != null) {
            adapter.updateList(filteredList);
        }
    }

    private void onCourseClick(LopHoc lopHoc) {
        SharedPreferences prefs = getSharedPreferences("AccessHistory", MODE_PRIVATE);
        prefs.edit().putLong("LAST_ACCESS_" + lopHoc.MaLH, System.currentTimeMillis()).apply();

        SharedPreferences session = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();
        editor.putString("CURRENT_CLASS_ID", lopHoc.MaLH);
        editor.putString("CURRENT_CLASS_NAME", lopHoc.TenLH);
        editor.apply();

        Intent intent = new Intent(this, CourseActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}