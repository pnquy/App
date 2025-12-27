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

    private TextInputEditText etEmail, etOTP, etNewPass;
    private Button btnGetCode, btnConfirm;
    private ImageView btnBack;
    private LinearLayout layoutResetPass;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String generatedOTP = "";
    private String currentEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        db = AppDatabase.getDatabase(getApplicationContext());

        etEmail = findViewById(R.id.etResetEmail);
        etOTP = findViewById(R.id.etOTP);
        etNewPass = findViewById(R.id.etNewPass);

        btnGetCode = findViewById(R.id.btnGetCode);
        btnConfirm = findViewById(R.id.btnConfirmReset);

        btnBack = findViewById(R.id.btnBackToLogin);
        layoutResetPass = findViewById(R.id.layoutResetPass);

        btnBack.setOnClickListener(v -> finish());
        btnGetCode.setOnClickListener(v -> handleSendCode());
        btnConfirm.setOnClickListener(v -> handleConfirmReset());
    }

    private void handleSendCode() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập Email");
            return;
        }

        executor.execute(() -> {
            TaiKhoan user = db.taiKhoanDao().getByEmail(email);

            runOnUiThread(() -> {
                if (user == null) {
                    Toast.makeText(this, "Email không tồn tại trong hệ thống!", Toast.LENGTH_SHORT).show();
                } else {
                    Random random = new Random();
                    int otp = random.nextInt(9000) + 1000;
                    generatedOTP = String.valueOf(otp);
                    currentEmail = email;

                    Toast.makeText(this, "Mã xác nhận của bạn là: " + generatedOTP, Toast.LENGTH_LONG).show();

                    etEmail.setEnabled(false);
                    btnGetCode.setVisibility(View.GONE);
                    layoutResetPass.setVisibility(View.VISIBLE);
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

        if (!inputOTP.equals(generatedOTP)) {
            etOTP.setError("Mã xác nhận không đúng");
            return;
        }

        executor.execute(() -> {
            TaiKhoan user = db.taiKhoanDao().getByEmail(currentEmail);
            if (user != null) {
                user.MatKhau = newPass;
                db.taiKhoanDao().update(user);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Đổi mật khẩu thành công! Hãy đăng nhập lại.", Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        });
    }
}