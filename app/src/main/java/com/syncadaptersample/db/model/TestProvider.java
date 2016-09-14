package com.syncadaptersample.db.model;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.syncadaptersample.utils.Constants;

/**
 * Created by dima on 9/14/16.
 */
public class TestProvider extends SQLiteTableProvider {

    public static final String TABLE_NAME = "Test";

    public static final Uri URI = Uri.parse(Constants.SQL_AUTHORITY + '/' + TABLE_NAME);

    public TestProvider() {
        super(TABLE_NAME);
    }

    @Override
    public Uri getBaseUri() {
        return URI;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_NAME
                + "("+ Columns._ID +" integer  PRIMARY KEY, "
                + Columns.NAME + " text" +
                ")");
    }

    public interface Columns extends BaseColumns {
        String NAME = "name";
    }
}
