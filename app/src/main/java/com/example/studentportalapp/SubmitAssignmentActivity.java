package com.example.studentportalapp;

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
import com.example.studentportalapp.data.Entity.HocVien;
import com.example.studentportalapp.data.Entity.NopBai;
import com.example.studentportalapp.data.Entity.ThongBao;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class SubmitAssignmentActivity extends BaseActivity {

    private EditText etSubmitTitle, etNote;
    private Button btnAttach, btnSubmit;
    private Uri selectedFileUri;
    private String selectedFileName;
    private String maBT;
    private String currentMaHV;

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
        return R.layout.activity_submit_assignment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        currentMaHV = prefs.getString("KEY_USER_ID", "");
        maBT = getIntent().getStringExtra("MA_BT");
        String tenBT = getIntent().getStringExtra("TEN_BT");

        etSubmitTitle = findViewById(R.id.et_submit_title);
        etNote = findViewById(R.id.et_submit_note);
        btnAttach = findViewById(R.id.btn_attach_file_submit);
        btnSubmit = findViewById(R.id.btn_submit_final);

        if (etSubmitTitle != null && tenBT != null) {
            etSubmitTitle.setText(tenBT);
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar_submit);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        btnAttach.setOnClickListener(v -> filePickerLauncher.launch(new String[]{"*/*"}));

        btnSubmit.setOnClickListener(v -> {
            if (selectedFileUri == null) {
                Toast.makeText(this, "Vui lòng đính kèm bài làm!", Toast.LENGTH_SHORT).show();
                return;
            }
            handlePostSubmission();
        });
    }

    private void handlePostSubmission() {
        String note = etNote.getText().toString().trim();
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        NopBai nb = new NopBai();
        nb.MaNB = "NB" + System.currentTimeMillis();
        nb.MaBT = maBT;
        nb.MaHV = currentMaHV;
        nb.FileName = selectedFileName;
        nb.FilePath = selectedFileUri.toString();
        nb.GhiChu = note;
        nb.NgayNop = timeStamp;

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
            database.nopBaiDao().insert(nb);

            // Tìm thông tin học viên để lấy tên
            HocVien hv = database.hocVienDao().getByMaTKSync(currentMaHV);
            String tenHV = (hv != null) ? hv.getTenHV() : "Học viên";

            // Gửi thông báo cho giáo viên
            com.example.studentportalapp.data.Entity.BaiTap bt = database.baiTapDao().getByIdSync(maBT);
            if (bt != null) {
                com.example.studentportalapp.data.Entity.LopHoc lh = database.lopHocDao().getByIdSync(bt.MaLH);
                if (lh != null) {
                    ThongBao tb = new ThongBao();
                    tb.NoiDung = tenHV + " đã nộp bài: " + bt.TenBT;
                    tb.NgayTao = timeStamp;
                    tb.NguoiNhan = lh.MaGV; 
                    database.thongBaoDao().insert(tb);
                }
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Nộp bài thành công!", Toast.LENGTH_SHORT).show();
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
