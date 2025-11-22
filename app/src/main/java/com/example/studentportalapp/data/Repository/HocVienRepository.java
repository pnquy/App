package com.example.studentportalapp.data.Repository;

import android.app.Application;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.HocVien;
import com.example.studentportalapp.data.Dao.HocVienDao;

import java.util.List;

public class HocVienRepository {

    private final HocVienDao hocVienDao;

    public HocVienRepository(Application app) {
        hocVienDao = AppDatabase.getDatabase(app).hocVienDao();
    }

    public void insert(HocVien hv) {
        AppDatabase.databaseWriteExecutor.execute(() -> hocVienDao.insert(hv));
    }

    public void update(HocVien hv) {
        AppDatabase.databaseWriteExecutor.execute(() -> hocVienDao.update(hv));
    }

    public List<HocVien> getAll() {
        return hocVienDao.getAll().getValue();
    }
}
