package com.syncadaptersample.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.syncadaptersample.db.model.SQLiteTableProvider;


/**
 * Created by dima on 9/6/16.
 */
public class SQLiteOpenHelperImpl extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "flashcards.db";
    private static final int DATABASE_VERSION = 1;

    public SQLiteOpenHelperImpl(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransactionNonExclusive();
        try {
            for (SQLiteTableProvider provider : SQLiteContentProvider.SCHEMA.values()) {
                provider.onCreate(db);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransactionNonExclusive();
        try {
            for (SQLiteTableProvider provider : SQLiteContentProvider.SCHEMA.values()) {
                provider.onUpgrade(db, oldVersion, newVersion);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
