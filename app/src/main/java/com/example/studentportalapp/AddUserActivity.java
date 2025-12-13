package com.example.studentportalapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.GiaoVien;
import com.example.studentportalapp.data.Entity.HocVien;
import com.example.studentportalapp.data.Entity.TaiKhoan;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddUserActivity extends AppCompatActivity {

    private TextInputEditText etMaTK, etHoTen, etEmail, etMatKhau;
    private RadioGroup rgRole;
    private Button btnSave;
    private TextView tvBack;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        db = AppDatabase.getDatabase(getApplicationContext());

        // Ánh xạ View
        etMaTK = findViewById(R.id.etMaTK);
        etHoTen = findViewById(R.id.etHoTen);
        etEmail = findViewById(R.id.etEmail);
        etMatKhau = findViewById(R.id.etMatKhau);
        rgRole = findViewById(R.id.rgRole);
        btnSave = findViewById(R.id.btnSave);
        tvBack = findViewById(R.id.tvBack);

        // Mặc định sinh mã cho Học Viên khi vừa mở lên
        generateAutoId("HOCVIEN");

        // Lắng nghe sự kiện đổi vai trò để sinh lại mã
        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbTeacher) {
                generateAutoId("GIAOVIEN");
            } else {
                generateAutoId("HOCVIEN");
            }
        });

        // Xử lý nút Back
        tvBack.setOnClickListener(v -> finish());

        // Xử lý nút Lưu
        btnSave.setOnClickListener(v -> handleSaveUser());
    }

    // Hàm sinh mã tự động: GV + số lượng hoặc HV + số lượng
    private void generateAutoId(String role) {
        executor.execute(() -> {
            // Đếm số lượng user hiện có của vai trò này
            int count = db.taiKhoanDao().countUsersByRole(role);
            int nextId = count + 1;

            // Tạo tiền tố (Prefix)
            String prefix = role.equals("GIAOVIEN") ? "GV" : "HV";

            // Format thành 3 chữ số (Ví dụ: GV001, HV015...)
            String newId = String.format("%s%03d", prefix, nextId);

            // Kiểm tra xem mã này đã tồn tại chưa (đề phòng trường hợp xóa rồi tạo lại bị trùng)
            while (db.taiKhoanDao().getById(newId) != null) {
                nextId++;
                newId = String.format("%s%03d", prefix, nextId);
            }

            // Cập nhật lên giao diện
            String finalNewId = newId;
            runOnUiThread(() -> etMaTK.setText(finalNewId));
        });
    }

    private void handleSaveUser() {
        // Lấy dữ liệu (Mã TK lấy trực tiếp từ ô đã tự động điền)
        String maTK = etMaTK.getText().toString().trim();
        String hoTen = etHoTen.getText() != null ? etHoTen.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String matKhau = etMatKhau.getText() != null ? etMatKhau.getText().toString().trim() : "";

        // Kiểm tra dữ liệu trống
        if (maTK.isEmpty() || hoTen.isEmpty() || email.isEmpty() || matKhau.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Xác định vai trò
        String role;
        int selectedId = rgRole.getCheckedRadioButtonId();
        if (selectedId == R.id.rbTeacher) role = "GIAOVIEN";
        else role = "HOCVIEN";

        executor.execute(() -> {
            try {
                // 1. Tạo Tài Khoản
                TaiKhoan tk = new TaiKhoan();
                tk.MaTK = maTK;
                tk.HoTen = hoTen;
                tk.Email = email;
                tk.MatKhau = matKhau;
                tk.VaiTro = role;

                db.taiKhoanDao().insert(tk);

                // 2. Tạo Entity tương ứng
                if (role.equals("HOCVIEN")) {
                    HocVien hv = new HocVien();
                    hv.setMaHV(maTK);
                    hv.setTenHV(hoTen);
                    hv.setEmail(email);
                    hv.setMaTK(maTK);
                    // hv.setMaLH(null); // Đã bỏ trường này
                    db.hocVienDao().insert(hv);
                } else {
                    GiaoVien gv = new GiaoVien();
                    gv.setMaGV(maTK);
                    gv.setTenGV(hoTen);
                    gv.setEmail(email);
                    gv.setMaTK(maTK);
                    db.giaoVienDao().insert(gv);
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Thêm thành công: " + maTK, Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}