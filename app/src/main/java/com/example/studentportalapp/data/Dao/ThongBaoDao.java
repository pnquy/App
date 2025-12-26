package com.example.studentportalapp.data.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentportalapp.data.Entity.ThongBao;

import java.util.List;

@Dao
public interface ThongBaoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ThongBao thongBao);

    @Update
    void update(ThongBao thongBao);

    @Query("SELECT * FROM THONGBAO WHERE NguoiNhan = :maTK OR NguoiNhan = :role ORDER BY NgayTao DESC")
    LiveData<List<ThongBao>> getByNguoiNhan(String maTK, String role);

    @Query("UPDATE THONGBAO SET IsRead = 1 WHERE NguoiNhan = :maTK OR NguoiNhan = :role")
    void markAllAsRead(String maTK, String role);
}
