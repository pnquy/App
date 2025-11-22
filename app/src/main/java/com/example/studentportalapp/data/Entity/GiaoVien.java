package com.example.studentportalapp.data.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "GIAOVIEN",
        foreignKeys = {
                @ForeignKey(
                        entity = TaiKhoan.class,
                        parentColumns = "MaTK",
                        childColumns = "MaTK",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = LopHoc.class,
                        parentColumns = "MaLH",
                        childColumns = "MaLH",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class GiaoVien {

    @PrimaryKey
    @NonNull
    private String MaGV;

    private String TenGV;
    private String Email;


    private String MaLH;

    @NonNull
    private String MaTK;

    // ================= GETTER / SETTER =================

    @NonNull
    public String getMaGV() {
        return MaGV;
    }

    public void setMaGV(@NonNull String maGV) {
        this.MaGV = maGV;
    }

    public String getTenGV() {
        return TenGV;
    }

    public void setTenGV(String tenGV) {
        this.TenGV = tenGV;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    @NonNull
    public String getMaLH() {
        return MaLH;
    }

    public void setMaLH(@NonNull String maLH) {
        this.MaLH = maLH;
    }

    @NonNull
    public String getMaTK() {
        return MaTK;
    }

    public void setMaTK(@NonNull String maTK) {
        this.MaTK = maTK;
    }
}
