package com.example.studentportalapp.data.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "NOPBAI",
    foreignKeys = {
        @ForeignKey(entity = BaiTap.class, parentColumns = "MaBT", childColumns = "MaBT", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = HocVien.class, parentColumns = "MaHV", childColumns = "MaHV", onDelete = ForeignKey.CASCADE)
    }
)
public class NopBai {
    @PrimaryKey
    @NonNull
    public String MaNB;
    public String MaBT;
    public String MaHV;
    public String FileName;
    public String FilePath;
    public String GhiChu;
    public String NgayNop;
}
