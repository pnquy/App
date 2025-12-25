package com.example.studentportalapp.data.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.studentportalapp.data.Entity.NopBai;

import java.util.List;

@Dao
public interface NopBaiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NopBai nopBai);

    @Query("SELECT * FROM NOPBAI WHERE MaBT = :maBT")
    LiveData<List<NopBai>> getByBaiTap(String maBT);

    @Query("SELECT * FROM NOPBAI WHERE MaBT = :maBT AND MaHV = :maHV LIMIT 1")
    NopBai getSubmission(String maBT, String maHV);

    @Query("SELECT COUNT(*) FROM NOPBAI WHERE MaBT = :maBT")
    int countSubmissionsForAssignment(String maBT);
}
