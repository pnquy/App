package com.example.studentportalapp.data.Entity;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
@Entity(
        tableName = "BAITAP",
        foreignKeys = {
                @ForeignKey(
                        entity = LopHoc.class,
                        parentColumns = "MaLH",
                        childColumns = "MaLH",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class BaiTap {

    @PrimaryKey
    @NonNull
    public String MaBT;

    public String TenBT;
    public String MoTa;
    public String Deadline;

    @NonNull
    public String MaLH;
}
