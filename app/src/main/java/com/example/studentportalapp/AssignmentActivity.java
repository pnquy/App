package com.example.studentportalapp;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.adapter.AssignmentAdapter;
import com.example.studentportalapp.model.Assignment;
import java.util.ArrayList;
import java.util.List;

// --- Imports được thêm vào ---
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.View;
import android.content.Intent; // Thêm Intent để mở Activity mới

public class AssignmentActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_assignment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_assignment);

        // --- Ẩn/Hiện và Xử lý Click FAB ---
        // 1. Tìm nút FAB
        FloatingActionButton fabAddAssignment = findViewById(R.id.fab_add_assignment);

        // 2. Lấy vai trò người dùng (Giả định)
        String userRole = getUserRoleFromLogin();

        // 3. Ẩn/Hiện nút FAB dựa trên vai trò
        if (userRole.equals("teacher")) {
            fabAddAssignment.setVisibility(View.VISIBLE); // Hiện nút cho giáo viên

            // 4. Gán sự kiện click cho FAB
            fabAddAssignment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Mở màn hình Thêm Bài Tập (AddAssignmentActivity)
                    Intent intent = new Intent(AssignmentActivity.this, AddAssignmentActivity.class);
                    startActivity(intent);
                }
            });

        } else {
            fabAddAssignment.setVisibility(View.GONE); // Ẩn nút đi với học viên
        }


        RecyclerView recyclerViewAssignments = findViewById(R.id.recyclerViewAssignments);
        recyclerViewAssignments.setLayoutManager(new LinearLayoutManager(this));

        List<Assignment> assignmentList = new ArrayList<>();
        assignmentList.add(new Assignment("Task 8 - Group Work", "100 points", "View", "James Gosling", "July 13, 2021"));
        assignmentList.add(new Assignment("Task 7 - Group Work", "100 points", "Unsubmit", "James Gosling", "July 13, 2021"));
        assignmentList.add(new Assignment("Task 6 - Midterm", "80 points", "View", "James Gosling", "July 10, 2021"));
        assignmentList.add(new Assignment("Task 5 - Review", "60 points", "View", "James Gosling", "July 7, 2021"));
        assignmentList.add(new Assignment("Task 4 - Java IO", "90 points", "Unsubmit", "James Gosling", "July 5, 2021"));

        AssignmentAdapter adapter = new AssignmentAdapter(this, assignmentList);
        recyclerViewAssignments.setAdapter(adapter);

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