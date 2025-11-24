package com.example.studentportalapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.GiaoVien;
import com.example.studentportalapp.data.Entity.HocVien;
import com.example.studentportalapp.data.Entity.TaiKhoan;
import com.example.studentportalapp.databinding.ActivityAddUserBinding;

import java.util.List;
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

        // Khóa không cho nhập MaTK
        binding.etMaTK.setEnabled(false);

        // spinner role
        String[] roles = {"GIAOVIEN", "HOCVIEN"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roles);
        binding.spRole.setAdapter(adapter);

        // Khi chọn role → tự tạo MaTK
        binding.spRole.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int pos, long id) {
                String role = roles[pos];
                generateAutoId(role);
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        binding.btnCreate.setOnClickListener(v -> {
            String MaTK = binding.etMaTK.getText().toString().trim();
            String HoTen = binding.etHoTen.getText().toString().trim();
            String Email = binding.etEmail.getText().toString().trim();
            String MatKhau = binding.etMatKhau.getText().toString().trim();
            String role = binding.spRole.getSelectedItem().toString();

            if (HoTen.isEmpty() || Email.isEmpty() || MatKhau.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            executor.execute(() -> {
                // 1) lưu TAIKHOAN
                TaiKhoan tk = new TaiKhoan();
                tk.MaTK = MaTK;
                tk.HoTen = HoTen;
                tk.Email = Email;
                tk.MatKhau = MatKhau;
                tk.VaiTro = role;

                db.taiKhoanDao().insert(tk);

                // 2) nếu là giáo viên -> tạo record trong GIAOVIEN
                if (role.equals("GIAOVIEN")) {
                    GiaoVien gv = new GiaoVien();
                    gv.setMaGV(MaTK);
                    gv.setMaTK(MaTK);
                    gv.setTenGV(HoTen);
                    gv.setEmail(Email);
                    gv.setMaLH(null);   // không có lớp lúc tạo
                    db.giaoVienDao().insert(gv);
                }

                // 3) nếu là học viên -> tạo record trong HOCVIEN
                if (role.equals("HOCVIEN")) {  
                    HocVien hv = new HocVien();
                    hv.setMaHV(MaTK);
                    hv.setMaTK(MaTK);
                    hv.setTenHV(HoTen);
                    hv.setEmail(Email);
                    hv.setMaLH(null);   // không có lớp lúc tạo
                    db.hocVienDao().insert(hv);
                }



                runOnUiThread(() -> {
                    Toast.makeText(AddUserActivity.this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        });
    }

    // Tạo mã tự động
    private void generateAutoId(String role) {
        executor.execute(() -> {

            String prefix = role.equals("GIAOVIEN") ? "GV" : "HV";

            List<TaiKhoan> list = db.taiKhoanDao().getAllSync(); // cần hàm getAllSync()

            int count = 0;
            for (TaiKhoan tk : list) {
                if (tk.MaTK.startsWith(prefix)) {
                    count++;
                }
            }

            int next = count + 1;
            String newId = prefix + String.format("%02d", next);

            runOnUiThread(() -> binding.etMaTK.setText(newId));
        });
    }
}
