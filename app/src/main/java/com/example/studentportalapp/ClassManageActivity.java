package com.example.studentportalapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import com.example.studentportalapp.data.Entity.ThamGia;

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
        setContentView(R.layout.activity_class_manage); // Đảm bảo file xml này đã có rvClassList

        db = AppDatabase.getDatabase(getApplicationContext());
        recyclerView = findViewById(R.id.rvClassList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddClass).setOnClickListener(v -> showAddEditDialog(null));

        // Tải danh sách giáo viên trước để dùng cho việc chọn GVCN
        loadTeachers();
    }

    // Tải danh sách giáo viên vào bộ nhớ
    private void loadTeachers() {
        executor.execute(() -> {
            allTeachers = db.giaoVienDao().getAllTeacherItems(); // Cần sửa lại DAO trả về List<GiaoVien> hoặc dùng List<TeacherItem>
            // Ở đây tôi giả định bạn lấy List<GiaoVien> từ DB. Nếu DAO bạn trả về TeacherItem, hãy convert tương ứng.
            // Để đơn giản, ta dùng: db.giaoVienDao().getAllSync() (Bạn cần thêm hàm này vào GiaoVienDao trả về List<GiaoVien>)
            // Nếu chưa có, hãy tạm dùng loop để tạo mảng tên:
        });
    }

    // Tải danh sách lớp kèm thông tin phụ (Tên GV, Sĩ số)
    private void loadClasses() {
        db.lopHocDao().getAll().observe(this, lopHocList -> {
            executor.execute(() -> {
                List<ClassDisplayItem> displayList = new ArrayList<>();

                // Lấy danh sách GV mới nhất để map tên
                List<GiaoVien> currentTeachers = ((List<GiaoVien>) db.giaoVienDao().getAllSync());
                // Lưu ý: Cần thêm hàm List<GiaoVien> getAllSync() vào GiaoVienDao

                for (LopHoc lh : lopHocList) {
                    // 1. Lấy tên GVCN
                    String tenGV = "Chưa phân công";
                    for (GiaoVien gv : currentTeachers) {
                        if (gv.getMaGV().equals(lh.MaGV)) {
                            tenGV = gv.getTenGV();
                            break;
                        }
                    }
                    // 2. Đếm sĩ số
                    int count = db.thamGiaDao().countStudentsByClass(lh.MaLH);

                    displayList.add(new ClassDisplayItem(lh, tenGV, count));
                }

                // Cập nhật UI
                runOnUiThread(() -> {
                    ClassAdapter adapter = new ClassAdapter(displayList, new ClassAdapter.OnItemClickListener() {
                        @Override
                        public void onAddStudent(ClassDisplayItem item) {
                            // Mở dialog thêm học viên nhanh vào lớp này
                            showAddStudentToClassDialog(item.lopHoc);
                        }

                        @Override
                        public void onStats(ClassDisplayItem item) {
                            Toast.makeText(ClassManageActivity.this, "Chức năng thống kê đang phát triển", Toast.LENGTH_SHORT).show();
                            // Sau này: Intent intent = new Intent(..., StatsActivity.class); intent.putExtra("MaLH", item.lopHoc.MaLH);
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

    // Dialog Thêm/Sửa Lớp (Chọn GV từ danh sách)
    private void showAddEditDialog(LopHoc existingClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(existingClass == null ? "Thêm Lớp Mới" : "Sửa Lớp Học");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_class, null);
        TextInputEditText etMa = view.findViewById(R.id.etMaLH);
        TextInputEditText etTen = view.findViewById(R.id.etTenLH);
        // Đây là ô chọn GV, không cho nhập tay
        TextInputEditText etTenGV = view.findViewById(R.id.etMaGV);
        etTenGV.setFocusable(false);
        etTenGV.setClickable(true);


        // Biến tạm để lưu MaGV được chọn
        final String[] selectedMaGV = {existingClass != null ? existingClass.MaGV : null};

        // Logic tải danh sách GV để chọn
        executor.execute(() -> {
            List<GiaoVien> teachers = (List<GiaoVien>) db.giaoVienDao().getAllSync(); // Cần thêm hàm này vào DAO
            teacherNames = new String[teachers.size()];
            for(int i=0; i<teachers.size(); i++) teacherNames[i] = teachers.get(i).getMaGV() + " - " + teachers.get(i).getTenGV();

            runOnUiThread(() -> {
                // Nếu đang sửa, hiển thị tên GV hiện tại
                if (existingClass != null) {
                    for(GiaoVien g : teachers) {
                        if(g.getMaGV().equals(existingClass.MaGV)) {
                            etTenGV.setText(g.getMaGV() + " - " + g.getTenGV());
                            break;
                        }
                    }
                }

                // Sự kiện click vào ô GV -> Hiện list chọn
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
            lh.MaGV = selectedMaGV[0]; // Lưu mã GV đã chọn

            executor.execute(() -> {
                if (existingClass == null) db.lopHocDao().insert(lh);
                else db.lopHocDao().update(lh);

                // Gọi lại loadClasses để refresh list
                loadClasses();
            });
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    // Dialog thêm nhanh học viên vào lớp (Logic nút +)
    // Hàm mới: Chọn nhiều học viên từ danh sách
// 1. Hàm hiển thị Dialog chọn học viên (Logic Đa lớp)
    private void showAddStudentToClassDialog(LopHoc lh) {
        executor.execute(() -> {
            // Lấy tất cả học viên
            List<HocVien> allStudents = db.hocVienDao().getAllSync();

            // Lấy danh sách ID học viên ĐANG ở trong lớp này (từ bảng THAMGIA)
            List<String> enrolledStudentIds = db.thamGiaDao().getStudentIdsByClass(lh.MaLH);

            String[] studentNames = new String[allStudents.size()];
            boolean[] checkedItems = new boolean[allStudents.size()];

            // List lưu ID những người được chọn
            List<String> selectedIds = new ArrayList<>();

            for (int i = 0; i < allStudents.size(); i++) {
                HocVien hv = allStudents.get(i);
                studentNames[i] = hv.getMaHV() + " - " + hv.getTenHV();

                // Kiểm tra xem HV này có trong list enrolled không
                if (enrolledStudentIds.contains(hv.getMaHV())) {
                    checkedItems[i] = true; // Đánh dấu đã chọn
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
                    // Gọi hàm lưu (truyền danh sách ID hiện tại và danh sách ID mới chọn)
                    saveStudentClassChanges(lh, enrolledStudentIds, selectedIds);
                });

                builder.setNegativeButton("Hủy", null);
                builder.show();
            });
        });
    }

    // 2. Hàm lưu thay đổi vào bảng THAMGIA
    private void saveStudentClassChanges(LopHoc currentClass, List<String> oldIds, List<String> newIds) {
        executor.execute(() -> {
            // A. Tìm những người MỚI được tích -> Thêm vào bảng THAMGIA
            for (String newId : newIds) {
                if (!oldIds.contains(newId)) {
                    db.thamGiaDao().insert(new com.example.studentportalapp.data.Entity.ThamGia(newId, currentClass.MaLH));
                }
            }

            // B. Tìm những người BỊ BỎ tích -> Xóa khỏi bảng THAMGIA
            for (String oldId : oldIds) {
                if (!newIds.contains(oldId)) {
                    db.thamGiaDao().removeStudentFromClass(oldId, currentClass.MaLH);
                }
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Đã cập nhật danh sách lớp!", Toast.LENGTH_SHORT).show();
                loadClasses(); // Load lại để cập nhật sĩ số
            });
        });
    }
    private void showDeleteConfirm(LopHoc lh) {
        new AlertDialog.Builder(this)
                .setTitle("Cảnh báo")
                .setMessage("Xóa lớp " + lh.TenLH + " sẽ set NULL mã lớp của tất cả học viên trong lớp này. Tiếp tục?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    executor.execute(() -> {
                        db.lopHocDao().delete(lh);
                        loadClasses();
                    });
                })
                .setNegativeButton("Hủy", null).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClasses();
    }
}