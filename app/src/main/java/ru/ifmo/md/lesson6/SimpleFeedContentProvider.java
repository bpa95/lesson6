package ru.ifmo.md.lesson6;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class SimpleFeedContentProvider extends ContentProvider {

    public static final String FEED_TABLE_NAME = "feed";

    public static final String DATABASE_NAME = "rssreader.db";
    public static int DATABASE_VERSION = 1;

    public static final int ID_COLUMN = 0;
    public static final int TITLE_COLUMN = 1;
    public static final int DESCRIPTION_COLUMN = 2;
    public static final int TIMESTAMP_COLUMN = 3;
    public static final int QUERY_TEXT_COLUMN = 4;
    public static final int MEDIA_ID_COLUMN = 5;

    private static class SimpleFeedDbHelper extends SQLiteOpenHelper {
        private  SimpleFeedDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            createTable(sqLiteDatabase);
        }

        private void createTable(SQLiteDatabase sqLiteDatabase) {
            String qs = "CREATE TABLE " + FEED_TABLE_NAME + " (";
            sqLiteDatabase.execSQL(qs);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldv, int newv) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FEED_TABLE_NAME + ";");
            createTable(sqLiteDatabase);
        }
    }

    public SimpleFeedContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
