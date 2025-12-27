package com.example.studentportalapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportalapp.adapter.TeacherAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.GiaoVien;
import com.example.studentportalapp.model.TeacherDisplayItem;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TeacherManageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_manage);

        db = AppDatabase.getDatabase(getApplicationContext());
        recyclerView = findViewById(R.id.recyclerTeacher);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        loadTeachers();
    }

    private void loadTeachers() {
        db.giaoVienDao().getAll().observe(this, listGV -> {
            executor.execute(() -> {
                List<TeacherDisplayItem> displayList = new ArrayList<>();

                for (GiaoVien gv : listGV) {
                    int count = db.lopHocDao().countClassesByTeacher(gv.getMaGV());
                    displayList.add(new TeacherDisplayItem(gv, count));
                }

                runOnUiThread(() -> {
                    TeacherAdapter adapter = new TeacherAdapter(this, displayList, new TeacherAdapter.OnItemClickListener() {
                        @Override
                        public void onEdit(TeacherDisplayItem item) {
                            showEditDialog(item.giaoVien);
                        }

                        @Override
                        public void onDelete(TeacherDisplayItem item) {
                            showDeleteConfirm(item.giaoVien);
                        }

                        @Override
                        public void onViewClasses(TeacherDisplayItem item) {
                            showClassesDialog(item.giaoVien);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                });
            });
        });
    }

    private void showClassesDialog(GiaoVien gv) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lớp dạy: " + gv.getTenGV());

        ListView listView = new ListView(this);
        builder.setView(listView);

        executor.execute(() -> {
            List<String> classNames = db.lopHocDao().getClassNamesByTeacher(gv.getMaGV());

            runOnUiThread(() -> {
                if (classNames.isEmpty()) {
                    Toast.makeText(this, "Giáo viên này chưa nhận lớp nào.", Toast.LENGTH_SHORT).show();
                    ArrayList<String> emptyList = new ArrayList<>();
                    emptyList.add("(Chưa có lớp)");
                    listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emptyList));
                } else {
                    listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, classNames));
                }
            });
        });

        builder.setPositiveButton("Đóng", null);
        builder.show();
    }

    private void showEditDialog(GiaoVien gv) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_student, null); // Tái sử dụng layout sửa

        TextInputEditText etTen = view.findViewById(R.id.etEditTenHV);
        TextInputEditText etEmail = view.findViewById(R.id.etEditEmailHV);
        TextInputEditText etMa = view.findViewById(R.id.etEditLopHV);

        etTen.setText(gv.getTenGV());
        etEmail.setText(gv.getEmail());

        etMa.setText("MSGV: " + gv.getMaGV());
        etMa.setEnabled(false);

        builder.setView(view);
        builder.setTitle("Sửa Giáo Viên");

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newTen = etTen.getText().toString();
            String newEmail = etEmail.getText().toString();

            gv.setTenGV(newTen);
            gv.setEmail(newEmail);

            executor.execute(() -> {
                db.giaoVienDao().update(gv);
                runOnUiThread(() -> Toast.makeText(this, "Đã cập nhật!", Toast.LENGTH_SHORT).show());
            });
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showDeleteConfirm(GiaoVien gv) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa Giáo Viên")
                .setMessage("Bạn có chắc muốn xóa GV " + gv.getTenGV() + "?\nCác lớp do GV này phụ trách sẽ chuyển về trạng thái 'Chưa phân công'.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    executor.execute(() -> {
                        db.giaoVienDao().delete(gv);

                        runOnUiThread(() -> Toast.makeText(this, "Đã xóa giáo viên!", Toast.LENGTH_SHORT).show());
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}