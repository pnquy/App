package com.example.studentportalapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.adapter.NotificationAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.ThongBao;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.concurrent.Executors;

public class NotificationActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private AppDatabase db;
    private String currentMaTK;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_notification;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = AppDatabase.getDatabase(getApplicationContext());
        SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        currentMaTK = prefs.getString("KEY_USER_ID", "");

        MaterialToolbar toolbar = findViewById(R.id.toolbar_notification);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        recyclerView = findViewById(R.id.rv_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadNotifications();
    }

    private void loadNotifications() {
        SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String role = prefs.getString("KEY_ROLE", "");

        // Lấy thông báo gửi riêng cho mình HOẶC thông báo chung cho vai trò của mình
        db.thongBaoDao().getByNguoiNhan(currentMaTK, role).observe(this, list -> {
            if (list != null) {
                NotificationAdapter adapter = new NotificationAdapter(list);
                recyclerView.setAdapter(adapter);

                // Kiểm tra xem có tin chưa đọc không mới update DB để tránh vòng lặp vô tận (update -> reload -> update)
                boolean hasUnread = false;
                for (ThongBao tb : list) {
                    if (!tb.IsRead) {
                        hasUnread = true;
                        break;
                    }
                }

                if (hasUnread) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.thongBaoDao().markAllAsRead(currentMaTK, role);
                    });
                }
            }
        });
    }
}
