package com.example.studentportalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.adapter.CourseAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.BaiGiang;
import com.example.studentportalapp.model.ActivityItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class CourseActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private TextView tvTitle, tvSubtitle;
    private View layoutEmptyState; // View thông báo rỗng
    private View btnBack; // Nút Back
    private AppDatabase db;
    private String currentMaLH;
    private ArrayList<BaiGiang> originalList;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_course;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getDatabase(getApplicationContext());

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentMaLH = prefs.getString("CURRENT_CLASS_ID", "");
        String tenLH = prefs.getString("CURRENT_CLASS_NAME", "Lớp học");
        String role = prefs.getString("KEY_ROLE", "");

        // Ánh xạ View
        tvTitle = findViewById(R.id.tv_title);
        tvSubtitle = findViewById(R.id.tv_subtitle);
        recyclerView = findViewById(R.id.rvActivityCourse);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        btnBack = findViewById(R.id.btnBack);
        FloatingActionButton fab = findViewById(R.id.fab_add);

        // Setup UI
        tvTitle.setText(tenLH);
        tvSubtitle.setText("Mã lớp: " + currentMaLH);

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Phân quyền Button Thêm
        if ("GIAOVIEN".equals(role)) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> startActivity(new Intent(this, AddLectureActivity.class)));
        } else {
            fab.setVisibility(View.GONE);
        }

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // Reload khi quay lại từ màn hình thêm/sửa
    }

    private void loadData() {
        db.baiGiangDao().getByLop(currentMaLH).observe(this, listBG -> {
            if (listBG == null || listBG.isEmpty()) {
                // Nếu không có dữ liệu -> Hiện Empty State, Ẩn List
                layoutEmptyState.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                // Có dữ liệu -> Ẩn Empty State, Hiện List
                layoutEmptyState.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                originalList = new ArrayList<>(listBG);
                ArrayList<ActivityItem> items = new ArrayList<>();

                for (BaiGiang bg : listBG) {
                    String fName = (bg.FileName != null) ? bg.FileName : "";
                    items.add(new ActivityItem(
                            "GV: " + bg.MaGV,
                            "Mới đăng",
                            bg.TenBG,
                            fName,
                            "0"
                    ));
                }

                CourseAdapter adapter = new CourseAdapter(items, position -> showLectureDialog(originalList.get(position)));
                recyclerView.setAdapter(adapter);
            }
        });
    }

    // ... (Giữ nguyên các hàm showLectureDialog, showTeacherOptions, confirmDelete bên dưới) ...
    private void showLectureDialog(BaiGiang bg) {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String role = prefs.getString("KEY_ROLE", "");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(bg.TenBG);

        String msg = "Nội dung: " + (bg.NoiDung == null ? "" : bg.NoiDung);
        if (bg.FileName != null && !bg.FileName.isEmpty()) {
            msg += "\n\nTệp đính kèm: " + bg.FileName;
        }
        builder.setMessage(msg);

        if (bg.FilePath != null && !bg.FilePath.isEmpty()) {
            builder.setNeutralButton("Mở File", (dialog, which) -> {
                try {
                    Uri uri = Uri.parse(bg.FilePath);
                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    String mimeType = getContentResolver().getType(uri);
                    if (mimeType == null) mimeType = "*/*";

                    intent.setDataAndType(uri, mimeType);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    startActivity(Intent.createChooser(intent, "Mở bài giảng bằng"));
                } catch (Exception e) {
                    Toast.makeText(this, "Lỗi: File không tồn tại hoặc không có quyền truy cập", Toast.LENGTH_LONG).show();
                }
            });
        }

        if ("GIAOVIEN".equals(role)) {
            builder.setPositiveButton("Quản lý", (dialog, which) -> showTeacherOptions(bg));
        }

        builder.setNegativeButton("Đóng", null);
        builder.show();
    }

    private void showTeacherOptions(BaiGiang bg) {
        String[] options = {"Chỉnh sửa", "Xóa bài giảng"};

        new AlertDialog.Builder(this)
                .setTitle("Tùy chọn quản lý")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Intent intent = new Intent(this, AddLectureActivity.class);
                            intent.putExtra("EDIT_ID", bg.MaBG);
                            intent.putExtra("EDIT_TITLE", bg.TenBG);
                            intent.putExtra("EDIT_CONTENT", bg.NoiDung);
                            intent.putExtra("EDIT_FILE_PATH", bg.FilePath);
                            intent.putExtra("EDIT_FILE_NAME", bg.FileName);
                            startActivity(intent);
                            break;
                        case 1:
                            confirmDelete(bg);
                            break;
                    }
                })
                .show();
    }

    private void confirmDelete(BaiGiang bg) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa bài giảng \"" + bg.TenBG + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.baiGiangDao().delete(bg);
                        runOnUiThread(() -> Toast.makeText(this, "Đã xóa bài giảng!", Toast.LENGTH_SHORT).show());
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}