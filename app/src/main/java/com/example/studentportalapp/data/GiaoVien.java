package com.example.studentportalapp.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "GIAOVIEN")
public class GiaoVien {

    @PrimaryKey
    @NonNull
    public String MaGV;

    public String MaTK;
    public String MaLH;
}
