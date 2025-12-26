package com.example.studentportalapp.data.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.studentportalapp.data.Entity.TaiKhoan;

import java.util.List;

@Dao
public interface TaiKhoanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TaiKhoan tk);

    @Update
    void update(TaiKhoan tk);
    @Delete
    void delete(TaiKhoan tk);

    @Query("SELECT * FROM TAIKHOAN")
    LiveData<List<TaiKhoan>> getAll();
    @Query("SELECT * FROM TAIKHOAN")
    List<TaiKhoan> getAllSync();
    @Query("SELECT * FROM TAIKHOAN WHERE Email = :email LIMIT 1")
    TaiKhoan getByEmail(String email);
    @Query("SELECT * FROM TAIKHOAN WHERE MaTK = :username AND MatKhau = :password LIMIT 1")
    TaiKhoan login(String username, String password);

    @Query("SELECT COUNT(*) FROM TAIKHOAN WHERE VaiTro = :role")
    int countUsersByRole(String role);

    @Query("SELECT * FROM TAIKHOAN WHERE MaTK = :id LIMIT 1")
    TaiKhoan getById(String id);
    
    @Query("SELECT VaiTro FROM TAIKHOAN WHERE MaTK = :maTK")
    String getRoleById(String maTK);

}
