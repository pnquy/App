package com.example.studentportalapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.BaiGiang;

import java.util.concurrent.Executors;

public class AddLectureActivity extends BaseActivity {

    private EditText etTitle, etDesc;
    private Button btnAttach;
    private TextView tvFileName;
    private View btnPost;

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
                        tvFileName.setText("Đã chọn: " + selectedFileName);
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

        etTitle = findViewById(R.id.et_lecture_title);
        etDesc = findViewById(R.id.et_lecture_description);
        btnAttach = findViewById(R.id.btn_attach_file);
        btnPost = findViewById(R.id.btn_post_lecture);
        tvFileName = findViewById(R.id.tv_file_name);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) toolbar.setNavigationOnClickListener(v -> finish());

        btnAttach.setOnClickListener(v -> filePickerLauncher.launch(new String[]{"*/*"}));

        btnPost.setOnClickListener(v -> handlePost());

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

            etTitle.setText(title);
            etDesc.setText(content);
            if (existingFileName != null) {
                tvFileName.setText("File cũ: " + existingFileName);
            }

            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) toolbar.setTitle("Sửa Bài Giảng");
        }
    }

    private void handlePost() {
        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Nhập tiêu đề!");
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
            if (isEditMode) {
                AppDatabase.getDatabase(getApplicationContext()).baiGiangDao().update(bg);
                runOnUiThread(() -> Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show());
            } else {
                AppDatabase.getDatabase(getApplicationContext()).baiGiangDao().insert(bg);
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