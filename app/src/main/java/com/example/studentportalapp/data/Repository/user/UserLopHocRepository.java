package com.example.studentportalapp.data.Repository.user;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Dao.LopHocDao;
import com.example.studentportalapp.data.Entity.LopHoc;

import java.util.List;

public class UserLopHocRepository {

    private final LopHocDao lopHocDao;

    public UserLopHocRepository(Application app) {
        lopHocDao = AppDatabase.getDatabase(app).lopHocDao();
    }

    public LiveData<LopHoc> getLopHocById(String id) {
        return lopHocDao.getById(id);
    }

    public LiveData<List<LopHoc>> getAllLopHoc() {
        return lopHocDao.getAll();
    }
}
