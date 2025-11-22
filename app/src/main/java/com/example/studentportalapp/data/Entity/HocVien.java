package com.example.studentportalapp.data.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "HOCVIEN",
        foreignKeys = {
                @ForeignKey(
                        entity = LopHoc.class,
                        parentColumns = "MaLH",
                        childColumns = "MaLH",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = TaiKhoan.class,
                        parentColumns = "MaTK",
                        childColumns = "MaTK",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class HocVien {

    @PrimaryKey
    @NonNull
    private String MaHV;

    private String TenHV;
    private String Email;

    @NonNull
    private String MaLH;

    @NonNull
    private String MaTK;


    @NonNull
    public String getMaHV() { return MaHV; }

    public String getTenHV() { return TenHV; }

    public String getEmail() { return Email; }

    @NonNull
    public String getMaLH() { return MaLH; }

    @NonNull
    public String getMaTK() { return MaTK; }

    public void setMaHV(@NonNull String maHV) { this.MaHV = maHV; }

    public void setTenHV(String tenHV) { this.TenHV = tenHV; }

    public void setEmail(String email) { this.Email = email; }

    public void setMaLH(@NonNull String maLH) { this.MaLH = maLH; }

    public void setMaTK(@NonNull String maTK) { this.MaTK = maTK; }
}
