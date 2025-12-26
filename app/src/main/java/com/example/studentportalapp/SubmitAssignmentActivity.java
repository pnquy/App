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
import java.util.Date;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.HocVien;
import com.example.studentportalapp.data.Entity.NopBai;
import com.example.studentportalapp.data.Entity.ThongBao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class SubmitAssignmentActivity extends BaseActivity {

    private EditText etSubmitTitle, etNote;
    private View btnAttach; // Đổi thành View vì là LinearLayout
    private TextView tvFileName; // Thêm TextView hiển thị tên file
    private Button btnSubmit;
    private Uri selectedFileUri;
    private TextView tvDeadlineWarning;
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

                    // Cập nhật giao diện khi chọn file xong
                    if (tvFileName != null) {
                        tvFileName.setText("Đã chọn: " + selectedFileName);
                        tvFileName.setTextColor(getResources().getColor(R.color.purple_500));
                    }
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

        // Ánh xạ theo ID mới
        etSubmitTitle = findViewById(R.id.et_submit_title);
        etNote = findViewById(R.id.et_submit_note);
        btnAttach = findViewById(R.id.btn_attach_file_submit);
        tvFileName = findViewById(R.id.tvFileName); // Ánh xạ TextView tên file
        btnSubmit = findViewById(R.id.btn_submit_final);
        View btnBack = findViewById(R.id.btnBack);
        tvDeadlineWarning = findViewById(R.id.tvDeadlineWarning);
        if (etSubmitTitle != null && tenBT != null) {
            etSubmitTitle.setText(tenBT);
        }

        // Xử lý nút Back
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Xử lý chọn file
        btnAttach.setOnClickListener(v -> filePickerLauncher.launch(new String[]{"*/*"}));

        // Xử lý submit
        btnSubmit.setOnClickListener(v -> {
            if (selectedFileUri == null) {
                Toast.makeText(this, "Vui lòng đính kèm bài làm!", Toast.LENGTH_SHORT).show();
                return;
            }
            handlePostSubmission();
        });
        checkDeadline();
    }
    private void checkDeadline() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            com.example.studentportalapp.data.Entity.BaiTap bt = db.baiTapDao().getByIdSync(maBT);

            if (bt != null && bt.Deadline != null && !bt.Deadline.isEmpty()) {
                try {
                    // Giả sử định dạng ngày là dd/MM/yyyy
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                    // Set giờ deadline là cuối ngày (23:59:59) để so sánh chính xác
                    Date deadlineDate = sdf.parse(bt.Deadline);
                    deadlineDate.setHours(23);
                    deadlineDate.setMinutes(59);
                    deadlineDate.setSeconds(59);

                    Date currentDate = new Date();

                    runOnUiThread(() -> {
                        if (currentDate.after(deadlineDate)) {
                            // QUÁ HẠN
                            btnSubmit.setEnabled(false);
                            btnSubmit.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
                            btnAttach.setEnabled(false); // Không cho chọn file
                            btnAttach.setAlpha(0.5f);
                            etNote.setEnabled(false);

                            tvDeadlineWarning.setVisibility(View.VISIBLE);
                            tvDeadlineWarning.setText("Đã quá hạn nộp bài (" + bt.Deadline + "). Bạn không thể nộp.");
                        } else {
                            // CÒN HẠN
                            tvDeadlineWarning.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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

            HocVien hv = database.hocVienDao().getByMaTKSync(currentMaHV);
            String tenHV = (hv != null) ? hv.getTenHV() : "Học viên";

            com.example.studentportalapp.data.Entity.BaiTap bt = database.baiTapDao().getByIdSync(maBT);
            if (bt != null) {
                com.example.studentportalapp.data.Entity.LopHoc lh = database.lopHocDao().getByIdSync(bt.MaLH);
                if (lh != null) {
                    ThongBao tb = new ThongBao();
                    tb.NoiDung = tenHV + " đã nộp bài: " + bt.TenBT;
                    tb.NgayTao = timeStamp;
                    tb.NguoiNhan = lh.MaGV;
                    // SỬA LỖI: Thêm 2 dòng này để link hoạt động
                    tb.LoaiTB = "SUBMISSION"; 
                    tb.TargetId = maBT; 
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
