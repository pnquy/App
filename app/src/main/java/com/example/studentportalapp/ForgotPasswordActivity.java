package com.example.studentportalapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.TaiKhoan;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForgotPasswordActivity extends AppCompatActivity {

    // Khai báo View
    private TextInputEditText etEmail, etOTP, etNewPass;
    private Button btnGetCode, btnConfirm;
    private ImageView btnBack; // <--- SỬA LỖI Ở ĐÂY: Dùng ImageView thay vì Button
    private LinearLayout layoutResetPass;

    // Database
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Biến lưu OTP và Email hiện tại
    private String generatedOTP = "";
    private String currentEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        db = AppDatabase.getDatabase(getApplicationContext());

        // 1. Ánh xạ View (Khớp ID trong XML)
        etEmail = findViewById(R.id.etResetEmail);
        etOTP = findViewById(R.id.etOTP);
        etNewPass = findViewById(R.id.etNewPass);

        btnGetCode = findViewById(R.id.btnGetCode);
        btnConfirm = findViewById(R.id.btnConfirmReset);

        btnBack = findViewById(R.id.btnBackToLogin); // XML là ImageView -> Java là ImageView
        layoutResetPass = findViewById(R.id.layoutResetPass);

        // 2. Xử lý sự kiện nút Back
        btnBack.setOnClickListener(v -> finish());

        // 3. Xử lý nút Lấy Mã
        btnGetCode.setOnClickListener(v -> handleSendCode());

        // 4. Xử lý nút Đổi Mật Khẩu
        btnConfirm.setOnClickListener(v -> handleConfirmReset());
    }

    private void handleSendCode() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập Email");
            return;
        }

        executor.execute(() -> {
            // Kiểm tra Email có tồn tại trong DB không
            TaiKhoan user = db.taiKhoanDao().getByEmail(email);

            runOnUiThread(() -> {
                if (user == null) {
                    Toast.makeText(this, "Email không tồn tại trong hệ thống!", Toast.LENGTH_SHORT).show();
                } else {
                    // Email đúng -> Tạo mã OTP 4 số ngẫu nhiên
                    Random random = new Random();
                    int otp = random.nextInt(9000) + 1000; // 1000 -> 9999
                    generatedOTP = String.valueOf(otp);
                    currentEmail = email;

                    // HIỆN OTP LÊN MÀN HÌNH (Simulation)
                    Toast.makeText(this, "Mã xác nhận của bạn là: " + generatedOTP, Toast.LENGTH_LONG).show();

                    // Cập nhật giao diện
                    etEmail.setEnabled(false); // Khóa ô email
                    btnGetCode.setVisibility(View.GONE); // Ẩn nút lấy mã
                    layoutResetPass.setVisibility(View.VISIBLE); // Hiện form nhập OTP
                }
            });
        });
    }

    private void handleConfirmReset() {
        String inputOTP = etOTP.getText() != null ? etOTP.getText().toString().trim() : "";
        String newPass = etNewPass.getText() != null ? etNewPass.getText().toString().trim() : "";

        if (inputOTP.isEmpty() || newPass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra OTP
        if (!inputOTP.equals(generatedOTP)) {
            etOTP.setError("Mã xác nhận không đúng");
            return;
        }

        // OTP đúng -> Cập nhật mật khẩu vào DB
        executor.execute(() -> {
            TaiKhoan user = db.taiKhoanDao().getByEmail(currentEmail);
            if (user != null) {
                user.MatKhau = newPass; // Cập nhật pass mới
                db.taiKhoanDao().update(user);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Đổi mật khẩu thành công! Hãy đăng nhập lại.", Toast.LENGTH_LONG).show();
                    finish(); // Đóng màn hình này
                });
            }
        });
    }
}