package com.example.studentportalapp.data.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        tableName = "THAMGIA",
        primaryKeys = {"MaHV", "MaLH"},
        foreignKeys = {
                @ForeignKey(entity = HocVien.class, parentColumns = "MaHV", childColumns = "MaHV", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = LopHoc.class, parentColumns = "MaLH", childColumns = "MaLH", onDelete = ForeignKey.CASCADE)
        }
)
public class ThamGia {
    @NonNull
    public String MaHV;

    @NonNull
    public String MaLH;

    public ThamGia(@NonNull String MaHV, @NonNull String MaLH) {
        this.MaHV = MaHV;
        this.MaLH = MaLH;
    }
}