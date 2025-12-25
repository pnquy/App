package com.example.studentportalapp.data.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentportalapp.data.Entity.Diem;

import java.util.List;

@Dao
public interface DiemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Diem diem);

    @Update
    void update(Diem diem);

    @Delete
    void delete(Diem diem);

    @Query("SELECT * FROM DIEM WHERE MaHV = :maHV")
    LiveData<List<Diem>> getByHocVien(String maHV);

    @Query("SELECT * FROM DIEM WHERE MaBT = :maBT")
    LiveData<List<Diem>> getByBaiTap(String maBT);

    @Query("SELECT * FROM DIEM WHERE MaBT = :maBT")
    List<Diem> getByBaiTapSync(String maBT);

    @Query("SELECT * FROM DIEM")
    LiveData<List<Diem>> getAll();

    @Query("SELECT * FROM DIEM WHERE MaHV = :maHV AND MaBT = :maBT LIMIT 1")
    LiveData<Diem> getByHocVienBaiTap(String maHV, String maBT);
}