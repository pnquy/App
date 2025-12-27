package com.example.studentportalapp.data.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TAIKHOAN")
public class TaiKhoan {

    @PrimaryKey
    @NonNull
    public String MaTK;

    public String HoTen;
    public String Email;
    public String MatKhau;
    public String VaiTro;
    public String getMaTK() { return MaTK; }
    public String getHoTen() { return HoTen; }
    public String getEmail() { return Email; }
    public String getMatKhau() { return MatKhau; }
    public String getVaiTro() { return VaiTro; }
    public void setMaTK(String maTK) { MaTK = maTK; }
    public void setHoTen(String hoTen) { HoTen = hoTen; }
    public void setEmail(String email) { Email = email; }
    public void setMatKhau(String matKhau) { MatKhau = matKhau; }
    public void setVaiTro(String vaiTro) { VaiTro = vaiTro; }
}
