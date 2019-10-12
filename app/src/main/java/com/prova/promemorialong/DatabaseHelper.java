package com.prova.promemorialong;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyMo.db";
    public static final String TABLE_NAME = "task";
    public static final String TABLE_NAME2 = "basket";
    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String DATETIME = "DATETIME";
    public static final String PRIORITY = "PRIORITY";
    public static final String CLASS = "CLASS";
    public static final String NOTE = "NOTE";
    public static final String STATE = "STATE";
    public static final String START = "START";
    public static final String STARTDAY = "STARTDAY";
    public static final String FINISH = "FINISH";
    public static final String FINISHDAY = "FINISHDAY";
    public static final String SELECTION = "SELECTION";
    public static final int version = 18;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, version);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT NOT NULL,DATETIME LONG,PRIORITY INT,CLASS TEXT, NOTE STRING, STATE STRING,START LONG,STARTDAY STRING, FINISH LONG, FINISHDAY STRING)");
        db.execSQL("create table " + TABLE_NAME2 +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT,SELECTION TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        onCreate(db);
    }

    public boolean insertTask(String name, long dateTime, int priority, String classify, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        DateFormat dateFormat = new SimpleDateFormat("dd");
        Date date = new Date(System.currentTimeMillis());
        String newDate = dateFormat.format(date);
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(DATETIME, dateTime);
        contentValues.put(PRIORITY, priority);
        contentValues.put(CLASS, classify);
        contentValues.put(STATE, "pending");
        contentValues.put(NOTE, note);
        contentValues.put(START, (System.currentTimeMillis()/1000));
        contentValues.put(STARTDAY, newDate);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }


    public boolean updateTask(int id, String name, long dateTime, int priority, String classify, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(NAME, name);
        contentValues.put(DATETIME, dateTime);
        contentValues.put(PRIORITY, priority);
        contentValues.put(CLASS, classify);
        contentValues.put(NOTE, note);
        db.update(TABLE_NAME, contentValues, ID + "=" + id, null);
        return true;
    }

    public boolean updateStateOngoing(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATE, "ongoing");
        db.update(TABLE_NAME, contentValues, ID + "=" + id, null);
        return true;
    }

    public boolean updateStateComplete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        DateFormat dateFormat = new SimpleDateFormat("dd");
        Date date = new Date(System.currentTimeMillis());
        String newDate = dateFormat.format(date);
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATE, "complete");
        contentValues.put(FINISH, (System.currentTimeMillis()/1000));
        contentValues.put(FINISHDAY, newDate);
        db.update(TABLE_NAME, contentValues, ID + "=" + id, null);
        return true;
    }


    public Integer deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SELECTION, "");
        db.insert(TABLE_NAME2, null, contentValues);
        return db.delete(TABLE_NAME, ID + "=" + id, null);
    }


    public Cursor getTask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor2 = db.query(TABLE_NAME, new String[]{ID, NAME, DATETIME, PRIORITY, CLASS, NOTE}, "ID = ?", new String[]{String.valueOf(id)}, null, null, null);
        return cursor2;
    }

    public int getLastId() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{ID}, null, null, null, null, "ID DESC limit 1");
        cursor.moveToPosition(0);
        return cursor.getInt(cursor.getColumnIndex(ID));

    }


    public Cursor getAllTask(int choiceType, String choiceClass) {
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor cursor = db.query(TABLE_NAME, new String[]{ID,NAME,DATETIME,PRIORITY}, "date(datetime(DATETIME / 1000 , 'unixepoch', 'localtime')) >= date('now', 'localtime') OR DATETIME is null",null, null, null, "PRIORITY DESC");
        switch (choiceType) {
            case (0):
                //tutti i non completati,dal più vecchio
                Cursor cursor0 = db.query(TABLE_NAME, new String[]{ID, NAME, DATETIME, PRIORITY, CLASS}, "(STATE = ?) and CLASS LIKE ?", new String[]{"pending",choiceClass}, null, null, "DATETIME ASC");
                return cursor0;
            case (1):
                //tutti quelli in corso
                Cursor cursor1 = db.query(TABLE_NAME, new String[]{ID, NAME, DATETIME, PRIORITY, CLASS}, "(STATE = ?) and CLASS LIKE ?", new String[]{"ongoing",choiceClass}, null, null, "DATETIME ASC");
                return cursor1;
            case (2):
                //quelli con priorità più alta
                Cursor cursor2 = db.query(TABLE_NAME, new String[]{ID, NAME, DATETIME, PRIORITY, CLASS}, "(STATE = ? or STATE = ?) and CLASS LIKE ?", new String[]{"pending", "ongoing",choiceClass}, null, null, "PRIORITY DESC");
                return cursor2;
            case (3):
                //tutti i completati
                Cursor cursor3 = db.query(TABLE_NAME, new String[]{ID, NAME, DATETIME, PRIORITY, CLASS}, "STATE = ? and CLASS LIKE ?", new String[]{"complete",choiceClass}, null, null, "DATETIME DESC");
                return cursor3;
            default:
                //di default tutti i non completati dal più vecchio
                Cursor cursor = db.query(TABLE_NAME, new String[]{ID, NAME, DATETIME, PRIORITY, CLASS}, "(STATE = ?) and CLASS LIKE ?", new String[]{"pending",choiceClass}, null, null, "DATETIME ASC");
                return cursor;

        }

    }

    public Cursor getStatsPie() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*),STATE from task where (date(datetime(DATETIME / 1000 , 'unixepoch', 'localtime')) >= date('now','start of month')) and (date(datetime(DATETIME / 1000 , 'unixepoch', 'localtime')) < date('now','start of month','+1 month')) or DATETIME = 0 group by STATE",null);
        return cursor;
    }

    public Cursor getStatsHist() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*),CLASS from task where ((date(datetime(DATETIME / 1000 , 'unixepoch', 'localtime')) >= date('now','start of month')) and (date(datetime(DATETIME / 1000 , 'unixepoch', 'localtime')) < date('now','start of month','+1 month'))or DATETIME = 0)  group by CLASS",null);
        return cursor;
    }

    public Cursor getStatsLine() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*),FINISHDAY from task where ((date(datetime(FINISH, 'unixepoch', 'localtime')) >= date('now','start of month')) and (date(datetime(FINISH, 'unixepoch', 'localtime')) < date('now','start of month','+1 month'))) and STATE = ? group by FINISHDAY ", new String[]{"complete"});
        return cursor;
    }

    public long countCompleteTask() {
        SQLiteDatabase db = this.getWritableDatabase();
        return DatabaseUtils.queryNumEntries(db, "task","STATE = ?",new String[]{"complete"});
    }

    public long countDeletedTask() {
        SQLiteDatabase db = this.getWritableDatabase();
        return DatabaseUtils.queryNumEntries(db, "basket");
    }
}

