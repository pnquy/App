package com.example.studentportalapp.data.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.studentportalapp.data.Entity.BinhLuan;

import java.util.List;

@Dao
public interface BinhLuanDao {
    @Insert
    void insert(BinhLuan binhLuan);

    @Query("SELECT * FROM BINHLUAN WHERE TargetId = :targetId AND TargetType = :targetType ORDER BY NgayTao ASC")
    LiveData<List<BinhLuan>> getComments(String targetId, String targetType);
}
