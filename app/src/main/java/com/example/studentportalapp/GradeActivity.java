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
    private AppDatabase db;
    private String currentMaTK;
    private String realMaHV;
    private List<LopHoc> listAllLop;

    // Biến để lưu mã lớp được truyền từ thông báo (nếu có)
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

        // Nhận dữ liệu từ Intent (nếu mở từ thông báo)
        if (getIntent().hasExtra("TARGET_CLASS_ID")) {
            targetClassIdFromNoti = getIntent().getStringExtra("TARGET_CLASS_ID");
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar_grade);
        toolbar.setNavigationOnClickListener(v -> finish());

        spinnerClasses = findViewById(R.id.spinner_classes);
        recyclerView = findViewById(R.id.rv_grades);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findRealStudentId();
    }

    private void findRealStudentId() {
        Executors.newSingleThreadExecutor().execute(() -> {
            HocVien hv = db.hocVienDao().getByMaTKSync(currentMaTK);
            if (hv != null) {
                realMaHV = hv.getMaHV();
                runOnUiThread(this::loadClasses);
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Không tìm thấy thông tin học viên!", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void loadClasses() {
        Executors.newSingleThreadExecutor().execute(() -> {
            listAllLop = db.thamGiaDao().getClassesByStudent(currentMaTK);
            
            runOnUiThread(() -> {
                if (listAllLop == null || listAllLop.isEmpty()) {
                    Toast.makeText(this, "Bạn chưa tham gia lớp học nào.", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> tenLopList = new ArrayList<>();
                int selectedPosition = 0;

                for (int i = 0; i < listAllLop.size(); i++) {
                    LopHoc lh = listAllLop.get(i);
                    tenLopList.add(lh.TenLH + " (" + lh.MaLH + ")");

                    // Nếu có mã lớp từ thông báo, ta tìm vị trí của nó
                    if (targetClassIdFromNoti != null && lh.MaLH.equals(targetClassIdFromNoti)) {
                        selectedPosition = i;
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                        android.R.layout.simple_spinner_item, tenLopList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClasses.setAdapter(adapter);

                // Tự động chọn lớp từ thông báo
                spinnerClasses.setSelection(selectedPosition);

                spinnerClasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedMaLH = listAllLop.get(position).MaLH;
                        loadGradesByClass(selectedMaLH);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            });
        });
    }

    private void loadGradesByClass(String maLH) {
        db.diemDao().getByHocVien(realMaHV).observe(this, listDiem -> {
            if (listDiem == null) {
                recyclerView.setAdapter(new GradeAdapter(this, new ArrayList<>()));
                return;
            }

            Executors.newSingleThreadExecutor().execute(() -> {
                List<Diem> filteredList = new ArrayList<>();
                for (Diem d : listDiem) {
                    if (isAssignmentInClass(d.MaBT, maLH)) {
                        filteredList.add(d);
                    }
                }

                runOnUiThread(() -> {
                    GradeAdapter adapter = new GradeAdapter(this, filteredList);
                    recyclerView.setAdapter(adapter);
                });
            });
        });
    }

    private boolean isAssignmentInClass(String maBT, String maLH) {
        com.example.studentportalapp.data.Entity.BaiTap bt = db.baiTapDao().getByIdSync(maBT);
        return bt != null && maLH.equals(bt.MaLH);
    }
}
