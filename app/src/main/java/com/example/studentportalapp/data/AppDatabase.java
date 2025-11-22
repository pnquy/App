package com.example.studentportalapp.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.studentportalapp.data.Dao.GiaoVienDao;
import com.example.studentportalapp.data.Dao.BaiGiangDao;
import com.example.studentportalapp.data.Dao.BaiTapDao;
import com.example.studentportalapp.data.Dao.DiemDao;
import com.example.studentportalapp.data.Dao.HocVienDao;
import com.example.studentportalapp.data.Dao.LopHocDao;
import com.example.studentportalapp.data.Dao.TaiKhoanDao;
import com.example.studentportalapp.data.Entity.BaiGiang;
import com.example.studentportalapp.data.Entity.BaiTap;
import com.example.studentportalapp.data.Entity.Diem;
import com.example.studentportalapp.data.Entity.GiaoVien;
import com.example.studentportalapp.data.Entity.HocVien;
import com.example.studentportalapp.data.Entity.LopHoc;
import com.example.studentportalapp.data.Entity.TaiKhoan;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                TaiKhoan.class,
                GiaoVien.class,
                HocVien.class,
                LopHoc.class,
                BaiGiang.class,
                BaiTap.class,
                Diem.class
        },
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // ===========================
    //       SINGLETON INSTANCE
    // ===========================
    private static volatile AppDatabase INSTANCE;

    // Thread pool (background insert/update/delete)
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);


    // ===========================
    //            DAO
    // ===========================
    public abstract TaiKhoanDao taiKhoanDao();
    public abstract GiaoVienDao giaoVienDao();
    public abstract HocVienDao hocVienDao();
    public abstract LopHocDao lopHocDao();
    public abstract BaiGiangDao baiGiangDao();
    public abstract BaiTapDao baiTapDao();
    public abstract DiemDao diemDao();


    // ===========================
    //      GET DATABASE INSTANCE
    // ===========================
    public static AppDatabase getDatabase(final Context context) {

        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {

                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "school_db"
                            )
                            // Tự xóa DB khi thay đổi version (tránh crash)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
