package com.example.studentportalapp;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.adapter.CourseAdapter;
import com.example.studentportalapp.model.ActivityItem;
import java.util.ArrayList;

// --- Imports được thêm vào ---
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.View;
import android.content.Intent; // Thêm Intent để mở Activity mới

public class CourseActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_course;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- Ẩn/Hiện và Xử lý Click FAB ---
        // 1. Tìm nút FAB
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);

        // 2. Lấy vai trò người dùng (Giả định)
        // Cần thay thế hàm này bằng logic thật để lấy vai trò từ SharedPreferences hoặc Intent
        String userRole = getUserRoleFromLogin();

        // 3. Ẩn/Hiện nút FAB dựa trên vai trò
        if (userRole.equals("teacher")) {
            fabAdd.setVisibility(View.VISIBLE); // Hiện nút cho giáo viên

            // 4. Gán sự kiện click cho FAB
            fabAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Mở màn hình Thêm Bài Giảng (AddLectureActivity)
                    Intent intent = new Intent(CourseActivity.this, AddLectureActivity.class);
                    startActivity(intent);
                }
            });

        } else {
            fabAdd.setVisibility(View.GONE); // Ẩn nút đi với học viên
        }


        RecyclerView recyclerView = findViewById(R.id.rvActivityCourse);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<ActivityItem> activityList = new ArrayList<>();
        activityList.add(new ActivityItem("James Gosling", "July 13, 2021", "Java Stack Program", "JavaStack.docx", "26"));
        activityList.add(new ActivityItem("James Gosling", "July 12, 2021", "Data Structures - Lesson 6", "Lesson6.docx", "28"));
        activityList.add(new ActivityItem("Bjarne Stroustrup", "July 10, 2021", "C++ Template Programming", "C++Templates.pdf", "19"));
        activityList.add(new ActivityItem("Bjarne Stroustrup", "July 9, 2021", "C++ Basics", "C++.pdf", "25"));

        CourseAdapter adapter = new CourseAdapter(activityList);
        recyclerView.setAdapter(adapter);
    }

    // --- Hàm giả định được thêm vào ---
    // Cần thay thế hàm này bằng logic thật của bạn để lấy vai trò người dùng
    private String getUserRoleFromLogin() {
        // Ví dụ: Lấy từ SharedPreferences
        // SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        // return prefs.getString("USER_ROLE", "student"); // "student" là giá trị mặc định

        // Tạm thời trả về "teacher" để bạn kiểm tra nút FAB
        return "teacher";
    }
}