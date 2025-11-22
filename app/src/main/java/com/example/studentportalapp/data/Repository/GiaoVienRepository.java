package com.example.studentportalapp.data.Repository;

import android.app.Application;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.GiaoVien;
import com.example.studentportalapp.data.Dao.GiaoVienDao;

import java.util.List;

public class GiaoVienRepository {

    private final GiaoVienDao giaoVienDao;

    public GiaoVienRepository(Application app) {
        giaoVienDao = AppDatabase.getDatabase(app).giaoVienDao();
    }

    public void insert(GiaoVien gv) {
        AppDatabase.databaseWriteExecutor.execute(() -> giaoVienDao.insert(gv));
    }

    public void update(GiaoVien gv) {
        AppDatabase.databaseWriteExecutor.execute(() -> giaoVienDao.update(gv));
    }

    public List<GiaoVien> getAll() {
        return (List<GiaoVien>) giaoVienDao.getAll();
    }
}
