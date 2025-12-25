package com.example.studentportalapp.data.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentportalapp.data.Entity.BaiTap;

import java.util.List;

@Dao
public interface BaiTapDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BaiTap bt);

    @Update
    void update(BaiTap bt);

    @Delete
    void delete(BaiTap bt);

    @Query("SELECT * FROM BAITAP WHERE MaLH = :maLH")
    LiveData<List<BaiTap>> getByLop(String maLH);

    @Query("SELECT * FROM BAITAP WHERE MaLH = :maLH")
    List<BaiTap> getByLopSync(String maLH);

    @Query("SELECT * FROM BAITAP")
    LiveData<List<BaiTap>> getAll();

    @Query("SELECT * FROM BAITAP WHERE MaBT = :maBT")
    LiveData<BaiTap> getById(String maBT);

    // Thêm phương thức này để lấy dữ liệu đồng bộ
    @Query("SELECT * FROM BAITAP WHERE MaBT = :maBT LIMIT 1")
    BaiTap getByIdSync(String maBT);
}
