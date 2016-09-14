package com.syncadaptersample.db.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by dima on 9/6/16.
 */
public abstract class SQLiteTableProvider {

    protected final String mName;

    public SQLiteTableProvider(String name) {
        mName = name;
    }

    public abstract Uri getBaseUri();

    public Cursor query(SQLiteDatabase db, String[] columns, String where, String[] whereArgs, String orderBy) {
        return db.query(mName, columns, where, whereArgs, null, null, orderBy);
    }

    public long insert(SQLiteDatabase db, ContentValues values) {
        return db.insertWithOnConflict(mName, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public int delete(SQLiteDatabase db, String where, String[] whereArgs) {
        return db.delete(mName, where, whereArgs);
    }

    public int update(SQLiteDatabase db, ContentValues values, String where, String[] whereArgs) {
        return db.update(mName, values, where, whereArgs);
    }

    public Cursor rawQuery(SQLiteDatabase db, String rawQuery){
        return db.rawQuery(rawQuery, new String[0]);
    }

    public void onContentChanged(Context context, int operation, Bundle extras) {

    }

    public abstract void onCreate(SQLiteDatabase db);

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + mName + ";");
        onCreate(db);
    }
}
