package com.example.studentportalapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.GiaoVien;
import com.example.studentportalapp.data.TaiKhoan;
import com.example.studentportalapp.databinding.ActivityAddUserBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddUserActivity extends AppCompatActivity {

    private ActivityAddUserBinding binding;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getDatabase(getApplicationContext());

        // spinner role
        String[] roles = {"GIAOVIEN", "HOCVIEN"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roles);
        binding.spRole.setAdapter(adapter);

        binding.btnCreate.setOnClickListener(v -> {
            String MaTK = binding.etMaTK.getText().toString().trim();
            String HoTen = binding.etHoTen.getText().toString().trim();
            String Email = binding.etEmail.getText().toString().trim();
            String MatKhau = binding.etMatKhau.getText().toString().trim();
            String role = binding.spRole.getSelectedItem().toString();

            if (MaTK.isEmpty() || HoTen.isEmpty() || Email.isEmpty() || MatKhau.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            executor.execute(() -> {
                TaiKhoan tk = new TaiKhoan();
                tk.MaTK = MaTK;
                tk.HoTen = HoTen;
                tk.Email = Email;
                tk.MatKhau = MatKhau;
                tk.VaiTro = role;

                db.taiKhoanDao().insert(tk);

                // nếu là giáo viên -> tạo record trong table GIAOVIEN
                if (role.equals("GIAOVIEN")) {
                    GiaoVien gv = new GiaoVien();
                    gv.MaGV = MaTK; // sử dụng MaTK chính là MaGV luôn cho đơn giản
                    gv.MaTK = MaTK;
                    gv.MaLH = ""; // để sau admin gán lớp
                    db.giaoVienDao().insert(gv);
                }

                runOnUiThread(() -> {
                    Toast.makeText(AddUserActivity.this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        });
    }
}
