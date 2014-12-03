package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

public class FeedContentProvider extends ContentProvider {
    private static final String LOG_TAG = "myLogs";

    public static final String SIMPLE_FEED = "simple_feed";
    public static final String FEED_TABLE_NAME = "feeds";

    private static final int FEEDS = 1;
    private static final int FEEDS_ID = 2;
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Feed.AUTHORITY, Feed.SimpleFeed.FEED_NAME, FEEDS);
        uriMatcher.addURI(Feed.AUTHORITY, Feed.SimpleFeed.FEED_NAME + "/#", FEEDS_ID);
    }

    private static class SimpleFeedDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = SIMPLE_FEED + ".db";
        private static int DATABASE_VERSION = 2;

        private  SimpleFeedDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            createTable(sqLiteDatabase);
        }

        private void createTable(SQLiteDatabase sqLiteDatabase) {
            String qs = "CREATE TABLE " + FEED_TABLE_NAME + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + Feed.SimpleFeed.TITLE_NAME + " TEXT, "
                    + Feed.SimpleFeed.URL_NAME + " TEXT" + ");";
            sqLiteDatabase.execSQL(qs);
            insertFeeds(sqLiteDatabase);
        }

        private void insertFeeds(SQLiteDatabase sqLiteDatabase) {
            ContentValues cv = new ContentValues();
            cv.put(Feed.SimpleFeed.TITLE_NAME, "Bash");
            cv.put(Feed.SimpleFeed.URL_NAME, "http://bash.im/rss/");
            sqLiteDatabase.insert(FEED_TABLE_NAME, null, cv);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldv, int newv) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FEED_TABLE_NAME + ";");
            createTable(sqLiteDatabase);
        }
    }

    public FeedContentProvider() {}

    private SimpleFeedDbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "delete, " + uri.toString());

        int match = uriMatcher.match(uri);
        int affected;

        switch (match) {
            case FEEDS:
                affected = getDb().delete(FEED_TABLE_NAME,
                        (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case FEEDS_ID:
                long videoId = ContentUris.parseId(uri);
                affected = getDb().delete(FEED_TABLE_NAME,
                        BaseColumns._ID + "=" + videoId
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("unknown video element: " +
                        uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return affected;
    }

    @Override
    public String getType(Uri uri) {
        Log.d(LOG_TAG, "getType, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case FEEDS:
                return Feed.SimpleFeed.CONTENT_TYPE;
            case FEEDS_ID:
                return Feed.SimpleFeed.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown type: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        Log.d(LOG_TAG, "insert, " + uri.toString());
        if (uriMatcher.match(uri) != FEEDS) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = initialValues;
        } else {
            values = new ContentValues();
        }

        db = getDb();
        long rowID = db.insert(FEED_TABLE_NAME, null, values);
        if (rowID > 0) {
            Uri resultUri = ContentUris.withAppendedId(Feed.SimpleFeed.CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(resultUri, null);
            return resultUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        Log.d(LOG_TAG, "onCreate");
        dbHelper = new SimpleFeedDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG, "query, " + uri.toString());

        switch (uriMatcher.match(uri)) {
            case FEEDS:
                Log.d(LOG_TAG, "URI_CONTACTS");
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = Feed.SimpleFeed.TITLE_NAME + " ASC";
                }
                break;
            case FEEDS_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = BaseColumns._ID + " = " + id;
                } else {
                    selection = selection + " AND " + BaseColumns._ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(FEED_TABLE_NAME, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),
                Feed.SimpleFeed.CONTENT_URI);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int affected;

        switch (uriMatcher.match(uri)) {
            case FEEDS:
                affected = getDb().update(FEED_TABLE_NAME, values,
                        selection, selectionArgs);
                break;

            case FEEDS_ID:
                String feedId = uri.getPathSegments().get(1);
                affected = getDb().update(FEED_TABLE_NAME, values,
                        BaseColumns._ID + "=" + feedId
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return affected;
    }

    private SQLiteDatabase getDb() { return dbHelper.getWritableDatabase(); }
}
