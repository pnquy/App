package com.example.studentportalapp.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "GIAOVIEN")
public class GiaoVien {

    @PrimaryKey
    @NonNull
    public String MaGV; // vd: sử dụng luôn MaTK cho đồng bộ

    public String MaTK; // foreign key tới TAIKHOAN.MaTK
    public String MaLH; // mã lớp dạy, tạm thời có thể để trống
}
