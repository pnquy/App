package com.example.studentportalapp.data.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "LOPHOC",
        foreignKeys = @ForeignKey(
                entity = GiaoVien.class,
                parentColumns = "MaGV",
                childColumns = "MaGV",
                onDelete = ForeignKey.CASCADE
        )
)
public class LopHoc {

    @PrimaryKey
    @NonNull
    public String MaLH;
    public String TenLH;
    public String MoTa;
    @NonNull
    public String MaGV;
}
