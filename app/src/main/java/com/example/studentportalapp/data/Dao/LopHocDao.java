package com.example.studentportalapp.data.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentportalapp.data.Entity.LopHoc;

import java.util.List;

@Dao
public interface LopHocDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LopHoc lh);

    @Update
    void update(LopHoc lh);

    @Delete
    void delete(LopHoc lh);

    @Query("SELECT * FROM LOPHOC")
    LiveData<List<LopHoc>> getAll();

    @Query("SELECT COUNT(*) FROM LOPHOC WHERE MaGV = :maGV")
    int countClassesByTeacher(String maGV);

    @Query("SELECT TenLH FROM LOPHOC WHERE MaGV = :maGV")
    List<String> getClassNamesByTeacher(String maGV);

    @Query("SELECT * FROM LOPHOC WHERE MaLH = :maLH LIMIT 1")
    LopHoc getByIdSync(String maLH); // Đổi tên thành getByIdSync để khớp với SubmitAssignmentActivity

    @Query("SELECT * FROM LOPHOC WHERE MaGV = :maGV")
    List<LopHoc> getClassesByTeacher(String maGV);
}
