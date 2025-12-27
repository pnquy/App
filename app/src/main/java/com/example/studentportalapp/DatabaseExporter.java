package com.example.studentportalapp;

import android.content.Context;
import android.database.Cursor;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.studentportalapp.data.AppDatabase;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseExporter {

    public static String exportToSQL(Context context, AppDatabase db) throws Exception {
        SupportSQLiteDatabase sqlDb = db.getOpenHelper().getReadableDatabase();

        StringBuilder sb = new StringBuilder();
        sb.append("-- BACKUP DATABASE: StudentPortalApp\n");
        sb.append("-- DATE: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())).append("\n");
        sb.append("PRAGMA foreign_keys = OFF;\n\n");

        Cursor c = sqlDb.query("SELECT name FROM sqlite_master WHERE type='table' AND name NOT IN ('android_metadata', 'room_master_table')");
        List<String> tables = new ArrayList<>();
        while (c.moveToNext()) {
            tables.add(c.getString(0));
        }
        c.close();

        for (String tableName : tables) {
            sb.append("-- DATA FOR TABLE: ").append(tableName).append("\n");

            Cursor cursor = sqlDb.query("SELECT * FROM " + tableName);

            while (cursor.moveToNext()) {
                sb.append("INSERT INTO ").append(tableName).append(" VALUES (");

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    if (i > 0) sb.append(", ");

                    int type = cursor.getType(i);
                    if (type == Cursor.FIELD_TYPE_NULL) {
                        sb.append("NULL");
                    } else if (type == Cursor.FIELD_TYPE_STRING) {
                        String val = cursor.getString(i);
                        val = val.replace("'", "''");
                        sb.append("'").append(val).append("'");
                    } else {
                        sb.append(cursor.getString(i));
                    }
                }
                sb.append(");\n");
            }
            cursor.close();
            sb.append("\n");
        }

        sb.append("PRAGMA foreign_keys = ON;\n");

        File folder = context.getExternalFilesDir(null);
        String fileName = "Backup_" + System.currentTimeMillis() + ".sql";
        File file = new File(folder, fileName);

        FileWriter writer = new FileWriter(file);
        writer.write(sb.toString());
        writer.flush();
        writer.close();

        return file.getAbsolutePath();
    }
}