package com.example.studentportalapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.BaiGiang;
import com.example.studentportalapp.data.Entity.ThongBao;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AddLectureActivity extends BaseActivity {

    private TextInputEditText etTitle, etDesc;
    private LinearLayout btnAttach;
    private TextView tvFileName;
    private Button btnPost;
    private ImageView btnBack;
    private TextView tvHeaderTitle;

    private String currentMaLH;
    private String currentMaGV;
    private Uri selectedFileUri = null;
    private String selectedFileName = null;

    private boolean isEditMode = false;
    private String existingId = null;
    private String existingFilePath = null;
    private String existingFileName = null;

    private final ActivityResultLauncher<String[]> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    selectedFileUri = uri;
                    try {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                    selectedFileName = getFileName(uri);
                    if (tvFileName != null) {
                        tvFileName.setText(selectedFileName);
                    }
                }
            }
    );

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_add_lecture;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentMaLH = prefs.getString("CURRENT_CLASS_ID", "");
        currentMaGV = prefs.getString("KEY_USER_ID", "");

        etTitle = findViewById(R.id.edtTitle);
        etDesc = findViewById(R.id.edtContent);
        btnAttach = findViewById(R.id.btnPickFile);
        btnPost = findViewById(R.id.btnSubmit);
        tvFileName = findViewById(R.id.tvFileName);
        
        btnBack = findViewById(R.id.btnBack);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnAttach != null) {
            btnAttach.setOnClickListener(v -> filePickerLauncher.launch(new String[]{"*/*"}));
        }

        if (btnPost != null) {
            btnPost.setOnClickListener(v -> handlePost());
        }

        checkEditMode();
    }

    private void checkEditMode() {
        Intent intent = getIntent();
        if (intent.hasExtra("EDIT_ID")) {
            isEditMode = true;
            existingId = intent.getStringExtra("EDIT_ID");
            String title = intent.getStringExtra("EDIT_TITLE");
            String content = intent.getStringExtra("EDIT_CONTENT");
            existingFilePath = intent.getStringExtra("EDIT_FILE_PATH");
            existingFileName = intent.getStringExtra("EDIT_FILE_NAME");

            if (etTitle != null) etTitle.setText(title);
            if (etDesc != null) etDesc.setText(content);
            if (existingFileName != null && tvFileName != null) {
                tvFileName.setText(existingFileName);
            }

            if (tvHeaderTitle != null) tvHeaderTitle.setText("Sửa Bài Giảng");
            if (btnPost != null) btnPost.setText("Lưu Thay Đổi");
        }
    }

    private void handlePost() {
        String title = (etTitle != null && etTitle.getText() != null) ? etTitle.getText().toString().trim() : "";
        String desc = (etDesc != null && etDesc.getText() != null) ? etDesc.getText().toString().trim() : "";

        if (title.isEmpty()) {
            if (etTitle != null) etTitle.setError("Nhập tiêu đề!");
            return;
        }

        BaiGiang bg = new BaiGiang();
        bg.MaBG = isEditMode ? existingId : "BG" + System.currentTimeMillis();
        bg.TenBG = title;
        bg.NoiDung = desc;
        bg.MaLH = currentMaLH;
        bg.MaGV = currentMaGV;

        if (selectedFileUri != null) {
            bg.FilePath = selectedFileUri.toString();
            bg.FileName = selectedFileName;
        } else {
            bg.FilePath = existingFilePath;
            bg.FileName = existingFileName;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
            if (isEditMode) {
                database.baiGiangDao().update(bg);
                runOnUiThread(() -> Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show());
            } else {
                database.baiGiangDao().insert(bg);
                
                // Gửi thông báo cho HỌC VIÊN
                ThongBao tb = new ThongBao();
                tb.NoiDung = "Có bài giảng mới: " + title;
                tb.NgayTao = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
                tb.NguoiNhan = "HOCVIEN";
                tb.LoaiTB = "LECTURE";
                tb.TargetId = currentMaLH;
                database.thongBaoDao().insert(tb);

                runOnUiThread(() -> Toast.makeText(this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show());
            }
            runOnUiThread(this::finish);
        });
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) result = cursor.getString(index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
