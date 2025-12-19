package com.example.studentportalapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.BaiTap;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AddAssignmentActivity extends BaseActivity {

    private MaterialToolbar toolbar;
    private EditText etTitle, etInstructions, etPoints, etDueDate;
    private Button btnAttach, btnAssign;
    private Calendar myCalendar;
    private Uri selectedFileUri;
    private String selectedFileName;
    private String currentMaLH;

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
                    btnAttach.setText("Đã chọn: " + selectedFileName);
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

        toolbar = findViewById(R.id.toolbar_assignment);
        etTitle = findViewById(R.id.et_assignment_title);
        etInstructions = findViewById(R.id.et_assignment_instructions);
        etPoints = findViewById(R.id.et_assignment_points);
        etDueDate = findViewById(R.id.et_assignment_due_date);
        btnAttach = findViewById(R.id.btn_attach_file_assignment);
        btnAssign = findViewById(R.id.btn_assign_assignment);

        myCalendar = Calendar.getInstance();

        toolbar.setNavigationOnClickListener(v -> finish());

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

        btnAttach.setOnClickListener(v -> filePickerLauncher.launch(new String[]{"*/*"}));

        btnAssign.setOnClickListener(v -> handleAssign());
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        etDueDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void handleAssign() {
        String title = etTitle.getText().toString().trim();
        String instructions = etInstructions.getText().toString().trim();
        String points = etPoints.getText().toString().trim();
        String dueDate = etDueDate.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Vui lòng nhập tiêu đề");
            return;
        }
        if (dueDate.isEmpty()) {
            etDueDate.setError("Vui lòng chọn hạn nộp");
            return;
        }

        BaiTap bt = new BaiTap();
        bt.MaBT = "BT" + System.currentTimeMillis();
        bt.TenBT = title;
        bt.MoTa = instructions + (points.isEmpty() ? "" : " (Điểm: " + points + ")");
        bt.Deadline = dueDate;
        bt.MaLH = currentMaLH;

        if (selectedFileUri != null) {
            bt.FilePath = selectedFileUri.toString();
            bt.FileName = selectedFileName;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getDatabase(getApplicationContext()).baiTapDao().insert(bt);
            runOnUiThread(() -> {
                Toast.makeText(this, "Giao bài tập thành công!", Toast.LENGTH_SHORT).show();
                finish();
            });
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