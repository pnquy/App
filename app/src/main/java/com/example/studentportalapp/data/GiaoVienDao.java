package com.example.studentportalapp.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GiaoVienDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GiaoVien gv);

    @Query("SELECT * FROM GIAOVIEN")
    List<GiaoVien> getAll();

    @Query("DELETE FROM GIAOVIEN WHERE MaGV = :maGV")
    void deleteByMaGV(String maGV);

    // lấy list để show lên RecyclerView
    @Query("SELECT gv.MaGV AS MaGV, tk.HoTen AS HoTen, tk.Email AS Email " +
            "FROM GIAOVIEN gv " +
            "JOIN TAIKHOAN tk ON gv.MaTK = tk.MaTK")
    List<TeacherItem> getAllTeacherItems();

}
