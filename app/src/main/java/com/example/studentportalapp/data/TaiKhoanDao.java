package com.example.studentportalapp.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface TaiKhoanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TaiKhoan tk);

    @Query("SELECT * FROM TAIKHOAN WHERE Email = :email LIMIT 1")
    TaiKhoan getByEmail(String email);

    @Query("SELECT * FROM TAIKHOAN WHERE MaTK = :maTK LIMIT 1")
    TaiKhoan getByMaTK(String maTK);

}
