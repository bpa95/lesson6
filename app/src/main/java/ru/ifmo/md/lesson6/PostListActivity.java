package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class PostListActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    private SimpleCursorAdapter adapter;
    private ListView listView;
    private String FEED_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        FEED_NAME = getIntent().getStringExtra("feed_name");

        String from[] = { Feed.SimplePost.TITLE_NAME };
        int to[] = { android.R.id.text1 };
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, null, from, to);

        listView = (ListView) findViewById(R.id.post_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(contentShower);

        getLoaderManager().initLoader(0, null, this);
    }

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(),
                text,
                Toast.LENGTH_SHORT)
                .show();
    }

    private void loadPosts() {
        Intent intent = new Intent(this, RSSPullService.class);
        intent.putExtra("link", getIntent().getStringExtra("link"));
        intent.putExtra("feed_name", FEED_NAME);
        startService(intent);
    }

    private void refreshPosts() {
        getContentResolver().delete(Feed.SimplePost.CONTENT_URI,
                "(" + Feed.SimplePost.FEED_NAME + "=\"" + FEED_NAME + "\")", null);
        loadPosts();
    }

    private AdapterView.OnItemClickListener contentShower = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor o = ((Cursor) adapter.getItem(position));
            Intent postViewIntent = new Intent(PostListActivity.this, PostViewActivity.class);
            postViewIntent.putExtra("description", o.getString(Feed.SimplePost.DESCRIPTION_COLUMN));
            startActivity(postViewIntent);
        }
    };

    public void refreshButton(View view) {
        refreshPosts();
    }

    static final String[] SUMMARY_PROJECTION = new String[] {
            Feed.SimplePost._ID,
            Feed.SimplePost.FEED_NAME,
            Feed.SimplePost.TITLE_NAME,
            Feed.SimplePost.DESCRIPTION_NAME,
            Feed.SimplePost.URL_NAME
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Feed.SimplePost.CONTENT_URI;

        String select = "(" + Feed.SimplePost.FEED_NAME + "=\"" + FEED_NAME + "\")";
        return new CursorLoader(getBaseContext(), baseUri,
                SUMMARY_PROJECTION, select, null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
