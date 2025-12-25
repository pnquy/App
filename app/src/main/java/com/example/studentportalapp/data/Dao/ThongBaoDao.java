package com.example.studentportalapp.data.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentportalapp.data.Entity.ThongBao;

import java.util.List;

@Dao
public interface ThongBaoDao {
    @Insert
    void insert(ThongBao thongBao);

    @Query("SELECT * FROM THONGBAO WHERE NguoiNhan = :maTK OR NguoiNhan = 'ALL' OR NguoiNhan = :roleMarker ORDER BY NgayTao DESC")
    LiveData<List<ThongBao>> getByNguoiNhan(String maTK, String roleMarker);

    @Query("SELECT COUNT(*) FROM THONGBAO WHERE (NguoiNhan = :maTK OR NguoiNhan = 'ALL' OR NguoiNhan = :roleMarker) AND IsRead = 0")
    LiveData<Integer> getUnreadCount(String maTK, String roleMarker);

    @Update
    void update(ThongBao thongBao);

    @Query("UPDATE THONGBAO SET IsRead = 1 WHERE NguoiNhan = :maTK OR NguoiNhan = :roleMarker")
    void markAllAsRead(String maTK, String roleMarker);
}
