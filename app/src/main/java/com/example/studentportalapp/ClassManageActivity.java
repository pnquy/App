package com.example.studentportalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportalapp.adapter.ClassAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.GiaoVien;
import com.example.studentportalapp.data.Entity.HocVien;
import com.example.studentportalapp.data.Entity.LopHoc;
import com.example.studentportalapp.data.TeacherItem;
import com.example.studentportalapp.model.ClassDisplayItem;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClassManageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private List<TeacherItem> allTeachers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_manage);

        db = AppDatabase.getDatabase(getApplicationContext());
        recyclerView = findViewById(R.id.rvClassList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddClass).setOnClickListener(v -> showAddEditDialog(null));

        loadTeachers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClasses();
    }

    private void loadTeachers() {
        executor.execute(() -> {
            try {
                List<GiaoVien> teachers = db.giaoVienDao().getAllSync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void loadClasses() {
        db.lopHocDao().getAll().observe(this, lopHocList -> {
            executor.execute(() -> {
                List<ClassDisplayItem> displayList = new ArrayList<>();
                List<GiaoVien> currentTeachers = db.giaoVienDao().getAllSync();

                for (LopHoc lh : lopHocList) {
                    String tenGV = "Chưa phân công";
                    for (GiaoVien gv : currentTeachers) {
                        if (gv.getMaGV().equals(lh.MaGV)) {
                            tenGV = gv.getTenGV();
                            break;
                        }
                    }
                    int count = db.thamGiaDao().countStudentsByClass(lh.MaLH);
                    displayList.add(new ClassDisplayItem(lh, tenGV, count));
                }

                runOnUiThread(() -> {
                    ClassAdapter adapter = new ClassAdapter(displayList, new ClassAdapter.OnItemClickListener() {
                        @Override
                        public void onAddStudent(ClassDisplayItem item) {
                            showAddStudentToClassDialog(item.lopHoc);
                        }

                        @Override
                        public void onStats(ClassDisplayItem item) {
                            Intent intent = new Intent(ClassManageActivity.this, ClassStatsActivity.class);
                            intent.putExtra("MA_LH", item.lopHoc.MaLH);
                            intent.putExtra("TEN_LH", item.lopHoc.TenLH);
                            startActivity(intent);
                        }

                        @Override
                        public void onEdit(ClassDisplayItem item) {
                            showAddEditDialog(item.lopHoc);
                        }

                        @Override
                        public void onDelete(ClassDisplayItem item) {
                            showDeleteConfirm(item.lopHoc);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                });
            });
        });
    }

    private String[] teacherNames;
    private List<GiaoVien> teacherList;

    private void showAddEditDialog(LopHoc existingClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(existingClass == null ? "Thêm Lớp Mới" : "Sửa Lớp Học");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_class, null);

        TextInputEditText etMa = view.findViewById(R.id.etMaLH);
        TextInputEditText etTen = view.findViewById(R.id.etTenLH);
        TextInputEditText etGV = view.findViewById(R.id.etMaGV);

        etGV.setFocusable(false);
        etGV.setClickable(true);

        final String[] selectedMaGV = {existingClass != null ? existingClass.MaGV : null};

        executor.execute(() -> {
            teacherList = db.giaoVienDao().getAllSync();
            teacherNames = new String[teacherList.size()];
            for (int i = 0; i < teacherList.size(); i++) {
                teacherNames[i] = teacherList.get(i).getMaGV() + " - " + teacherList.get(i).getTenGV();
            }

            if (existingClass != null && existingClass.MaGV != null) {
                for (GiaoVien gv : teacherList) {
                    if (gv.getMaGV().equals(existingClass.MaGV)) {
                        runOnUiThread(() -> etGV.setText(gv.getMaGV() + " - " + gv.getTenGV()));
                        break;
                    }
                }
            }
        });

        etGV.setOnClickListener(v -> {
            if (teacherNames == null || teacherNames.length == 0) {
                Toast.makeText(this, "Chưa có giáo viên nào!", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(this)
                    .setTitle("Chọn Giáo Viên Phụ Trách")
                    .setItems(teacherNames, (dialog, which) -> {
                        GiaoVien selected = teacherList.get(which);
                        etGV.setText(selected.getMaGV() + " - " + selected.getTenGV());
                        selectedMaGV[0] = selected.getMaGV();
                    })
                    .show();
        });

        if (existingClass != null) {
            etMa.setText(existingClass.MaLH);
            etMa.setEnabled(false);
            etTen.setText(existingClass.TenLH);
        }

        builder.setView(view);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String ma = etMa.getText().toString().trim();
            String ten = etTen.getText().toString().trim();

            if (ma.isEmpty() || ten.isEmpty()) {
                Toast.makeText(this, "Thiếu thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            LopHoc lh = new LopHoc();
            lh.MaLH = ma;
            lh.TenLH = ten;
            lh.MaGV = selectedMaGV[0];

            executor.execute(() -> {
                if (existingClass == null) db.lopHocDao().insert(lh);
                else db.lopHocDao().update(lh);

                runOnUiThread(() -> {
                    loadClasses();
                    Toast.makeText(ClassManageActivity.this, "Lưu thành công!", Toast.LENGTH_SHORT).show();
                });
            });
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showAddStudentToClassDialog(LopHoc lh) {
        executor.execute(() -> {
            List<HocVien> allStudents = db.hocVienDao().getAllSync();
            List<String> enrolledStudentIds = db.thamGiaDao().getStudentIdsByClass(lh.MaLH);

            String[] studentNames = new String[allStudents.size()];
            boolean[] checkedItems = new boolean[allStudents.size()];

            List<String> selectedIds = new ArrayList<>();

            for (int i = 0; i < allStudents.size(); i++) {
                HocVien hv = allStudents.get(i);
                studentNames[i] = hv.getMaHV() + " - " + hv.getTenHV();

                if (enrolledStudentIds.contains(hv.getMaHV())) {
                    checkedItems[i] = true;
                    selectedIds.add(hv.getMaHV());
                } else {
                    checkedItems[i] = false;
                }
            }

            runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Chọn học viên vào lớp " + lh.TenLH);

                builder.setMultiChoiceItems(studentNames, checkedItems, (dialog, which, isChecked) -> {
                    String hvID = allStudents.get(which).getMaHV();
                    if (isChecked) {
                        selectedIds.add(hvID);
                    } else {
                        selectedIds.remove(hvID);
                    }
                });

                builder.setPositiveButton("Lưu", (dialog, which) -> {
                    saveStudentClassChanges(lh, enrolledStudentIds, selectedIds);
                });

                builder.setNegativeButton("Hủy", null);
                builder.show();
            });
        });
    }

    private void saveStudentClassChanges(LopHoc currentClass, List<String> oldIds, List<String> newIds) {
        executor.execute(() -> {
            for (String newId : newIds) {
                if (!oldIds.contains(newId)) {
                    db.thamGiaDao().insert(new com.example.studentportalapp.data.Entity.ThamGia(newId, currentClass.MaLH));
                }
            }

            for (String oldId : oldIds) {
                if (!newIds.contains(oldId)) {
                    db.thamGiaDao().removeStudentFromClass(oldId, currentClass.MaLH);
                }
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Đã cập nhật danh sách lớp!", Toast.LENGTH_SHORT).show();
                loadClasses();
            });
        });
    }

    private void showDeleteConfirm(LopHoc lh) {
        new AlertDialog.Builder(this)
                .setTitle("Cảnh báo")
                .setMessage("Xóa lớp " + lh.TenLH + " sẽ xóa lớp này khỏi danh sách học của tất cả học viên. Tiếp tục?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    executor.execute(() -> {
                        db.lopHocDao().delete(lh);
                        runOnUiThread(() -> {
                            loadClasses();
                            Toast.makeText(ClassManageActivity.this, "Đã xóa lớp thành công!", Toast.LENGTH_SHORT).show();
                        });
                    });
                })
                .setNegativeButton("Hủy", null).show();
    }
}