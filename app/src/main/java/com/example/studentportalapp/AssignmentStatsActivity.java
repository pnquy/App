package com.example.studentportalapp;

import android.os.Bundle;
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

        TextView tvTitle = findViewById(R.id.tvAssignmentTitle);
        tvTitle.setText("Bảng điểm: " + tenBT);

        recyclerView = findViewById(R.id.rvStatsStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = AppDatabase.getDatabase(getApplicationContext());
        loadData();
    }

    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
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

            runOnUiThread(() -> {
                StatsStudentAdapter adapter = new StatsStudentAdapter(displayList);
                recyclerView.setAdapter(adapter);
            });
        });
    }
}