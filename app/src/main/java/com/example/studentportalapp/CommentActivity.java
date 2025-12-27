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
import com.example.studentportalapp.data.Entity.BaiGiang;
import com.example.studentportalapp.data.Entity.BaiTap;
import com.example.studentportalapp.data.Entity.BinhLuan;
import com.example.studentportalapp.data.Entity.LopHoc;
import com.example.studentportalapp.data.Entity.ThongBao;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        targetType = getIntent().getStringExtra("TARGET_TYPE");

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

                String notiContent = "";
                String notiType = "";
                String maLH = "";
                String teacherId = "";

                if ("ASSIGNMENT".equals(targetType)) {
                    BaiTap bt = db.baiTapDao().getByIdSync(targetId);
                    if (bt != null) {
                        maLH = bt.MaLH;
                        notiContent = currentUserName + " đã bình luận về bài tập: " + bt.TenBT;
                        notiType = "COMMENT_ASSIGNMENT";
                    }
                } else if ("LECTURE".equals(targetType)) {
                    BaiGiang bg = db.baiGiangDao().getByIdSync(targetId);
                    if (bg != null) {
                        maLH = bg.MaLH;
                        notiContent = currentUserName + " đã bình luận về bài giảng: " + bg.TenBG;
                        notiType = "COMMENT_LECTURE";
                    }
                }

                if (!maLH.isEmpty()) {
                    LopHoc lh = db.lopHocDao().getByIdSync(maLH);
                    if (lh != null) {
                        teacherId = lh.MaGV;
                    }
                }
                
                if (!teacherId.isEmpty() && currentUserId.equals(teacherId)) {
                    List<String> studentIds = db.thamGiaDao().getStudentIdsByClass(maLH);
                    if (studentIds != null) {
                        for (String studentId : studentIds) {
                            if (!studentId.equals(currentUserId)) {
                                ThongBao tb = new ThongBao();
                                tb.NgayTao = bl.NgayTao;
                                tb.TargetId = targetId;
                                tb.NoiDung = notiContent;
                                tb.LoaiTB = notiType;
                                tb.NguoiNhan = studentId;
                                db.thongBaoDao().insert(tb);
                            }
                        }
                    }
                } else if (!teacherId.isEmpty()) {
                     if (!teacherId.equals(currentUserId)) {
                        ThongBao tb = new ThongBao();
                        tb.NgayTao = bl.NgayTao;
                        tb.TargetId = targetId;
                        tb.NoiDung = notiContent;
                        tb.LoaiTB = notiType;
                        tb.NguoiNhan = teacherId;
                        db.thongBaoDao().insert(tb);
                     }
                }

                runOnUiThread(() -> etComment.setText(""));
            });
        });
    }
}
