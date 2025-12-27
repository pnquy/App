package com.example.studentportalapp.data.Repository.user;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Dao.BaiTapDao;
import com.example.studentportalapp.data.Entity.BaiTap;

import java.util.List;

public class UserBaiTapRepository {
    private final BaiTapDao baiTapDao;

    public UserBaiTapRepository(Application app) {
        baiTapDao = AppDatabase.getDatabase(app).baiTapDao();
    }
    public LiveData<BaiTap> getBaiTapById(String MaBT) {
        return baiTapDao.getById(MaBT);
    }
    public LiveData<List<BaiTap>> getAllBaiTap() {
        return baiTapDao.getAll();
    }
}
