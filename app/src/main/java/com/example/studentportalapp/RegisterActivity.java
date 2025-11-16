package com.example.studentportalapp;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentportalapp.data.User;
import com.example.studentportalapp.databinding.ActivityRegisterBinding;
import com.example.studentportalapp.data.AppDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getDatabase(getApplicationContext());


        binding.tvToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });


        binding.btnRegister.setOnClickListener(v -> {
            String email = binding.etRegEmail.getText().toString().trim();
            String password = binding.etRegPassword.getText().toString().trim();
            String rawCourseCode = binding.etRegCourseCode.getText().toString().trim();
            final String courseCode = rawCourseCode.isEmpty() ? null : rawCourseCode;

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            executor.execute(() -> {
                User existing = db.userDao().getByEmail(email);

                runOnUiThread(() -> {
                    if (existing != null) {
                        Toast.makeText(this, "Email đã được đăng ký.", Toast.LENGTH_SHORT).show();
                    } else {
                        executor.execute(() -> {
                            User newUser = new User(email, password, courseCode);
                            db.userDao().insert(newUser);

                            runOnUiThread(() -> {
                                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                            });
                        });
                    }
                });
            });
        });
    }
}
