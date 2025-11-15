package com.example.studentportalapp.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HocVienDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HocVien hv);

    @Update
    void update(HocVien hv);

    @Delete
    void delete(HocVien hv);

    @Query("SELECT hv.MaHV AS MaHV, tk.HoTen AS HoTen, tk.Email AS Email " +
            "FROM HOCVIEN hv " +
            "JOIN TAIKHOAN tk ON hv.MaHV = tk.MaTK")
    List<HocVien> getAll();
}
