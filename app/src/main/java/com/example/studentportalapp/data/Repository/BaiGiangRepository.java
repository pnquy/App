package com.example.studentportalapp.data.Repository;

import android.app.Application;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.BaiGiang;
import com.example.studentportalapp.data.Dao.BaiGiangDao;

import java.util.List;

public class BaiGiangRepository {

    private final BaiGiangDao baiGiangDao;

    public BaiGiangRepository(Application app) {
        baiGiangDao = AppDatabase.getDatabase(app).baiGiangDao();
    }

    public void insert(BaiGiang bg) {
        AppDatabase.databaseWriteExecutor.execute(() -> baiGiangDao.insert(bg));
    }

    public void update(BaiGiang bg) {
        AppDatabase.databaseWriteExecutor.execute(() -> baiGiangDao.update(bg));
    }

    public List<BaiGiang> getAll() {
        return baiGiangDao.getAll().getValue();
    }
}
