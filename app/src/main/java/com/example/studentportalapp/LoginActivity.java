package com.example.studentportalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.TaiKhoan;
import com.example.studentportalapp.databinding.ActivityLoginBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final String ADMIN_EMAIL = "admin@gmail.com";
    private final String ADMIN_PASS  = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getDatabase(getApplicationContext());

        // --- CODE MỚI THÊM VÀO ---
        // Xử lý nút Quên Mật Khẩu
        binding.tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        // -------------------------

        // Xử lý khi nhấn nút Đăng nhập (Code cũ của bạn giữ nguyên)
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASS)) {
                saveUserSession("ADMIN", "ADMIN");
                Toast.makeText(LoginActivity.this, "Đăng nhập ADMIN thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            executor.execute(() -> {
                TaiKhoan user = db.taiKhoanDao().getByEmail(email);

                runOnUiThread(() -> {
                    if (user == null) {
                        Toast.makeText(LoginActivity.this, "Tài khoản không tồn tại.", Toast.LENGTH_SHORT).show();
                    } else if (!user.MatKhau.equals(password)) { // Lưu ý check field public/getter
                        Toast.makeText(LoginActivity.this, "Mật khẩu không đúng.", Toast.LENGTH_SHORT).show();
                    } else {
                        saveUserSession(user.MaTK, user.VaiTro);
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            });
        });
    }

    private void saveUserSession(String userId, String role) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_ID", userId);
        editor.putString("USER_ROLE", role);
        editor.apply();
    }
}