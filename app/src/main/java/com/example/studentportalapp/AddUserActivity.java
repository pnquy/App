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

        etMaTK = findViewById(R.id.etMaTK);
        etHoTen = findViewById(R.id.etHoTen);
        etEmail = findViewById(R.id.etEmail);
        etMatKhau = findViewById(R.id.etMatKhau);
        rgRole = findViewById(R.id.rgRole);
        btnSave = findViewById(R.id.btnSave);
        tvBack = findViewById(R.id.tvBack);

        generateAutoId("HOCVIEN");

        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbTeacher) {
                generateAutoId("GIAOVIEN");
            } else {
                generateAutoId("HOCVIEN");
            }
        });

        tvBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> handleSaveUser());
    }

    private void generateAutoId(String role) {
        executor.execute(() -> {
            int count = db.taiKhoanDao().countUsersByRole(role);
            int nextId = count + 1;

            String prefix = role.equals("GIAOVIEN") ? "GV" : "HV";
            String newId = String.format("%s%03d", prefix, nextId);

            while (db.taiKhoanDao().getById(newId) != null) {
                nextId++;
                newId = String.format("%s%03d", prefix, nextId);
            }

            String finalNewId = newId;
            runOnUiThread(() -> etMaTK.setText(finalNewId));
        });
    }

    private void handleSaveUser() {
        String maTK = etMaTK.getText().toString().trim();
        String hoTen = etHoTen.getText() != null ? etHoTen.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String matKhau = etMatKhau.getText() != null ? etMatKhau.getText().toString().trim() : "";

        if (maTK.isEmpty() || hoTen.isEmpty() || email.isEmpty() || matKhau.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        String role;
        int selectedId = rgRole.getCheckedRadioButtonId();
        if (selectedId == R.id.rbTeacher) role = "GIAOVIEN";
        else role = "HOCVIEN";

        executor.execute(() -> {
            try {
                TaiKhoan tk = new TaiKhoan();
                tk.MaTK = maTK;
                tk.HoTen = hoTen;
                tk.Email = email;
                tk.MatKhau = matKhau;
                tk.VaiTro = role;

                db.taiKhoanDao().insert(tk);

                if (role.equals("HOCVIEN")) {
                    HocVien hv = new HocVien();
                    hv.setMaHV(maTK);
                    hv.setTenHV(hoTen);
                    hv.setEmail(email);
                    hv.setMaTK(maTK);
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