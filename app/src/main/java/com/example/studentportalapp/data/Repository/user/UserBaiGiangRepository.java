package com.example.studentportalapp.data.Repository.user;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Dao.BaiGiangDao;
import com.example.studentportalapp.data.Entity.BaiGiang;

import java.util.List;

public class UserBaiGiangRepository {

    private final BaiGiangDao baiGiangDao;

    public UserBaiGiangRepository(Application app) {
        baiGiangDao = AppDatabase.getDatabase(app).baiGiangDao();
    }

    public LiveData<BaiGiang> getBaiGiangById(String maBG) {
        return baiGiangDao.getById(maBG);
    }

    public LiveData<List<BaiGiang>> getAllBaiGiang() {
        return baiGiangDao.getAll();
    }
}

