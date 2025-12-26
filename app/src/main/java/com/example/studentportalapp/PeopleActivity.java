package com.example.studentportalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportalapp.adapter.PeopleAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.BaiTap;
import com.example.studentportalapp.data.Entity.Diem;
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
    private String userRole;

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
        userRole = prefs.getString("KEY_ROLE", "");

        rvProf = findViewById(R.id.recyclerViewProfessors);
        rvStud = findViewById(R.id.recyclerViewStudents);
        tvCount = findViewById(R.id.txtStudentCount);
        View btnHomeLogo = findViewById(R.id.btnHomeLogo);

        if (btnHomeLogo != null) {
            btnHomeLogo.setOnClickListener(v -> {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }

        rvProf.setLayoutManager(new LinearLayoutManager(this));
        rvStud.setLayoutManager(new LinearLayoutManager(this));

        loadPeople();
        View btnNotiHeader = findViewById(R.id.btnNotiHeader);

        if (btnNotiHeader != null) {
            btnNotiHeader.setOnClickListener(v -> {
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
            });
        }
    }

    private void loadPeople() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Person> listGV = new ArrayList<>();
            List<Person> listHV = new ArrayList<>();

            LopHoc lop = db.lopHocDao().getByIdSync(currentMaLH);
            if (lop != null) {
                List<GiaoVien> allGV = db.giaoVienDao().getAllSync();
                for (GiaoVien gv : allGV) {
                    if (gv.getMaGV().equals(lop.MaGV)) {
                        listGV.add(new Person(gv.getMaGV(), gv.getTenGV(), "Giáo viên chủ nhiệm"));
                        break;
                    }
                }
            }

            List<String> studentIds = db.thamGiaDao().getStudentIdsByClass(currentMaLH);
            List<HocVien> allHV = db.hocVienDao().getAllSync();

            for (HocVien hv : allHV) {
                if (studentIds.contains(hv.getMaHV())) {
                    listHV.add(new Person(hv.getMaHV(), hv.getTenHV(), "Học viên"));
                }
            }

            runOnUiThread(() -> {
                rvProf.setAdapter(new PeopleAdapter(listGV, null));

                rvStud.setAdapter(new PeopleAdapter(listHV, person -> {
                    if ("GIAOVIEN".equals(userRole)) {
                        showStudentGradesDialog(person);
                    }
                }));
                tvCount.setText("Sĩ số: " + listHV.size());
            });
        });
    }

    private void showStudentGradesDialog(Person student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bảng điểm: " + student.getName());

        ListView listView = new ListView(this);
        builder.setView(listView);

        Executors.newSingleThreadExecutor().execute(() -> {
            List<BaiTap> assignments = db.baiTapDao().getByLopSync(currentMaLH);
            List<Diem> gradesSync = db.diemDao().getByHocVienSync(student.getId());

            List<String> displayList = new ArrayList<>();

            if (assignments != null) {
                for (BaiTap bt : assignments) {
                    String score = "-";

                    if (gradesSync != null) {
                        for (Diem d : gradesSync) {
                            if (d.MaBT.equals(bt.MaBT)) {
                                score = String.valueOf(d.SoDiem);
                                break;
                            }
                        }
                    }
                    displayList.add(bt.TenBT + " : " + score);
                }
            }

            runOnUiThread(() -> {
                if (displayList.isEmpty()) {
                    displayList.add("Chưa có bài tập nào.");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
                listView.setAdapter(adapter);
            });
        });

        builder.setPositiveButton("Đóng", null);
        builder.show();
    }
}