package com.example.studentportalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.adapter.AssignmentAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.BaiTap;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class AssignmentActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private AppDatabase db;
    private String currentMaLH;

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
        String role = prefs.getString("KEY_ROLE", "");

        recyclerView = findViewById(R.id.recyclerViewAssignments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FloatingActionButton fab = findViewById(R.id.fab_add_assignment);

        if ("GIAOVIEN".equals(role)) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> startActivity(new Intent(this, AddAssignmentActivity.class)));
        } else {
            fab.setVisibility(View.GONE);
        }

        loadData();
    }

    private void loadData() {
        db.baiTapDao().getByLop(currentMaLH).observe(this, listBT -> {
            if (listBT == null) listBT = new ArrayList<>();
            AssignmentAdapter adapter = new AssignmentAdapter(this, listBT, this::showAssignmentDialog);
            recyclerView.setAdapter(adapter);
        });
    }

    private void showAssignmentDialog(BaiTap bt) {
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
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(bt.FilePath), "*/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, "Mở file"));
                } catch (Exception e) {
                    Toast.makeText(this, "Không thể mở file", Toast.LENGTH_SHORT).show();
                }
            });
        }

        builder.setPositiveButton("OK", null);
        builder.show();
    }
}