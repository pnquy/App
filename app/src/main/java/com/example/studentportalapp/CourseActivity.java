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

public class CourseActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private TextView tvTitle, tvSubtitle;
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

        tvTitle = findViewById(R.id.tv_title);
        tvSubtitle = findViewById(R.id.tv_subtitle);
        recyclerView = findViewById(R.id.rvActivityCourse);
        FloatingActionButton fab = findViewById(R.id.fab_add);

        tvTitle.setText(tenLH);
        tvSubtitle.setText("Mã lớp: " + currentMaLH);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if ("GIAOVIEN".equals(role)) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> startActivity(new Intent(this, AddLectureActivity.class)));
        } else {
            fab.setVisibility(View.GONE);
        }

        loadData();
    }

    private void loadData() {
        db.baiGiangDao().getByLop(currentMaLH).observe(this, listBG -> {
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
        });
    }

    private void showLectureDialog(BaiGiang bg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(bg.TenBG);

        String msg = "Nội dung: " + (bg.NoiDung == null ? "" : bg.NoiDung);
        if (bg.FileName != null && !bg.FileName.isEmpty()) {
            msg += "\n\nTệp đính kèm: " + bg.FileName;
        }
        builder.setMessage(msg);

        if (bg.FilePath != null && !bg.FilePath.isEmpty()) {
            builder.setPositiveButton("Mở File", (dialog, which) -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(bg.FilePath), "*/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, "Mở bằng"));
                } catch (Exception e) {
                    Toast.makeText(this, "Không thể mở file", Toast.LENGTH_SHORT).show();
                }
            });
        }

        builder.setNegativeButton("Đóng", null);
        builder.show();
    }
}