package com.example.studentportalapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentportalapp.adapter.TeacherAdapter;
import com.example.studentportalapp.data.TeacherItem;
import com.example.studentportalapp.databinding.ActivityTeacherManageBinding;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.GiaoVien;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TeacherManageActivity extends AppCompatActivity {

    private ActivityTeacherManageBinding binding;
    private AppDatabase db;
    private TeacherAdapter adapter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeacherManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getDatabase(getApplicationContext());

        adapter = new TeacherAdapter(new ArrayList<>());
        binding.rvTeacherList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTeacherList.setAdapter(adapter);

        loadTeacherData();
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {

            finish();

        });

    }

    private void loadTeacherData() {
        executor.execute(() -> {
            List<TeacherItem> list = db.giaoVienDao().getAllTeacherItems();
            runOnUiThread(() -> adapter.setData(list));
        });
    }
}
