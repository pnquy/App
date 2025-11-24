package com.example.studentportalapp.data.Repository.user;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Dao.GiaoVienDao;
import com.example.studentportalapp.data.Entity.GiaoVien;

import java.util.List;

public class UserGiaoVienRepository {

    private final GiaoVienDao giaoVienDao;

    public UserGiaoVienRepository(Application app) {
        giaoVienDao = AppDatabase.getDatabase(app).giaoVienDao();
    }

    public LiveData<GiaoVien> getGiaoVienById(String id) {
        return giaoVienDao.getById(id);
    }

    public LiveData<List<GiaoVien>> getAllGiaoVien() {
        return giaoVienDao.getAll();
    }
}
