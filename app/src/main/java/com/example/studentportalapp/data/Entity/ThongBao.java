package com.example.studentportalapp.data.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "THONGBAO")
public class ThongBao {
    @PrimaryKey
    @NonNull
    public String MaTB;
    public String NoiDung;
    public String NgayTao;
    public String NguoiNhan; // MaTK của người nhận, hoặc "ALL"
    public boolean IsRead;

    public ThongBao() {
        this.MaTB = "TB" + System.currentTimeMillis();
        this.IsRead = false;
    }
}
