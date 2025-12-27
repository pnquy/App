package com.example.studentportalapp;

import android.content.Context;
import android.net.Uri;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.studentportalapp.data.AppDatabase;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseImporter {

    public static void importFromSQL(Context context, AppDatabase db, Uri fileUri) throws Exception {
        SupportSQLiteDatabase sqlDb = db.getOpenHelper().getWritableDatabase();

        InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        sqlDb.beginTransaction();
        try {
            sqlDb.execSQL("DELETE FROM TAIKHOAN");
            sqlDb.execSQL("DELETE FROM HOCVIEN");
            sqlDb.execSQL("DELETE FROM GIAOVIEN");
            sqlDb.execSQL("DELETE FROM LOPHOC");
            sqlDb.execSQL("DELETE FROM BAIGIANG");
            sqlDb.execSQL("DELETE FROM BAITAP");
            sqlDb.execSQL("DELETE FROM DIEM");
            sqlDb.execSQL("DELETE FROM THAMGIA");

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--") || line.startsWith("//")) {
                    continue;
                }

                if (line.endsWith(";")) {
                    line = line.substring(0, line.length() - 1);
                }
                sqlDb.execSQL(line);
            }
            sqlDb.setTransactionSuccessful();
        } finally {
            sqlDb.endTransaction();
            reader.close();
            inputStream.close();
        }
    }
}