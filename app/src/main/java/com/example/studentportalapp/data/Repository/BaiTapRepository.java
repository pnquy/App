package com.example.studentportalapp.data.Repository;

import android.app.Application;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.BaiTap;
import com.example.studentportalapp.data.Dao.BaiTapDao;

import java.util.List;

public class BaiTapRepository {

    private final BaiTapDao baiTapDao;

    public BaiTapRepository(Application app) {
        baiTapDao = AppDatabase.getDatabase(app).baiTapDao();
    }

    public void insert(BaiTap bt) {
        AppDatabase.databaseWriteExecutor.execute(() -> baiTapDao.insert(bt));
    }

    public void update(BaiTap bt) {
        AppDatabase.databaseWriteExecutor.execute(() -> baiTapDao.update(bt));
    }

    public List<BaiTap> getAll() {
        return baiTapDao.getAll().getValue();
    }
}
