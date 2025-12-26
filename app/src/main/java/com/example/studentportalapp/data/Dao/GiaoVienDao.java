package com.example.studentportalapp.data.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentportalapp.data.Entity.GiaoVien;
import com.example.studentportalapp.data.TeacherItem;

import java.util.List;

@Dao
public interface GiaoVienDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GiaoVien gv);

    @Update
    void update(GiaoVien gv);

    @Delete
    void delete(GiaoVien gv);

    @Query("SELECT * FROM GIAOVIEN")
    LiveData<List<GiaoVien>> getAll();

    @Query("SELECT * FROM GIAOVIEN WHERE MaGV = :id")
    LiveData<GiaoVien> getById(String id);
    
    @Query("SELECT TenGV FROM GIAOVIEN WHERE MaGV = :maGV")
    String getNameById(String maGV);

    @Query("SELECT * FROM GIAOVIEN")
    List<GiaoVien> getAllSync();
    @Query("SELECT MaGV, TenGV AS HoTen, Email FROM GIAOVIEN")
    List<TeacherItem> getAllTeacherItems();

    @Query("DELETE FROM GIAOVIEN WHERE MaGV = :maGV")
    void deleteByMaGV(String maGV);

}

