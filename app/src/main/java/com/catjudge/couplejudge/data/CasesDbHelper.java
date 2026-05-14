package com.catjudge.couplejudge.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CasesDbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "cat_couple_judge.db";
    public static final int DB_VERSION = 2;
    public static final String TABLE_CASES = "cases";

    public CasesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CASES + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "judge_type TEXT,"
                + "mode TEXT,"
                + "event_description TEXT,"
                + "view_a TEXT,"
                + "view_b TEXT,"
                + "judgment_result TEXT,"
                + "status TEXT,"
                + "created_at TEXT,"
                + "category TEXT,"
                + "in_notebook INTEGER DEFAULT 0"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_CASES + " ADD COLUMN in_notebook INTEGER DEFAULT 0");
            db.execSQL("UPDATE " + TABLE_CASES + " SET in_notebook = 1 WHERE status = '已判决'");
        }
    }
}
