package com.example.studentportalapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportalapp.adapter.SubmissionsAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.NopBai;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class ViewSubmissionsActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private AppDatabase db;
    private String maBT;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_view_submissions;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = AppDatabase.getDatabase(getApplicationContext());
        maBT = getIntent().getStringExtra("MA_BT");
        String tenBT = getIntent().getStringExtra("TEN_BT");

        MaterialToolbar toolbar = findViewById(R.id.toolbar_view_submissions);
        if (tenBT != null) toolbar.setTitle("Bài nộp: " + tenBT);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.rv_submissions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();
    }

    private void loadData() {
        db.nopBaiDao().getByBaiTap(maBT).observe(this, list -> {
            if (list == null) list = new ArrayList<>();
            SubmissionsAdapter adapter = new SubmissionsAdapter(this, list, this::onSubmissionClick);
            recyclerView.setAdapter(adapter);
        });
    }

    private void onSubmissionClick(NopBai nb) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chi tiết bài nộp");
        String msg = "Mã HV: " + nb.MaHV + "\nNgày nộp: " + nb.NgayNop + "\nGhi chú: " + (nb.GhiChu != null ? nb.GhiChu : "");
        builder.setMessage(msg);

        if (nb.FilePath != null) {
            builder.setPositiveButton("Mở bài làm", (dialog, which) -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(nb.FilePath), "*/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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