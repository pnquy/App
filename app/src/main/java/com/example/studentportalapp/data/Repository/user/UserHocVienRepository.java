package com.example.studentportalapp.data.Repository.user;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Dao.HocVienDao;
import com.example.studentportalapp.data.Entity.HocVien;

import java.util.List;

public class UserHocVienRepository {

    private final HocVienDao hocVienDao;

    public UserHocVienRepository(Application app) {
        hocVienDao = AppDatabase.getDatabase(app).hocVienDao();
    }

    public LiveData<HocVien> getHocVienById(String id) {
        return hocVienDao.getById(id);
    }

    public LiveData<List<HocVien>> getAllHocVien() {
        return hocVienDao.getAll();
    }
}
