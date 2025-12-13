package com.example.studentportalapp.data.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentportalapp.data.Entity.HocVien;

import java.util.List;

@Dao
public interface HocVienDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HocVien hv);

    @Update
    void update(HocVien hv);

    @Delete
    void delete(HocVien hv);

    @Query("SELECT * FROM HOCVIEN")
    LiveData<List<HocVien>> getAll();
    @Query("SELECT COUNT(*) FROM HOCVIEN WHERE MaLH = :maLH")
    int countStudentsByClass(String maLH);
    @Query("SELECT * FROM HOCVIEN WHERE MaHV = :id")
    LiveData<HocVien> getById(String id);
}

