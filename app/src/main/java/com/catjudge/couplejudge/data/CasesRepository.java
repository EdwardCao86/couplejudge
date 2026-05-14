package com.catjudge.couplejudge.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.catjudge.couplejudge.model.CaseRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CasesRepository {
    private final CasesDbHelper dbHelper;

    public CasesRepository(Context context) {
        dbHelper = new CasesDbHelper(context);
    }

    public long createDraft(String judgeType, String mode, String eventDescription, String category, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("judge_type", judgeType);
        values.put("mode", mode);
        values.put("event_description", eventDescription);
        values.put("view_a", "");
        values.put("view_b", "");
        values.put("judgment_result", "");
        values.put("status", status);
        values.put("created_at", now());
        values.put("category", category);
        values.put("in_notebook", 0);
        return db.insert(CasesDbHelper.TABLE_CASES, null, values);
    }

    public void updateViews(long id, String viewA, String viewB) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("view_a", viewA);
        values.put("view_b", viewB);
        db.update(CasesDbHelper.TABLE_CASES, values, "id=?", new String[]{String.valueOf(id)});
    }

    public void updateStatus(long id, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        db.update(CasesDbHelper.TABLE_CASES, values, "id=?", new String[]{String.valueOf(id)});
    }

    public void updateJudgment(long id, String result, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("judgment_result", result);
        values.put("status", status);
        values.put("in_notebook", "已判决".equals(status) ? 1 : 0);
        db.update(CasesDbHelper.TABLE_CASES, values, "id=?", new String[]{String.valueOf(id)});
    }

    public void removeFromNotebook(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("in_notebook", 0);
        db.update(CasesDbHelper.TABLE_CASES, values, "id=?", new String[]{String.valueOf(id)});
    }

    public void deleteCase(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(CasesDbHelper.TABLE_CASES, "id=?", new String[]{String.valueOf(id)});
    }

    public CaseRecord getCaseById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(CasesDbHelper.TABLE_CASES, null, "id=?",
                new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return fromCursor(cursor);
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    public List<CaseRecord> getCases(String status) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        if (status == null) {
            cursor = db.query(CasesDbHelper.TABLE_CASES, null, null, null, null, null, "id DESC");
        } else {
            cursor = db.query(CasesDbHelper.TABLE_CASES, null, "status=?",
                    new String[]{status}, null, null, "id DESC");
        }
        List<CaseRecord> records = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                records.add(fromCursor(cursor));
            }
        } finally {
            cursor.close();
        }
        return records;
    }

    public List<CaseRecord> getArchivedCases() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(CasesDbHelper.TABLE_CASES, null, "in_notebook=?",
                new String[]{"1"}, null, null, "id DESC");
        List<CaseRecord> records = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                records.add(fromCursor(cursor));
            }
        } finally {
            cursor.close();
        }
        return records;
    }

    public CaseRecord getLatestOngoingCase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(CasesDbHelper.TABLE_CASES, null,
                "status!=?", new String[]{"已判决"},
                null, null, "id DESC", "1");
        try {
            if (cursor.moveToFirst()) {
                return fromCursor(cursor);
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    public Map<String, Integer> getStatusCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("全部", getArchivedCases().size());
        counts.put("已判决", getArchivedCases().size());
        counts.put("进行中", 0);
        return counts;
    }

    private CaseRecord fromCursor(Cursor cursor) {
        CaseRecord record = new CaseRecord();
        record.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
        record.setJudgeType(cursor.getString(cursor.getColumnIndexOrThrow("judge_type")));
        record.setMode(cursor.getString(cursor.getColumnIndexOrThrow("mode")));
        record.setEventDescription(cursor.getString(cursor.getColumnIndexOrThrow("event_description")));
        record.setViewA(cursor.getString(cursor.getColumnIndexOrThrow("view_a")));
        record.setViewB(cursor.getString(cursor.getColumnIndexOrThrow("view_b")));
        record.setJudgmentResult(cursor.getString(cursor.getColumnIndexOrThrow("judgment_result")));
        record.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
        record.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
        record.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
        record.setInNotebook(cursor.getInt(cursor.getColumnIndexOrThrow("in_notebook")) == 1);
        return record;
    }

    private String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
    }
}
