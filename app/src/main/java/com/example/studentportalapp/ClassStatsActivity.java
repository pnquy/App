package com.example.studentportalapp;

import android.content.Intent; import android.os.Bundle; import androidx.appcompat.app.AppCompatActivity; import androidx.recyclerview.widget.LinearLayoutManager; import androidx.recyclerview.widget.RecyclerView; import com.example.studentportalapp.adapter.StatsAssignmentAdapter; import com.example.studentportalapp.data.AppDatabase; import com.example.studentportalapp.data.Entity.BaiTap; import com.example.studentportalapp.data.Entity.Diem; import com.example.studentportalapp.model.StatsAssignmentItem; import com.google.android.material.appbar.MaterialToolbar; import java.util.ArrayList; import java.util.List; import java.util.concurrent.Executors;

public class ClassStatsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SimpleBarChart barChart;
    private AppDatabase db;
    private String maLH, tenLH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_stats);

        maLH = getIntent().getStringExtra("MA_LH");
        tenLH = getIntent().getStringExtra("TEN_LH");

        MaterialToolbar toolbar = findViewById(R.id.toolbarClassStats);
        toolbar.setTitle("Thống kê: " + tenLH);
        toolbar.setNavigationOnClickListener(v -> finish());

        barChart = findViewById(R.id.barChart);
        recyclerView = findViewById(R.id.rvStatsAssignments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = AppDatabase.getDatabase(getApplicationContext());
        loadData();
    }

    private void loadData() {
        db.baiTapDao().getByLop(maLH).observe(this, listBaiTap -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                List<StatsAssignmentItem> statsList = new ArrayList<>();

                for (BaiTap bt : listBaiTap) {
                    List<Diem> listDiem = db.diemDao().getByBaiTapSync(bt.MaBT);

                    double totalScore = 0;
                    int count = 0;
                    for (Diem d : listDiem) {
                        totalScore += d.SoDiem;
                        count++;
                    }

                    double avg = (count > 0) ? (totalScore / count) : 0;
                    statsList.add(new StatsAssignmentItem(bt, avg, count));
                }

                runOnUiThread(() -> {
                    barChart.setData(statsList);

                    StatsAssignmentAdapter adapter = new StatsAssignmentAdapter(statsList, item -> {
                        Intent intent = new Intent(ClassStatsActivity.this, AssignmentStatsActivity.class);
                        intent.putExtra("MA_BT", item.baiTap.MaBT);
                        intent.putExtra("TEN_BT", item.baiTap.TenBT);
                        intent.putExtra("MA_LH", maLH);
                        startActivity(intent);
                    });
                    recyclerView.setAdapter(adapter);
                });
            });
        });
    }
}