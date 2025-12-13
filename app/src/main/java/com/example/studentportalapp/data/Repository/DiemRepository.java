package com.example.studentportalapp.data.Repository;

import android.app.Application;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.Diem;
import com.example.studentportalapp.data.Dao.DiemDao;

import java.util.List;

public class DiemRepository {

    private final DiemDao diemDao;

    public DiemRepository(Application app) {
        diemDao = AppDatabase.getDatabase(app).diemDao();
    }

    public void insert(Diem diem) {
        AppDatabase.databaseWriteExecutor.execute(() -> diemDao.insert(diem));
    }

    public void update(Diem diem) {
        AppDatabase.databaseWriteExecutor.execute(() -> diemDao.update(diem));
    }

    public List<Diem> getAll() {
        return diemDao.getAll().getValue();
    }
}
