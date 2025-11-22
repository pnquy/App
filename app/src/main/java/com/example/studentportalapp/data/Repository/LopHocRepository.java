package com.example.studentportalapp.data.Repository;

import android.app.Application;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.LopHoc;
import com.example.studentportalapp.data.Dao.LopHocDao;

import java.util.List;

public class LopHocRepository {

    private final LopHocDao lopHocDao;

    public LopHocRepository(Application app) {
        lopHocDao = AppDatabase.getDatabase(app).lopHocDao();
    }

    public void insert(LopHoc lh) {
        AppDatabase.databaseWriteExecutor.execute(() -> lopHocDao.insert(lh));
    }

    public void update(LopHoc lh) {
        AppDatabase.databaseWriteExecutor.execute(() -> lopHocDao.update(lh));
    }

    public List<LopHoc> getAll() {
        return lopHocDao.getAll().getValue();
    }
}
