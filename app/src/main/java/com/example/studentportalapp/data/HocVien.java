package com.example.studentportalapp.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "HOCVIEN")
public class HocVien {
    @PrimaryKey
    @NonNull
    private String MaHV;

    private String HoTen;
    private String Email;
    private String MaLop;

    public String getMaHV() { return MaHV; }
    public void setMaHV(String maHV) { MaHV = maHV; }

    public String getHoTen() { return HoTen; }
    public void setHoTen(String hoTen) { HoTen = hoTen; }

    public String getEmail() { return Email; }
    public void setEmail(String email) { Email = email; }

    public String getMaLop() { return MaLop; }
    public void setMaLop(String maLop) { MaLop = maLop; }
}
