package com.example.studentportalapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studentportalapp.databinding.ActivityAdminBinding;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AddUserActivity.class);
            startActivity(intent);
        });

        // click quản lý giáo viên
        binding.btnManageTeacher.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, TeacherManageActivity.class);
            startActivity(intent);
        });

        // click quản lý học viên
        binding.btnManageStudent.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, StudentManageActivity.class);
            startActivity(intent);
        });
    }
}
