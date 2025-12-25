package com.example.studentportalapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.TaiKhoan;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvLabelID, tvUserID, tvName, tvEmail, tvHeaderName;
    private Button btnChangePass;
    private ImageView btnBack;

    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = AppDatabase.getDatabase(getApplicationContext());

        // 1. Ánh xạ View
        tvLabelID = findViewById(R.id.tvLabelID);
        tvUserID = findViewById(R.id.tvUserID);
        tvName = findViewById(R.id.tvName);
        tvHeaderName = findViewById(R.id.tvHeaderName);
        tvEmail = findViewById(R.id.tvEmail);
        btnChangePass = findViewById(R.id.btnChangePass);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // 2. Lấy thông tin phiên đăng nhập từ "UserSession"
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUserId = prefs.getString("KEY_USER_ID", "");
        String role = prefs.getString("KEY_ROLE", "");

        // 3. Logic hiển thị Label theo Role
        if (role.equalsIgnoreCase("GIAOVIEN")) {
            tvLabelID.setText("Mã Giảng Viên:");
        } else if (role.equalsIgnoreCase("HOCVIEN")) {
            tvLabelID.setText("Mã Học Viên:");
        } else {
            tvLabelID.setText("Mã Tài Khoản:"); // Trường hợp Admin
        }

        // 4. Load dữ liệu
        loadUserProfile(currentUserId);

        // 5. Nút đổi mật khẩu
        btnChangePass.setOnClickListener(v -> showChangePasswordDialog());
    }

    private void loadUserProfile(String userId) {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
            return;
        }
        executor.execute(() -> {
            TaiKhoan user = db.taiKhoanDao().getById(userId);

            runOnUiThread(() -> {
                if (user != null) {
                    tvUserID.setText(user.MaTK);
                    tvName.setText(user.HoTen);
                    tvHeaderName.setText(user.HoTen);
                    tvEmail.setText(user.Email);
                } else {
                    Toast.makeText(this, "Lỗi tải thông tin!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đổi Mật Khẩu");

        View view = LayoutInflater.from(this).inflate(R.layout.change_password, null);

        EditText etOldPass = view.findViewById(R.id.etOldPass);
        EditText etNewPass = view.findViewById(R.id.etNewPass);
        EditText etConfirmPass = view.findViewById(R.id.etConfirmPass);

        builder.setView(view);
        builder.setPositiveButton("Lưu", null); // Xử lý sau để tránh đóng dialog
        builder.setNegativeButton("Hủy", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String oldPass = etOldPass.getText().toString();
            String newPass = etNewPass.getText().toString();
            String confirmPass = etConfirmPass.getText().toString();

            if (oldPass.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            executor.execute(() -> {
                TaiKhoan user = db.taiKhoanDao().getById(currentUserId);
                if (user != null && user.MatKhau.equals(oldPass)) {
                    // Update Database
                    user.MatKhau = newPass;
                    db.taiKhoanDao().update(user);

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Mật khẩu cũ không đúng!", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }
}
