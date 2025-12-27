package com.example.studentportalapp.data.Repository.user;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Dao.TaiKhoanDao;
import com.example.studentportalapp.data.Entity.TaiKhoan;

import java.util.List;

public class UserTaiKhoanRepository {
    private final TaiKhoanDao taiKhoanDao;
    public UserTaiKhoanRepository(Application app) {
        taiKhoanDao = AppDatabase.getDatabase(app).taiKhoanDao();
    }
    public LiveData<List<TaiKhoan>> getAllTaiKhoan() {
        return taiKhoanDao.getAll();
    }
    public TaiKhoan getTaiKhoanById(String id) {
        return taiKhoanDao.getById(id);
    }
    public TaiKhoan getTaiKhoanByEmail(String email) {
        return taiKhoanDao.getByEmail(email);
    }
    public int countUsersByRole(String role) {
        return taiKhoanDao.countUsersByRole(role);
    }
}

