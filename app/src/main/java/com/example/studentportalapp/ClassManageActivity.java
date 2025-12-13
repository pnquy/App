package com.example.studentportalapp;

import android.content.DialogInterface;
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
    private List<TeacherItem> allTeachers = new ArrayList<>(); // Danh sách tất cả GV để chọn
    private String[] teacherNames; // Mảng tên GV để hiển thị lên Dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_manage);

        db = AppDatabase.getDatabase(getApplicationContext());
        recyclerView = findViewById(R.id.rvClassList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddClass).setOnClickListener(v -> showAddEditDialog(null));

        // Tải danh sách giáo viên trước để dùng cho việc chọn GVCN
        loadTeachers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClasses();
    }

    // Tải danh sách giáo viên vào bộ nhớ để dùng cho Dialog chọn GV
    private void loadTeachers() {
        executor.execute(() -> {
            // Lưu ý: Đảm bảo GiaoVienDao có hàm getAllSync() trả về List<GiaoVien>
            try {
                List<GiaoVien> teachers = db.giaoVienDao().getAllSync();
                // Nếu dùng TeacherItem thì convert, ở đây giả định dùng GiaoVien trực tiếp cho đơn giản
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Tải danh sách lớp kèm thông tin phụ (Tên GV, Sĩ số)
    private void loadClasses() {
        // Observer chạy trên Main Thread, nên an toàn
        db.lopHocDao().getAll().observe(this, lopHocList -> {
            executor.execute(() -> {
                List<ClassDisplayItem> displayList = new ArrayList<>();

                // Lấy danh sách GV để map tên
                List<GiaoVien> currentTeachers = db.giaoVienDao().getAllSync();

                for (LopHoc lh : lopHocList) {
                    // 1. Lấy tên GVCN
                    String tenGV = "Chưa phân công";
                    for (GiaoVien gv : currentTeachers) {
                        if (gv.getMaGV().equals(lh.MaGV)) {
                            tenGV = gv.getTenGV();
                            break;
                        }
                    }
                    // 2. Đếm sĩ số từ bảng trung gian THAMGIA
                    int count = db.thamGiaDao().countStudentsByClass(lh.MaLH);

                    displayList.add(new ClassDisplayItem(lh, tenGV, count));
                }

                // Cập nhật UI (Bắt buộc runOnUiThread)
                runOnUiThread(() -> {
                    ClassAdapter adapter = new ClassAdapter(displayList, new ClassAdapter.OnItemClickListener() {
                        @Override
                        public void onAddStudent(ClassDisplayItem item) {
                            showAddStudentToClassDialog(item.lopHoc);
                        }

                        @Override
                        public void onStats(ClassDisplayItem item) {
                            Toast.makeText(ClassManageActivity.this, "Chức năng thống kê đang phát triển", Toast.LENGTH_SHORT).show();
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

    // Dialog Thêm/Sửa Lớp
    // Dialog Thêm/Sửa Lớp (Đã cập nhật Check trùng + Khóa mã khi sửa)
    private void showAddEditDialog(LopHoc existingClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(existingClass == null ? "Thêm Lớp Mới" : "Sửa Lớp Học");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_class, null);
        TextInputEditText etMa = view.findViewById(R.id.etMaLH);
        TextInputEditText etTen = view.findViewById(R.id.etTenLH);
        TextInputEditText etTenGV = view.findViewById(R.id.etMaGV);
        etTenGV.setFocusable(false);
        etTenGV.setClickable(true);

        final String[] selectedMaGV = {existingClass != null ? existingClass.MaGV : null};

        // Logic tải danh sách GV (Giữ nguyên)
        executor.execute(() -> {
            List<GiaoVien> teachers = db.giaoVienDao().getAllSync();
            teacherNames = new String[teachers.size()];
            for (int i = 0; i < teachers.size(); i++) {
                teacherNames[i] = teachers.get(i).getMaGV() + " - " + teachers.get(i).getTenGV();
            }

            runOnUiThread(() -> {
                if (existingClass != null) {
                    for (GiaoVien g : teachers) {
                        if (g.getMaGV().equals(existingClass.MaGV)) {
                            etTenGV.setText(g.getMaGV() + " - " + g.getTenGV());
                            break;
                        }
                    }
                }
                etTenGV.setOnClickListener(v -> {
                    new AlertDialog.Builder(ClassManageActivity.this)
                            .setTitle("Chọn Giáo Viên")
                            .setItems(teacherNames, (dialog, which) -> {
                                GiaoVien selected = teachers.get(which);
                                etTenGV.setText(selected.getMaGV() + " - " + selected.getTenGV());
                                selectedMaGV[0] = selected.getMaGV();
                            })
                            .show();
                });
            });
        });

        // --- XỬ LÝ CHẾ ĐỘ SỬA ---
        if (existingClass != null) {
            etMa.setText(existingClass.MaLH);
            etMa.setEnabled(false);    // Vô hiệu hóa nhập liệu
            etMa.setFocusable(false);  // Không cho focus
            etMa.setAlpha(0.5f);       // Làm mờ đi để người dùng biết là không sửa được
            etTen.setText(existingClass.TenLH);
        }

        builder.setView(view);

        // --- XỬ LÝ NÚT LƯU ---
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String ma = etMa.getText().toString().trim();
            String ten = etTen.getText().toString().trim();

            if (ma.isEmpty() || ten.isEmpty()) {
                Toast.makeText(this, "Thiếu thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chạy Logic kiểm tra và Lưu trong Background Thread
            executor.execute(() -> {

                // 1. KIỂM TRA TRÙNG MÃ (Chỉ chạy khi đang THÊM MỚI)
                if (existingClass == null) {
                    LopHoc checkDuplicate = db.lopHocDao().getById(ma);
                    if (checkDuplicate != null) {
                        // Nếu tìm thấy mã lớp đã tồn tại -> Báo lỗi và Dừng lại
                        runOnUiThread(() ->
                                Toast.makeText(ClassManageActivity.this, "Lỗi: Mã lớp " + ma + " đã tồn tại!", Toast.LENGTH_LONG).show()
                        );
                        return; // Thoát khỏi hàm, không lưu
                    }
                }

                // 2. Nếu không trùng (hoặc đang Sửa), tiến hành tạo object
                LopHoc lh = new LopHoc();
                lh.MaLH = ma;
                lh.TenLH = ten;
                lh.MaGV = selectedMaGV[0];

                // 3. Thực hiện Insert hoặc Update
                if (existingClass == null) {
                    db.lopHocDao().insert(lh);
                } else {
                    db.lopHocDao().update(lh);
                }

                // 4. Cập nhật UI
                runOnUiThread(() -> {
                    loadClasses();
                    String msg = (existingClass == null) ? "Thêm lớp thành công!" : "Cập nhật thành công!";
                    Toast.makeText(ClassManageActivity.this, msg, Toast.LENGTH_SHORT).show();
                });
            });
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    // Dialog chọn nhiều học viên vào lớp (Logic Many-to-Many)
    private void showAddStudentToClassDialog(LopHoc lh) {
        executor.execute(() -> {
            // Lấy tất cả học viên
            List<HocVien> allStudents = db.hocVienDao().getAllSync();

            // Lấy danh sách ID học viên ĐANG ở trong lớp này (từ bảng THAMGIA)
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

    // Lưu thay đổi vào bảng THAMGIA
    private void saveStudentClassChanges(LopHoc currentClass, List<String> oldIds, List<String> newIds) {
        executor.execute(() -> {
            // A. Thêm mới
            for (String newId : newIds) {
                if (!oldIds.contains(newId)) {
                    db.thamGiaDao().insert(new com.example.studentportalapp.data.Entity.ThamGia(newId, currentClass.MaLH));
                }
            }

            // B. Xóa bỏ
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

    // Xóa lớp học
    private void showDeleteConfirm(LopHoc lh) {
        new AlertDialog.Builder(this)
                .setTitle("Cảnh báo")
                .setMessage("Xóa lớp " + lh.TenLH + " sẽ xóa lớp này khỏi danh sách học của tất cả học viên. Tiếp tục?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    executor.execute(() -> {
                        db.lopHocDao().delete(lh);

                        // --- KHẮC PHỤC LỖI CRASH Ở ĐÂY ---
                        runOnUiThread(() -> {
                            loadClasses();
                            Toast.makeText(ClassManageActivity.this, "Đã xóa lớp thành công!", Toast.LENGTH_SHORT).show();
                        });
                    });
                })
                .setNegativeButton("Hủy", null).show();
    }
}