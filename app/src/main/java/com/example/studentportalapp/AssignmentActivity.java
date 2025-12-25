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
import com.example.studentportalapp.adapter.AssignmentAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.BaiTap;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class AssignmentActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private AppDatabase db;
    private String currentMaLH;
    private TextView tvTitle, tvSubtitle;
    private View layoutEmptyState;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_assignment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = AppDatabase.getDatabase(getApplicationContext());
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentMaLH = prefs.getString("CURRENT_CLASS_ID", "");
        String tenLH = prefs.getString("CURRENT_CLASS_NAME", "Lớp học");
        String role = prefs.getString("KEY_ROLE", "");

        tvTitle = findViewById(R.id.tv_title);
        tvSubtitle = findViewById(R.id.tv_subtitle);
        recyclerView = findViewById(R.id.recyclerViewAssignments);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        FloatingActionButton fab = findViewById(R.id.fab_add_assignment);
        View btnHomeLogo = findViewById(R.id.btnHomeLogo);

        if (tvTitle != null) tvTitle.setText(tenLH);
        if (tvSubtitle != null) tvSubtitle.setText("Mã lớp: " + currentMaLH);

        if (btnHomeLogo != null) {
            btnHomeLogo.setOnClickListener(v -> {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if ("GIAOVIEN".equals(role)) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> startActivity(new Intent(this, AddAssignmentActivity.class)));
        } else {
            fab.setVisibility(View.GONE);
        }

        loadData();
        View btnNotiHeader = findViewById(R.id.btnNotiHeader);

        if (btnNotiHeader != null) {
            btnNotiHeader.setOnClickListener(v -> {
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
            });
        }
    }

    private void loadData() {
        db.baiTapDao().getByLop(currentMaLH).observe(this, listBT -> {
            if (listBT == null || listBT.isEmpty()) {
                listBT = new ArrayList<>();
                if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            AssignmentAdapter adapter = new AssignmentAdapter(this, listBT, this::showAssignmentDialog);
            recyclerView.setAdapter(adapter);
        });
    }

    private void showAssignmentDialog(BaiTap bt) {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String role = prefs.getString("KEY_ROLE", "");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(bt.TenBT);

        String msg = "Mô tả: " + bt.MoTa + "\n\nHạn nộp: " + bt.Deadline;
        if (bt.FileName != null) {
            msg += "\n\nFile đề bài: " + bt.FileName;
        }
        builder.setMessage(msg);

        if (bt.FilePath != null && !bt.FilePath.isEmpty()) {
            builder.setNeutralButton("Mở File", (dialog, which) -> {
                try {
                    Uri uri = Uri.parse(bt.FilePath);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String mimeType = getContentResolver().getType(uri);
                    if (mimeType == null) mimeType = "*/*";
                    intent.setDataAndType(uri, mimeType);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(Intent.createChooser(intent, "Mở file bài tập bằng"));
                } catch (Exception e) {
                    Toast.makeText(this, "Lỗi: Không có quyền truy cập file", Toast.LENGTH_LONG).show();
                }
            });
        }

        if ("GIAOVIEN".equals(role)) {
            builder.setPositiveButton("Quản Lý", (dialog, which) -> showTeacherOptions(bt));
        } else if ("HOCVIEN".equals(role)) {
            builder.setPositiveButton("Nộp bài", (dialog, which) -> {
                Intent intent = new Intent(this, SubmitAssignmentActivity.class);
                intent.putExtra("MA_BT", bt.MaBT);
                intent.putExtra("TEN_BT", bt.TenBT);
                startActivity(intent);
            });
        }

        builder.setNegativeButton("Đóng", null);
        builder.show();
    }

    private void showTeacherOptions(BaiTap bt) {
        String[] options = {"Xem bài nộp", "Chỉnh sửa", "Xóa bài tập"};

        new AlertDialog.Builder(this)
                .setTitle("Tùy chọn quản lý")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Intent intent = new Intent(this, ViewSubmissionsActivity.class);
                            intent.putExtra("MA_BT", bt.MaBT);
                            intent.putExtra("TEN_BT", bt.TenBT);
                            startActivity(intent);
                            break;
                        case 1:
                            Intent editIntent = new Intent(this, AddAssignmentActivity.class);
                            editIntent.putExtra("EDIT_ID", bt.MaBT);
                            editIntent.putExtra("EDIT_TITLE", bt.TenBT);
                            editIntent.putExtra("EDIT_DESC", bt.MoTa);
                            editIntent.putExtra("EDIT_DATE", bt.Deadline);
                            editIntent.putExtra("EDIT_FILE_PATH", bt.FilePath);
                            editIntent.putExtra("EDIT_FILE_NAME", bt.FileName);
                            startActivity(editIntent);
                            break;
                        case 2:
                            confirmDelete(bt);
                            break;
                    }
                })
                .show();
    }

    private void confirmDelete(BaiTap bt) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa bài tập \"" + bt.TenBT + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.baiTapDao().delete(bt);
                        runOnUiThread(() -> Toast.makeText(this, "Đã xóa bài tập!", Toast.LENGTH_SHORT).show());
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}