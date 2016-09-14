package com.syncadaptersample.db;

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.syncadaptersample.db.model.SQLiteOperation;
import com.syncadaptersample.db.model.SQLiteTableProvider;
import com.syncadaptersample.db.model.TestProvider;
import com.syncadaptersample.utils.Constants;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dima on 9/6/16.
 */
public class SQLiteContentProvider extends ContentProvider {

    private static final String MIME_ITEM = "vnd.android.cursor.item/";
    private static final String MIME_DIR = "vnd.android.cursor.dir/";
    public static final Map<String, SQLiteTableProvider> SCHEMA = new ConcurrentHashMap<>();
    static {
        SCHEMA.put(TestProvider.TABLE_NAME, new TestProvider());
    }

    private final SQLiteUriMatcher mUriMatcher = new SQLiteUriMatcher();
    private SQLiteOpenHelperImpl mHelper;

    private static ProviderInfo getProviderInfo(Context context, Class<? extends ContentProvider> contentProvider, int flag) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getProviderInfo(new ComponentName(context.getPackageName(),
                contentProvider.getName()), flag);
    }
    private static String getTableName(Uri uri) {
        return uri.getPathSegments().get(0);
    }

    @Override
    public boolean onCreate() {
        try {
            ProviderInfo pi = getProviderInfo(getContext(), getClass(), 0);
            String[] authorities = TextUtils.split(pi.authority, ";");
            for(String authority : authorities){
                mUriMatcher.addAuthority(authority);
            }
            mHelper = new SQLiteOpenHelperImpl(getContext());
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            throw new SQLiteException(e.getMessage());
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] columns, String where, String[] whereArgs, String orderBy) {
        int matchResult = mUriMatcher.match(uri);
        if (matchResult == SQLiteUriMatcher.NO_MATCH) {
            throw new SQLiteException("Unknown uri " + uri);
        }
        String tableName = getTableName(uri);
        final SQLiteTableProvider tableProvider = SCHEMA.get(tableName);
        if (tableProvider == null) {
            throw new SQLiteException("No such table " + tableName);
        }
        if (matchResult == SQLiteUriMatcher.MATCH_ID) {
            where = BaseColumns._ID + "=?";
            whereArgs = new String[]{uri.getLastPathSegment()};
        }
        Cursor cursor = tableProvider.query(mHelper.getReadableDatabase(), columns, where, whereArgs, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int matchResult = mUriMatcher.match(uri);
        if (matchResult == SQLiteUriMatcher.NO_MATCH) {
            throw new SQLiteException("Unknown uri " + uri);
        } else if (matchResult == SQLiteUriMatcher.MATCH_ID) {
            return MIME_ITEM + getTableName(uri);
        }
        return MIME_DIR + getTableName(uri);
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int matchResult = mUriMatcher.match(uri);
        if (matchResult == SQLiteUriMatcher.NO_MATCH) {
            throw new SQLiteException("Unknown uri " + uri);
        }
        final String tableName = getTableName(uri);
        final SQLiteTableProvider tableProvider = SCHEMA.get(tableName);
        if (tableProvider == null) {
            throw new SQLiteException("No such table " + tableName);
        }
        if (matchResult == SQLiteUriMatcher.MATCH_ID) {
            final int affectedRows = updateInternal(
                    tableProvider.getBaseUri(), tableProvider,
                    values, BaseColumns._ID + "=?",
                    new String[]{uri.getLastPathSegment()}
            );
            if (affectedRows > 0) {
                return uri;
            }
        }
        final long lastId = tableProvider.insert(mHelper.getWritableDatabase(), values);
        getContext().getContentResolver().notifyChange(tableProvider.getBaseUri(), null);
        final Bundle extras = new Bundle();
        extras.putLong(Constants.KEY_LAST_ID, lastId);
        tableProvider.onContentChanged(getContext(), SQLiteOperation.INSERT, extras);
        return uri;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int matchResult = mUriMatcher.match(uri);
        if (matchResult == SQLiteUriMatcher.NO_MATCH) {
            throw new SQLiteException("Unknown uri " + uri);
        }
        final String tableName = getTableName(uri);
        final SQLiteTableProvider tableProvider = SCHEMA.get(tableName);
        if (tableProvider == null) {
            throw new SQLiteException("No such table " + tableName);
        }
        if (matchResult == SQLiteUriMatcher.MATCH_ID) {
            where = BaseColumns._ID + "=?";
            whereArgs = new String[]{uri.getLastPathSegment()};
        }
        final int affectedRows = tableProvider.delete(mHelper.getWritableDatabase(), where, whereArgs);
        return affectedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        final int matchResult = mUriMatcher.match(uri);
        if (matchResult == SQLiteUriMatcher.NO_MATCH) {
            throw new SQLiteException("Unknown uri " + uri);
        }
        final String tableName = getTableName(uri);
        final SQLiteTableProvider tableProvider = SCHEMA.get(tableName);
        if (tableProvider == null) {
            throw new SQLiteException("No such table " + tableName);
        }
        if (matchResult == SQLiteUriMatcher.MATCH_ID) {
            where = BaseColumns._ID + "=?";
            whereArgs = new String[]{uri.getLastPathSegment()};
        }
        return updateInternal(tableProvider.getBaseUri(), tableProvider, values, where, whereArgs);
    }

    private int updateInternal(Uri uri, SQLiteTableProvider provider,
                               ContentValues values, String where, String[] whereArgs) {
        final int affectedRows = provider.update(mHelper.getWritableDatabase(), values, where, whereArgs);
        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            final Bundle extras = new Bundle();
            extras.putLong(Constants.KEY_AFFECTED_ROWS, affectedRows);
            provider.onContentChanged(getContext(), SQLiteOperation.UPDATE, extras);
        }
        return affectedRows;
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        db.beginTransaction();
        ContentProviderResult[] result = null;
        try {
            result = super.applyBatch(operations);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return result;
    }
}
