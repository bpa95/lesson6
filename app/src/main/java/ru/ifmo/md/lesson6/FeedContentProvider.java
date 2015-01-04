package ru.ifmo.md.lesson6;

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
import android.text.TextUtils;

public class FeedContentProvider extends ContentProvider {
    //private static final String LOG_TAG = "myLogs";

    public static final String SIMPLE_FEED = "simple_feed";
    public static final String FEEDS_TABLE_NAME = "feeds";
    public static final String POSTS_TABLE_NAME = "posts";

    private static final int FEEDS = 1;
    private static final int FEEDS_ID = 2;
    private static final int POSTS = 3;
    private static final int POSTS_ID = 4;
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Feed.AUTHORITY, Feed.SimpleFeed.FEED_NAME, FEEDS);
        uriMatcher.addURI(Feed.AUTHORITY, Feed.SimpleFeed.FEED_NAME + "/#", FEEDS_ID);
        uriMatcher.addURI(Feed.AUTHORITY, Feed.SimplePost.POST_NAME, POSTS);
        uriMatcher.addURI(Feed.AUTHORITY, Feed.SimplePost.POST_NAME + "/#", POSTS_ID);
    }

    private static class FeedsDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = SIMPLE_FEED + ".db";
        private static int DATABASE_VERSION = 3;

        private FeedsDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            createTables(sqLiteDatabase);
        }

        private void createTables(SQLiteDatabase sqLiteDatabase) {
            String qs = "CREATE TABLE " + FEEDS_TABLE_NAME + " ("
                    + Feed.SimpleFeed._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + Feed.SimpleFeed.TITLE_NAME + " TEXT, "
                    + Feed.SimpleFeed.URL_NAME + " TEXT" + ");";
            sqLiteDatabase.execSQL(qs);
            insertFeeds(sqLiteDatabase);
            qs = "CREATE TABLE " + POSTS_TABLE_NAME + " ("
                    + Feed.SimplePost._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + Feed.SimplePost.FEED_NAME + " TEXT, "
                    + Feed.SimplePost.TITLE_NAME + " TEXT, "
                    + Feed.SimplePost.DESCRIPTION_NAME + " TEXT, "
                    + Feed.SimplePost.URL_NAME + " TEXT" + ");";
            sqLiteDatabase.execSQL(qs);
        }

        private void insertFeeds(SQLiteDatabase sqLiteDatabase) {
            ContentValues cv = new ContentValues();
            cv.put(Feed.SimpleFeed.TITLE_NAME, "BBC");
            cv.put(Feed.SimpleFeed.URL_NAME, "http://feeds.bbci.co.uk/news/rss.xml");
            sqLiteDatabase.insert(FEEDS_TABLE_NAME, null, cv);
            cv.clear();
            cv.put(Feed.SimpleFeed.TITLE_NAME, "Echo MSK");
            cv.put(Feed.SimpleFeed.URL_NAME, "http://echo.msk.ru/interview/rss-fulltext.xml");
            sqLiteDatabase.insert(FEEDS_TABLE_NAME, null, cv);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldv, int newv) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FEEDS_TABLE_NAME + ";");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + POSTS_TABLE_NAME + ";");
            createTables(sqLiteDatabase);
        }
    }

    public FeedContentProvider() {}

    private FeedsDbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //Log.d(LOG_TAG, "delete, " + uri.toString());

        int match = uriMatcher.match(uri);
        int affected;

        switch (match) {
            case FEEDS:
                affected = getDb().delete(FEEDS_TABLE_NAME,
                        (!TextUtils.isEmpty(selection) ?
                                " (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case FEEDS_ID:
                long feedId = ContentUris.parseId(uri);
                affected = getDb().delete(FEEDS_TABLE_NAME,
                        Feed.SimpleFeed._ID + "=" + feedId
                                + (!TextUtils.isEmpty(selection) ?
                                " (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case POSTS:
                affected = getDb().delete(POSTS_TABLE_NAME,
                        (!TextUtils.isEmpty(selection) ?
                                " (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case POSTS_ID:
                long postId = ContentUris.parseId(uri);
                affected = getDb().delete(POSTS_TABLE_NAME,
                        Feed.SimplePost._ID + "=" + postId
                                + (!TextUtils.isEmpty(selection) ?
                                " (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("unknown feed element: " +
                        uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return affected;
    }

    @Override
    public String getType(Uri uri) {
        //Log.d(LOG_TAG, "getType, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case FEEDS:
                return Feed.SimpleFeed.CONTENT_TYPE;
            case FEEDS_ID:
                return Feed.SimpleFeed.CONTENT_ITEM_TYPE;
            case POSTS:
                return Feed.SimplePost.CONTENT_TYPE;
            case POSTS_ID:
                return Feed.SimplePost.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown type: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        //Log.d(LOG_TAG, "insert, " + uri.toString());
        int u = uriMatcher.match(uri);
        if (u != FEEDS && u != POSTS) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = initialValues;
        } else {
            values = new ContentValues();
        }

        db = getDb();

        if (u == FEEDS) {
            long rowID = db.insert(FEEDS_TABLE_NAME, null, values);
            if (rowID > 0) {
                Uri resultUri = ContentUris.withAppendedId(Feed.SimpleFeed.CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(resultUri, null);
                return resultUri;
            }
        } else if (u == POSTS) {
            long rowID = db.insert(POSTS_TABLE_NAME, null, values);
            if (rowID > 0) {
                Uri resultUri = ContentUris.withAppendedId(Feed.SimplePost.CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(resultUri, null);
                return resultUri;
            }
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        //Log.d(LOG_TAG, "onCreate");
        dbHelper = new FeedsDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        //Log.d(LOG_TAG, "query, " + uri.toString());

        String TABLE_NAME;
        Uri CONTENT_URI;
        switch (uriMatcher.match(uri)) {
            case FEEDS:
                //Log.d(LOG_TAG, "URI_FEEDS");
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = Feed.SimpleFeed.TITLE_NAME + " ASC";
                }
                TABLE_NAME = FEEDS_TABLE_NAME;
                CONTENT_URI = Feed.SimpleFeed.CONTENT_URI;
                break;
            case FEEDS_ID: {
                    String id = uri.getLastPathSegment();
                    //Log.d(LOG_TAG, "URI_FEEDS_ID, " + id);
                    if (TextUtils.isEmpty(selection)) {
                        selection = Feed.SimpleFeed._ID + " = " + id;
                    } else {
                        selection = selection + " AND " + Feed.SimpleFeed._ID + " = " + id;
                    }
                    TABLE_NAME = FEEDS_TABLE_NAME;
                    CONTENT_URI = Feed.SimpleFeed.CONTENT_URI;
                }
                break;
            case POSTS:
                //Log.d(LOG_TAG, "URI_POSTS");
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = Feed.SimplePost.TITLE_NAME + " ASC";
                }
                TABLE_NAME = POSTS_TABLE_NAME;
                CONTENT_URI = Feed.SimplePost.CONTENT_URI;
                break;
            case POSTS_ID: {
                    String id = uri.getLastPathSegment();
                    //Log.d(LOG_TAG, "URI_POSTS_ID, " + id);
                    if (TextUtils.isEmpty(selection)) {
                        selection = Feed.SimplePost._ID + " = " + id;
                    } else {
                        selection = selection + " AND " + Feed.SimplePost._ID + " = " + id;
                    }
                    TABLE_NAME = POSTS_TABLE_NAME;
                    CONTENT_URI = Feed.SimplePost.CONTENT_URI;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = getDb();
        Cursor cursor = db.query(TABLE_NAME, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int affected;

        switch (uriMatcher.match(uri)) {
            case FEEDS:
                affected = getDb().update(FEEDS_TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case FEEDS_ID:
                String feedId = uri.getPathSegments().get(1);
                affected = getDb().update(FEEDS_TABLE_NAME, values,
                        Feed.SimpleFeed._ID + "=" + feedId
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case POSTS:
                affected = getDb().update(POSTS_TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case POSTS_ID:
                String postId = uri.getPathSegments().get(1);
                affected = getDb().update(POSTS_TABLE_NAME, values,
                        Feed.SimplePost._ID + "=" + postId
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
