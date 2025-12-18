package com.example.studentportalapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.adapter.PeopleAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.GiaoVien;
import com.example.studentportalapp.data.Entity.HocVien;
import com.example.studentportalapp.data.Entity.LopHoc;
import com.example.studentportalapp.model.Person;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class PeopleActivity extends BaseActivity {

    private RecyclerView rvProf, rvStud;
    private TextView tvCount;
    private AppDatabase db;
    private String currentMaLH;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_people;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getDatabase(getApplicationContext());

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentMaLH = prefs.getString("CURRENT_CLASS_ID", "");

        rvProf = findViewById(R.id.recyclerViewProfessors);
        rvStud = findViewById(R.id.recyclerViewStudents);
        tvCount = findViewById(R.id.txtStudentCount);

        rvProf.setLayoutManager(new LinearLayoutManager(this));
        rvStud.setLayoutManager(new LinearLayoutManager(this));

        loadPeople();
    }

    private void loadPeople() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Person> listGV = new ArrayList<>();
            List<Person> listHV = new ArrayList<>();

            LopHoc lop = db.lopHocDao().getById(currentMaLH);
            if (lop != null) {
                List<GiaoVien> allGV = db.giaoVienDao().getAllSync();
                for (GiaoVien gv : allGV) {
                    if (gv.getMaGV().equals(lop.MaGV)) {
                        listGV.add(new Person(gv.getTenGV(), "Giáo viên chủ nhiệm"));
                        break;
                    }
                }
            }

            List<String> studentIds = db.thamGiaDao().getStudentIdsByClass(currentMaLH);
            List<HocVien> allHV = db.hocVienDao().getAllSync();

            for (HocVien hv : allHV) {
                if (studentIds.contains(hv.getMaHV())) {
                    listHV.add(new Person(hv.getTenHV(), "Học viên"));
                }
            }

            runOnUiThread(() -> {
                rvProf.setAdapter(new PeopleAdapter(listGV));
                rvStud.setAdapter(new PeopleAdapter(listHV));
                tvCount.setText("Sĩ số: " + listHV.size());
            });
        });
    }
}