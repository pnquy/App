package com.example.studentportalapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.appbar.MaterialToolbar;

public class AddLectureActivity extends BaseActivity {

    private Toolbar toolbar;
    private EditText etLectureTitle;
    private EditText etLectureDescription;
    private Button btnAttachFile;
    private Button btnPostLecture;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_add_lecture;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Ánh xạ (Tìm) các view
        toolbar = findViewById(R.id.toolbar);
        etLectureTitle = findViewById(R.id.et_lecture_title);
        etLectureDescription = findViewById(R.id.et_lecture_description);
        btnAttachFile = findViewById(R.id.btn_attach_file);
        btnPostLecture = findViewById(R.id.btn_post_lecture);

        // 2. Xử lý nút "Close" (X) trên Toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng Activity này và quay lại
            }
        });

        // 3. Xử lý nút "Đăng bài"
        btnPostLecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etLectureTitle.getText().toString().trim();
                String description = etLectureDescription.getText().toString().trim();

                if (title.isEmpty()) {
                    etLectureTitle.setError("Tiêu đề không được để trống");
                    return;
                }

                // --- Logic của bạn để đăng bài lên server/database ---
                // ...

                // Tạm thời hiển thị thông báo
                Toast.makeText(AddLectureActivity.this, "Đã đăng bài: " + title, Toast.LENGTH_SHORT).show();
                finish(); // Đăng xong thì đóng Activity
            }
        });

        // 4. Xử lý nút "Đính kèm tệp" (Sẽ cần logic phức tạp hơn sau)
        btnAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạm thời
                Toast.makeText(AddLectureActivity.this, "Mở trình chọn tệp...", Toast.LENGTH_SHORT).show();
                // Bạn sẽ cần dùng Intent để mở trình quản lý tệp
            }
        });
    }
}