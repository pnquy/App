package com.example.studentportalapp;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.adapter.StatsStudentAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.Diem;
import com.example.studentportalapp.data.Entity.HocVien;
import com.example.studentportalapp.model.StatsStudentItem;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AssignmentStatsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppDatabase db;
    private String maBT, tenBT, maLH;

    // View thống kê
    private TextView tvSubmissionRate;
    private ProgressBar progressSubmission;
    private ProgressBar progGioi, progKha, progTB, progYeu;
    private TextView tvCountGioi, tvCountKha, tvCountTB, tvCountYeu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_stats);

        maBT = getIntent().getStringExtra("MA_BT");
        tenBT = getIntent().getStringExtra("TEN_BT");
        maLH = getIntent().getStringExtra("MA_LH");

        MaterialToolbar toolbar = findViewById(R.id.toolbarAssignmentStats);
        toolbar.setTitle(tenBT);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Ánh xạ View thống kê
        tvSubmissionRate = findViewById(R.id.tvSubmissionRate);
        progressSubmission = findViewById(R.id.progressSubmission);
        progGioi = findViewById(R.id.progGioi);
        progKha = findViewById(R.id.progKha);
        progTB = findViewById(R.id.progTB);
        progYeu = findViewById(R.id.progYeu);
        tvCountGioi = findViewById(R.id.tvCountGioi);
        tvCountKha = findViewById(R.id.tvCountKha);
        tvCountTB = findViewById(R.id.tvCountTB);
        tvCountYeu = findViewById(R.id.tvCountYeu);

        TextView tvTitle = findViewById(R.id.tvAssignmentTitle);
        tvTitle.setText("Danh sách chi tiết (" + tenBT + ")");

        recyclerView = findViewById(R.id.rvStatsStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = AppDatabase.getDatabase(getApplicationContext());
        loadData();
    }

    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // 1. Lấy dữ liệu danh sách sinh viên
            List<String> studentIds = db.thamGiaDao().getStudentIdsByClass(maLH);
            List<HocVien> allStudents = new ArrayList<>();
            for(String id : studentIds) {
                HocVien hv = db.hocVienDao().getByIdSync(id);
                if(hv != null) allStudents.add(hv);
            }

            List<Diem> scores = db.diemDao().getByBaiTapSync(maBT);

            List<StatsStudentItem> displayList = new ArrayList<>();
            for (HocVien hv : allStudents) {
                double score = 0;
                boolean hasScore = false;

                for (Diem d : scores) {
                    if (d.MaHV.equals(hv.getMaHV())) {
                        score = d.SoDiem;
                        hasScore = true;
                        break;
                    }
                }
                displayList.add(new StatsStudentItem(hv, score, hasScore));
            }

            // 2. Tính toán thống kê
            // -- Tỷ lệ nộp bài --
            int totalStudents = studentIds.size();
            int totalSubmissions = db.nopBaiDao().countSubmissionsForAssignment(maBT);
            int subPercent = (totalStudents > 0) ? (int)((totalSubmissions * 100.0f) / totalStudents) : 0;

            // -- Phổ điểm (Dựa trên danh sách điểm đã lấy scores) --
            int countGioi = 0, countKha = 0, countTB = 0, countYeu = 0;
            int totalGraded = scores.size(); // Số lượng bài đã chấm

            for (Diem d : scores) {
                if (d.SoDiem >= 8.5) countGioi++;
                else if (d.SoDiem >= 7.0) countKha++;
                else if (d.SoDiem >= 5.0) countTB++;
                else countYeu++;
            }

            // Cập nhật UI
            int finalCountGioi = countGioi;
            int finalCountKha = countKha;
            int finalCountTB = countTB;
            int finalCountYeu = countYeu;

            runOnUiThread(() -> {
                // Set dữ liệu List
                StatsStudentAdapter adapter = new StatsStudentAdapter(displayList);
                recyclerView.setAdapter(adapter);

                // Set dữ liệu Thống kê
                tvSubmissionRate.setText(totalSubmissions + "/" + totalStudents + " (" + subPercent + "%)");
                progressSubmission.setProgress(subPercent);

                int maxProgress = (totalGraded > 0) ? totalGraded : 1;

                progGioi.setMax(maxProgress);
                progGioi.setProgress(finalCountGioi);
                tvCountGioi.setText(String.valueOf(finalCountGioi));

                progKha.setMax(maxProgress);
                progKha.setProgress(finalCountKha);
                tvCountKha.setText(String.valueOf(finalCountKha));

                progTB.setMax(maxProgress);
                progTB.setProgress(finalCountTB);
                tvCountTB.setText(String.valueOf(finalCountTB));

                progYeu.setMax(maxProgress);
                progYeu.setProgress(finalCountYeu);
                tvCountYeu.setText(String.valueOf(finalCountYeu));
            });
        });
    }
}