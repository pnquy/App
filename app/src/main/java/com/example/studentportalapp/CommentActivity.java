package com.example.studentportalapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportalapp.adapter.CommentAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.BinhLuan;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class CommentActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private EditText etComment;
    private ImageView btnSend;
    private AppDatabase db;
    
    private String targetId;
    private String targetType;
    private String currentUserId;
    private String currentUserName;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_comment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = AppDatabase.getDatabase(getApplicationContext());
        SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        currentUserId = prefs.getString("KEY_USER_ID", "");
        currentUserName = prefs.getString("KEY_NAME", "User");

        targetId = getIntent().getStringExtra("TARGET_ID");
        targetType = getIntent().getStringExtra("TARGET_TYPE"); // "ASSIGNMENT" or "LECTURE"

        MaterialToolbar toolbar = findViewById(R.id.toolbar_comment);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setTitle("Bình luận");

        recyclerView = findViewById(R.id.rv_comments);
        etComment = findViewById(R.id.et_comment_input);
        btnSend = findViewById(R.id.btn_send_comment);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db.binhLuanDao().getComments(targetId, targetType).observe(this, list -> {
            if (list == null) list = new ArrayList<>();
            CommentAdapter adapter = new CommentAdapter(CommentActivity.this, list);
            recyclerView.setAdapter(adapter);
            if (!list.isEmpty()) {
                recyclerView.smoothScrollToPosition(list.size() - 1);
            }
        });

        btnSend.setOnClickListener(v -> {
            String content = etComment.getText().toString().trim();
            if (content.isEmpty()) return;

            BinhLuan bl = new BinhLuan();
            bl.MaBL = "BL" + System.currentTimeMillis();
            bl.NoiDung = content;
            bl.NgayTao = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
            bl.MaNguoiGui = currentUserId;
            bl.TenNguoiGui = currentUserName;
            bl.TargetId = targetId;
            bl.TargetType = targetType;

            Executors.newSingleThreadExecutor().execute(() -> {
                db.binhLuanDao().insert(bl);
                runOnUiThread(() -> etComment.setText(""));
            });
        });
    }
}
