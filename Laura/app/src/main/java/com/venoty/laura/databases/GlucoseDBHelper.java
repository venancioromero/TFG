package com.venoty.laura.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.venoty.laura.models.Glucose;

import java.util.ArrayList;
import java.util.List;

public class GlucoseDBHelper extends SQLiteOpenHelper {

    private final String TAG = "Laura || GlucoseDBHelper.class";
    private static final String DATABASE_NAME = "laura.db";
    private final String TABLE_CURRENT_MEAS   = "measures";
    private final String TABLE_HISTORY_MEAS   = "history";
    private final String TIMESTAMP            = "timestamp";
    private final String GLUCOSE              = "glucose";

    public GlucoseDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_CURRENT_MEAS +
                                "(" + TIMESTAMP + " integer," +
                                      GLUCOSE   + " integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_HISTORY_MEAS +
                                "(" + TIMESTAMP + " integer,"
                                    + GLUCOSE   + " integer)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENT_MEAS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY_MEAS);
        onCreate(sqLiteDatabase);
    }
    private Glucose getLastHistoryRecord(){

        String query = "SELECT * FROM "+ TABLE_HISTORY_MEAS + " ORDER BY timestamp DESC LIMIT 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor     = db.rawQuery(query, null);

        if(cursor.getCount() == 0)
            return null;

        cursor.moveToFirst();

        Glucose g = new Glucose();
        g.setTimestamp(cursor.getLong(0));
        g.setMeasure(cursor.getInt(1));

        db.close();

        return g;
    }

    public boolean insertMeasure(Glucose g){
        SQLiteDatabase db           = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIMESTAMP,g.getTimestamp());
        contentValues.put(GLUCOSE, g.getCalculateGlucose());
        db.insert(TABLE_CURRENT_MEAS, null, contentValues);
        db.close();
        return true;
    }

    public boolean insertHistory(Glucose[] gluArr){

        Glucose lastRecord = getLastHistoryRecord();

        SQLiteDatabase db           = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        for(Glucose g : gluArr){
            if (lastRecord == null || g.getTimestamp() > lastRecord.getTimestamp()) {
                contentValues.put(TIMESTAMP, g.getTimestamp());
                contentValues.put(GLUCOSE, g.getCalculateGlucose());
                db.insert(TABLE_HISTORY_MEAS, null, contentValues);
            }
        }

        db.close();
        return true;

    }
    private List<Glucose> getData(String table, int daysAgo, String orderBy){
        String query = "SELECT * FROM " + table ;
        if (daysAgo > 0){
            long ts = System.currentTimeMillis() - (daysAgo*86400000l);
            query += " WHERE timestamp > " + ts;
        }
        query += " ORDER BY timestamp " + orderBy;

        List<Glucose> glucoseList = new ArrayList<Glucose>();
        SQLiteDatabase db         = this.getReadableDatabase();
        Cursor cursor             = db.rawQuery(query, null);
        cursor.moveToLast();

        while(!cursor.isBeforeFirst()){
            Glucose g = new Glucose();
            g.setTimestamp(cursor.getLong(0));
            g.setMeasure(cursor.getInt(1));
            glucoseList.add(g);
            cursor.moveToPrevious();
        }

        db.close();
        return glucoseList;
    }

    public List<Glucose> getAllMeasures(){
        return getData(TABLE_CURRENT_MEAS,0,"ASC");
    }

    public List<Glucose> getHistory(int days){
        return getData(TABLE_HISTORY_MEAS,days,"DESC");
    }

    public List<Glucose> getIntervalHistory(long from, long to){
        String query = "SELECT * FROM " + TABLE_HISTORY_MEAS  +
                       " WHERE timestamp BETWEEN " + from + " AND " + to + " ORDER BY timestamp DESC ";

        List<Glucose> glucoseList = new ArrayList<Glucose>();
        SQLiteDatabase db         = this.getReadableDatabase();
        Cursor cursor             = db.rawQuery(query, null);
        cursor.moveToLast();

        while(!cursor.isBeforeFirst()){
            Glucose g = new Glucose();
            g.setTimestamp(cursor.getLong(0));
            g.setMeasure(cursor.getInt(1));
            glucoseList.add(g);
            cursor.moveToPrevious();
        }

        db.close();
        return glucoseList;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_CURRENT_MEAS,null,null);
        db.delete(TABLE_HISTORY_MEAS,null,null);
        db.close();
    }
}
