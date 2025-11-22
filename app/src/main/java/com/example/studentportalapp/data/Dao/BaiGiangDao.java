package com.example.studentportalapp.data.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentportalapp.data.Entity.BaiGiang;

import java.util.List;

@Dao
public interface BaiGiangDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BaiGiang bg);

    @Update
    void update(BaiGiang bg);

    @Delete
    void delete(BaiGiang bg);

    @Query("SELECT * FROM BAIGIANG WHERE MaLH = :maLH")
    LiveData<List<BaiGiang>> getByLop(String maLH);

    @Query("SELECT * FROM BAIGIANG")
    LiveData<List<BaiGiang>> getAll();
}

