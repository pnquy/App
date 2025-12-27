package com.example.studentportalapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.adapter.StudentScoreAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.BaiTap;
import com.example.studentportalapp.data.Entity.Diem;
import com.example.studentportalapp.model.StudentScoreItem;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class StudentStatsActivity extends AppCompatActivity {

    private SimpleLineChart lineChart;
    private RecyclerView recyclerView;
    private AppDatabase db;
    private String currentMaHV, currentMaLH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_stats);

        db = AppDatabase.getDatabase(getApplicationContext());
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentMaHV = prefs.getString("KEY_USER_ID", "");
        currentMaLH = prefs.getString("CURRENT_CLASS_ID", "");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        lineChart = findViewById(R.id.lineChart);
        recyclerView = findViewById(R.id.rvComparison);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();
    }

    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // 1. Lấy danh sách điểm của mình trong lớp này (đã sắp xếp theo thời gian)
            List<Diem> myGrades = db.diemDao().getScoresByStudentInClass(currentMaHV, currentMaLH);

            // Danh sách điểm để vẽ biểu đồ
            List<Double> chartScores = new ArrayList<>();
            // Danh sách item so sánh
            List<StudentScoreItem> comparisonList = new ArrayList<>();

            for (Diem d : myGrades) {
                // Thêm vào dữ liệu biểu đồ
                chartScores.add(d.SoDiem);

                // Lấy thông tin bài tập
                BaiTap bt = db.baiTapDao().getByIdSync(d.MaBT);
                // Tính điểm trung bình của cả lớp cho bài tập này
                double classAvg = db.diemDao().getAverageScoreOfAssignment(d.MaBT);

                if (bt != null) {
                    comparisonList.add(new StudentScoreItem(bt.TenBT, d.SoDiem, classAvg));
                }
            }

            runOnUiThread(() -> {
                // Cập nhật biểu đồ
                lineChart.setScores(chartScores);

                // Cập nhật danh sách so sánh
                StudentScoreAdapter adapter = new StudentScoreAdapter(comparisonList);
                recyclerView.setAdapter(adapter);
            });
        });
    }
}