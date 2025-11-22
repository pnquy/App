package com.example.studentportalapp.data.Repository;

import android.app.Application;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.TaiKhoan;
import com.example.studentportalapp.data.Dao.TaiKhoanDao;

import java.util.List;

public class TaiKhoanRepository {

    private final TaiKhoanDao taiKhoanDao;

    public TaiKhoanRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        taiKhoanDao = db.taiKhoanDao();
    }

    public void insert(TaiKhoan tk) {
        AppDatabase.databaseWriteExecutor.execute(() -> taiKhoanDao.insert(tk));
    }

    public void update(TaiKhoan tk) {
        AppDatabase.databaseWriteExecutor.execute(() -> taiKhoanDao.update(tk));
    }

    public void delete(TaiKhoan tk) {
        AppDatabase.databaseWriteExecutor.execute(() -> taiKhoanDao.delete(tk));
    }

    public List<TaiKhoan> getAll() {
        return taiKhoanDao.getAll().getValue();
    }


}
