package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import java.util.Timer;
import java.util.TimerTask;


public class PostListActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    private SimpleCursorAdapter adapter;
    private String FEED_NAME;
    public static final String EXTRA_FEED_NAME = "feed_name";
    public static final String EXTRA_LINK = "link";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        FEED_NAME = getIntent().getStringExtra(EXTRA_FEED_NAME);

        String from[] = { Feed.SimplePost.TITLE_NAME };
        int to[] = { android.R.id.text1 };
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, null, from, to);

        ListView listView = (ListView) findViewById(R.id.post_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(contentShower);

        getLoaderManager().initLoader(0, null, this);
    }

    private void loadPosts() {
        Intent intent = new Intent(this, RSSPullService.class);
        intent.putExtra(RSSPullService.EXTRA_URL, getIntent().getStringExtra(EXTRA_LINK));
        intent.putExtra(RSSPullService.EXTRA_FEED_NAME, FEED_NAME);
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
            postViewIntent.putExtra(PostViewActivity.EXTRA_URL,
                    o.getString(Feed.SimplePost.URL_COLUMN));
            startActivity(postViewIntent);
        }
    };

    public void refreshButton(View view) {
        refreshPosts();
        final Button button = (Button) findViewById(R.id.refresh_button);
        button.setEnabled(false);
        button.setTextColor(Color.parseColor("#999999"));

        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        button.setEnabled(true);
                        button.setTextColor(Color.BLACK);
                    }
                });
            }
        }, 10000);
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
