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
    public LiveData<List<Diem>> getDiemByHocVien(String maHV) {
        return diemDao.getByHocVien(maHV);
    }
    public LiveData<List<Diem>> getDiemByBaiTap(String maBT) {
        return diemDao.getByBaiTap(maBT);
    }
    public LiveData<List<Diem>> getAllDiem() {
        return diemDao.getAll();
    }
    public LiveData<Diem> getDiemHocVienBaiTap(String maHV, String maBT) {
        return diemDao.getByHocVienBaiTap(maHV, maBT);
    }
}
