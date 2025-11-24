package com.example.studentportalapp.data.Repository.user;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Dao.DiemDao;
import com.example.studentportalapp.data.Entity.Diem;

import java.util.List;

public class UserDiemRepository {

    private final DiemDao diemDao;

    public UserDiemRepository(Application app) {
        diemDao = AppDatabase.getDatabase(app).diemDao();
    }

    // Lấy tất cả điểm của 1 học viên
    public LiveData<List<Diem>> getDiemByHocVien(String maHV) {
        return diemDao.getByHocVien(maHV);
    }

    // Lấy tất cả điểm của 1 bài tập
    public LiveData<List<Diem>> getDiemByBaiTap(String maBT) {
        return diemDao.getByBaiTap(maBT);
    }

    // Lấy tất cả điểm
    public LiveData<List<Diem>> getAllDiem() {
        return diemDao.getAll();
    }

    // Lấy điểm duy nhất của 1 học viên cho 1 bài tập
    public LiveData<Diem> getDiemHocVienBaiTap(String maHV, String maBT) {
        return diemDao.getByHocVienBaiTap(maHV, maBT);
    }
}
