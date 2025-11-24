package com.example.studentportalapp.data.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentportalapp.data.Entity.LopHoc;

import java.util.List;

@Dao
public interface LopHocDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LopHoc lh);

    @Update
    void update(LopHoc lh);

    @Delete
    void delete(LopHoc lh);

    @Query("SELECT * FROM LOPHOC")
    LiveData<List<LopHoc>> getAll();

    @Query("SELECT * FROM LOPHOC WHERE MaLH = :id")
    LiveData<LopHoc> getById(String id);
}

