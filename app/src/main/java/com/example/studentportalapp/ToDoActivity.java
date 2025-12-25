package com.example.studentportalapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import com.example.studentportalapp.adapter.TaskAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.BaiTap;
import com.example.studentportalapp.data.Entity.LopHoc;
import com.example.studentportalapp.data.Entity.NopBai;
import com.example.studentportalapp.model.Task;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ToDoActivity extends BaseActivity {

    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String currentUserId, currentUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = AppDatabase.getDatabase(getApplicationContext());
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUserId = prefs.getString("KEY_USER_ID", "");
        currentUserRole = prefs.getString("KEY_ROLE", "");

        ListView listView = findViewById(R.id.list_tasks);

        executor.execute(() -> {
            List<Task> tasks = new ArrayList<>();
            if ("HOCVIEN".equals(currentUserRole)) {
                List<LopHoc> enrolledClasses = db.thamGiaDao().getClassesByStudent(currentUserId);
                for (LopHoc lopHoc : enrolledClasses) {
                    List<BaiTap> assignments = db.baiTapDao().getByLopSync(lopHoc.MaLH);
                    for (BaiTap baiTap : assignments) {
                        if (isDeadlineActive(baiTap.Deadline)) {
                            NopBai submission = db.nopBaiDao().getSubmission(baiTap.MaBT, currentUserId);
                            if (submission == null) {
                                tasks.add(new Task(baiTap.TenBT, lopHoc.TenLH, baiTap.Deadline, baiTap.MaBT, currentUserRole));
                            }
                        }
                    }
                }
            } else if ("GIAOVIEN".equals(currentUserRole)) {
                List<LopHoc> taughtClasses = db.lopHocDao().getClassesByTeacher(currentUserId);
                for (LopHoc lopHoc : taughtClasses) {
                    List<BaiTap> assignments = db.baiTapDao().getByLopSync(lopHoc.MaLH);
                    int totalStudents = db.thamGiaDao().countStudentsByClass(lopHoc.MaLH);
                    for (BaiTap baiTap : assignments) {
                        if (isDeadlineActive(baiTap.Deadline)) {
                            int submissionCount = db.nopBaiDao().countSubmissionsForAssignment(baiTap.MaBT);
                            tasks.add(new Task(baiTap.TenBT, lopHoc.TenLH, baiTap.Deadline, baiTap.MaBT, currentUserRole, submissionCount, totalStudents));
                        }
                    }
                }
            }

            runOnUiThread(() -> {
                TaskAdapter adapter = new TaskAdapter(ToDoActivity.this, tasks);
                listView.setAdapter(adapter);
            });
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_todo;
    }

    private boolean isDeadlineActive(String deadline) {
        if (deadline == null || deadline.isEmpty()) {
            return true; // Mặc định là còn hạn nếu không có deadline
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            Date deadlineDate = sdf.parse(deadline);
            return new Date().before(deadlineDate); // Trả về true nếu ngày hiện tại trước deadline
        } catch (ParseException e) {
            e.printStackTrace();
            return true; // Mặc định là còn hạn nếu không thể phân tích chuỗi
        }
    }
}
