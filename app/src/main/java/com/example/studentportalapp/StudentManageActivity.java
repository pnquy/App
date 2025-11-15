package com.example.studentportalapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportalapp.adapter.HocVienAdapter;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.HocVien;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentManageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HocVienAdapter adapter;
    private List<HocVien> hocVienList = new ArrayList<>();
    private AppDatabase db;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private EditText etMaHV, etTenHV, etEmailHV, etLopHV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_manage);

        recyclerView = findViewById(R.id.recyclerHocVien);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = AppDatabase.getDatabase(getApplicationContext());

        loadHocVien();

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {

            finish();


        });
    }



    private void loadHocVien() {
        executor.execute(() -> {
            hocVienList = db.hocVienDao().getAll();
            runOnUiThread(() -> {
                adapter = new HocVienAdapter(this, hocVienList, new HocVienAdapter.OnItemClickListener() {
                    @Override
                    public void onEdit(HocVien hv) {
                        etMaHV.setText(hv.getMaHV());
                        etTenHV.setText(hv.getHoTen());
                        etEmailHV.setText(hv.getEmail());
                        etLopHV.setText(hv.getMaLop());

                        executor.execute(() -> {
                            db.hocVienDao().update(hv);
                            runOnUiThread(() -> {
                                Toast.makeText(StudentManageActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                loadHocVien();
                            });
                        });
                    }

                    @Override
                    public void onDelete(HocVien hv) {
                        executor.execute(() -> {
                            db.hocVienDao().delete(hv);
                            runOnUiThread(() -> {
                                Toast.makeText(StudentManageActivity.this, "Đã xóa!", Toast.LENGTH_SHORT).show();
                                loadHocVien();
                            });
                        });
                    }
                });
                recyclerView.setAdapter(adapter);
            });
        });
    }
}
