package com.example.studentportalapp.data.Entity;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
@Entity(
        tableName = "DIEM",
        primaryKeys = {"MaHV", "MaBT"},
        foreignKeys = {
                @ForeignKey(
                        entity = HocVien.class,
                        parentColumns = "MaHV",
                        childColumns = "MaHV",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = BaiTap.class,
                        parentColumns = "MaBT",
                        childColumns = "MaBT",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = GiaoVien.class,
                        parentColumns = "MaGV",
                        childColumns = "MaGV",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class Diem {

    @NonNull
    public String MaHV;

    @NonNull
    public String MaBT;

    @NonNull
    public String MaGV;

    public double SoDiem;
}

