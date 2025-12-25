package com.example.studentportalapp;

import android.app.DatePickerDialog;
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

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.BaiTap;
import com.example.studentportalapp.data.Entity.ThongBao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AddAssignmentActivity extends BaseActivity {

    private EditText etTitle, etInstructions, etDueDate;
    private View btnPickFile;
    private TextView tvFileName, tvHeaderTitle;
    private Button btnSubmit;
    private Calendar myCalendar;
    private Uri selectedFileUri;
    private String selectedFileName;
    private String currentMaLH;

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
                    tvFileName.setText("Đã chọn: " + selectedFileName);
                    tvFileName.setTextColor(getResources().getColor(R.color.purple_500));
                }
            }
    );

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_add_assignment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        currentMaLH = prefs.getString("CURRENT_CLASS_ID", "");

        // Ánh xạ View theo Layout Mới
        etTitle = findViewById(R.id.edtTitle);
        etInstructions = findViewById(R.id.edtDesc);
        etDueDate = findViewById(R.id.edtDeadline);
        btnPickFile = findViewById(R.id.btnPickFile);
        tvFileName = findViewById(R.id.tvFileName);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        View btnBack = findViewById(R.id.btnBack);

        myCalendar = Calendar.getInstance();

        // Xử lý nút Back
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Date Picker
        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateLabel();
        };

        etDueDate.setOnClickListener(v -> new DatePickerDialog(AddAssignmentActivity.this, date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        // Chọn file
        btnPickFile.setOnClickListener(v -> filePickerLauncher.launch(new String[]{"*/*"}));

        // Submit
        btnSubmit.setOnClickListener(v -> handleAssign());

        checkEditMode();
    }

    private void checkEditMode() {
        Intent intent = getIntent();
        if (intent.hasExtra("EDIT_ID")) {
            isEditMode = true;
            existingId = intent.getStringExtra("EDIT_ID");
            String title = intent.getStringExtra("EDIT_TITLE");
            String desc = intent.getStringExtra("EDIT_DESC");
            String date = intent.getStringExtra("EDIT_DATE");
            existingFilePath = intent.getStringExtra("EDIT_FILE_PATH");
            existingFileName = intent.getStringExtra("EDIT_FILE_NAME");

            etTitle.setText(title);
            etInstructions.setText(desc);
            etDueDate.setText(date);

            if (existingFileName != null) {
                tvFileName.setText("File cũ: " + existingFileName);
            }

            if (tvHeaderTitle != null) {
                tvHeaderTitle.setText("Cập Nhật Bài Tập");
            }
            btnSubmit.setText("Lưu Thay Đổi");
        }
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        etDueDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void handleAssign() {
        String title = etTitle.getText().toString().trim();
        String instructions = etInstructions.getText().toString().trim();
        String dueDate = etDueDate.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Vui lòng nhập tiêu đề");
            return;
        }
        if (dueDate.isEmpty()) {
            etDueDate.setError("Vui lòng chọn hạn nộp");
            return;
        }

        final BaiTap bt = new BaiTap();
        bt.MaBT = isEditMode ? existingId : "BT" + System.currentTimeMillis();
        bt.TenBT = title;
        bt.MoTa = instructions; // Layout mới đã bỏ phần điểm số để đơn giản hóa
        bt.Deadline = dueDate;
        bt.MaLH = currentMaLH;

        if (selectedFileUri != null) {
            bt.FilePath = selectedFileUri.toString();
            bt.FileName = selectedFileName;
        } else {
            bt.FilePath = existingFilePath;
            bt.FileName = existingFileName;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            if (isEditMode) {
                db.baiTapDao().update(bt);
                runOnUiThread(() -> Toast.makeText(this, "Đã cập nhật bài tập!", Toast.LENGTH_SHORT).show());
            } else {
                db.baiTapDao().insert(bt);

                // Gửi thông báo cho HỌC VIÊN
                ThongBao tb = new ThongBao();
                tb.NoiDung = "Có bài tập mới: " + title;
                tb.NgayTao = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
                tb.NguoiNhan = "HOCVIEN"; // Thông báo cho tất cả học viên (hoặc sửa logic để gửi theo lớp)
                db.thongBaoDao().insert(tb);

                runOnUiThread(() -> Toast.makeText(this, "Giao bài tập thành công!", Toast.LENGTH_SHORT).show());
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