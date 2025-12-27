package com.example.studentportalapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportalapp.adapter.GradeAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.BaiTap;
import com.example.studentportalapp.data.Entity.Diem;
import com.example.studentportalapp.data.Entity.HocVien;
import com.example.studentportalapp.data.Entity.LopHoc;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class GradeActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private Spinner spinnerClasses;
    private Spinner spinnerAssignments;
    private AppDatabase db;
    private String currentMaTK;
    private String realMaHV;
    private List<LopHoc> listAllLop;
    private String userRole;
    private List<Diem> currentClassGrades = new ArrayList<>();
    private List<BaiTap> currentClassAssignments = new ArrayList<>();
    private String currentSelectedMaBT = "ALL";
    private String targetClassIdFromNoti = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_grade;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = AppDatabase.getDatabase(getApplicationContext());
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentMaTK = prefs.getString("KEY_USER_ID", "");
        userRole = prefs.getString("KEY_ROLE", "");

        if (getIntent().hasExtra("TARGET_CLASS_ID")) {
            targetClassIdFromNoti = getIntent().getStringExtra("TARGET_CLASS_ID");
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar_grade);
        toolbar.setNavigationOnClickListener(v -> finish());
        if ("GIAOVIEN".equals(userRole)) {
            toolbar.setTitle("Quản Lý Điểm Số");
        }

        spinnerClasses = findViewById(R.id.spinner_classes);
        spinnerAssignments = findViewById(R.id.spinner_assignments);
        recyclerView = findViewById(R.id.rv_grades);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initData();
    }

    private void initData() {
        if ("GIAOVIEN".equals(userRole)) {
            loadClassesForTeacher();
        } else {
            findRealStudentId();
        }
    }

    private void findRealStudentId() {
        Executors.newSingleThreadExecutor().execute(() -> {
            HocVien hv = db.hocVienDao().getByMaTKSync(currentMaTK);
            if (hv != null) {
                realMaHV = hv.getMaHV();
                runOnUiThread(() -> loadClassesForStudent());
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Không tìm thấy thông tin học viên!", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void loadClassesForStudent() {
        Executors.newSingleThreadExecutor().execute(() -> {
            listAllLop = db.thamGiaDao().getClassesByStudent(currentMaTK);
            runOnUiThread(() -> setupClassSpinner(listAllLop));
        });
    }

    private void loadClassesForTeacher() {
        Executors.newSingleThreadExecutor().execute(() -> {
            listAllLop = db.lopHocDao().getClassesByTeacher(currentMaTK);
            runOnUiThread(() -> setupClassSpinner(listAllLop));
        });
    }

    private void setupClassSpinner(List<LopHoc> listLop) {
        if (listLop == null || listLop.isEmpty()) {
            Toast.makeText(this, "Không có lớp học nào.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> tenLopList = new ArrayList<>();
        int selectedPosition = 0;

        for (int i = 0; i < listLop.size(); i++) {
            LopHoc lh = listLop.get(i);
            tenLopList.add(lh.TenLH + " (" + lh.MaLH + ")");

            if (targetClassIdFromNoti != null && lh.MaLH.equals(targetClassIdFromNoti)) {
                selectedPosition = i;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, tenLopList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClasses.setAdapter(adapter);
        spinnerClasses.setSelection(selectedPosition);

        spinnerClasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMaLH = listLop.get(position).MaLH;
                loadAssignmentsForClass(selectedMaLH);
                loadGradesForClass(selectedMaLH);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadAssignmentsForClass(String maLH) {
        db.baiTapDao().getByLop(maLH).observe(this, listBT -> {
            currentClassAssignments = listBT != null ? listBT : new ArrayList<>();
            
            List<String> assignmentNames = new ArrayList<>();
            assignmentNames.add("Tất cả bài tập");

            for (BaiTap bt : currentClassAssignments) {
                assignmentNames.add(bt.TenBT);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, assignmentNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAssignments.setAdapter(adapter);

            spinnerAssignments.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        currentSelectedMaBT = "ALL";
                    } else {
                        currentSelectedMaBT = currentClassAssignments.get(position - 1).MaBT;
                    }
                    filterAndDisplayGrades();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        });
    }

    private void loadGradesForClass(String maLH) {
        if ("GIAOVIEN".equals(userRole)) {
            db.diemDao().getAll().observe(this, listDiem -> {
                if (listDiem == null) listDiem = new ArrayList<>();
                processGradesForClass(listDiem, maLH);
            });
        } else {
            db.diemDao().getByHocVien(realMaHV).observe(this, listDiem -> {
                if (listDiem == null) listDiem = new ArrayList<>();
                processGradesForClass(listDiem, maLH);
            });
        }
    }

    private void processGradesForClass(List<Diem> allGrades, String maLH) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Diem> filteredList = new ArrayList<>();
            for (Diem d : allGrades) {
                if (isAssignmentInClass(d.MaBT, maLH)) {
                    filteredList.add(d);
                }
            }
            
            currentClassGrades = filteredList;
            runOnUiThread(this::filterAndDisplayGrades);
        });
    }

    private void filterAndDisplayGrades() {
        List<Diem> finalDisplayList = new ArrayList<>();

        if ("ALL".equals(currentSelectedMaBT)) {
            finalDisplayList.addAll(currentClassGrades);
        } else {
            for (Diem d : currentClassGrades) {
                if (d.MaBT.equals(currentSelectedMaBT)) {
                    finalDisplayList.add(d);
                }
            }
        }

        GradeAdapter adapter = new GradeAdapter(this, finalDisplayList);
        recyclerView.setAdapter(adapter);
    }

    private boolean isAssignmentInClass(String maBT, String maLH) {
        for (BaiTap bt : currentClassAssignments) {
            if (bt.MaBT.equals(maBT)) {
                return true;
            }
        }

        BaiTap bt = db.baiTapDao().getByIdSync(maBT);
        return bt != null && maLH.equals(bt.MaLH);
    }
}
