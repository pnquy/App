package com.example.studentportalapp.data.Entity;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
@Entity(
        tableName = "BAIGIANG",
        foreignKeys = {
                @ForeignKey(
                        entity = LopHoc.class,
                        parentColumns = "MaLH",
                        childColumns = "MaLH",
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
public class BaiGiang {
    public String FileName;
    public String FilePath;
    @PrimaryKey
    @NonNull
    public String MaBG;
    public String TenBG;

    @NonNull
    public String MaLH;

    @NonNull
    public String MaGV;

    public String NoiDung;
}
