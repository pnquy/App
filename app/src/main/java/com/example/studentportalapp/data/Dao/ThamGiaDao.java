package com.example.studentportalapp.data.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.studentportalapp.data.Entity.LopHoc;
import com.example.studentportalapp.data.Entity.ThamGia;

import java.util.List;

@Dao
public interface ThamGiaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ThamGia thamGia);

    @Delete
    void delete(ThamGia thamGia);

    // Lấy danh sách MaHV thuộc 1 lớp
    @Query("SELECT MaHV FROM THAMGIA WHERE MaLH = :maLH")
    List<String> getStudentIdsByClass(String maLH);

    // Đếm sĩ số (Cập nhật cho logic cũ)
    @Query("SELECT COUNT(*) FROM THAMGIA WHERE MaLH = :maLH")
    int countStudentsByClass(String maLH);
    // Trong ThamGiaDao.java
    @Query("SELECT MaLH FROM THAMGIA WHERE MaHV = :maHV")
    List<String> getClassIdsByStudent(String maHV);
    // Xóa học viên khỏi lớp cụ thể
    @Query("DELETE FROM THAMGIA WHERE MaHV = :maHV AND MaLH = :maLH")
    void removeStudentFromClass(String maHV, String maLH);
    @Query("SELECT LOPHOC.* FROM LOPHOC INNER JOIN THAMGIA ON LOPHOC.MaLH = THAMGIA.MaLH WHERE THAMGIA.MaHV = :maHV")
    List<LopHoc> getClassesByStudent(String maHV);
}