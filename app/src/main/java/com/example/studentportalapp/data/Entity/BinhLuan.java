package com.example.studentportalapp.data.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "BINHLUAN")
public class BinhLuan {
    @PrimaryKey
    @NonNull
    public String MaBL;

    public String NoiDung;
    public String NgayTao;
    
    public String MaNguoiGui;
    public String TenNguoiGui;
    
    public String TargetType;
    public String TargetId;
}
