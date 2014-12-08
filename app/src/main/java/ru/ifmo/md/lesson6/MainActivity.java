package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class MainActivity extends Activity implements AddFeedDialogFragment.NoticeDialogListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private SimpleCursorAdapter adapter;
    private ListView listView;
    private boolean del = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String from[] = { Feed.SimpleFeed.TITLE_NAME, Feed.SimpleFeed.URL_NAME };
        int to[] = { android.R.id.text1, android.R.id.text2 };
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, null, from, to);

        listView = (ListView) findViewById(R.id.feed_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(contentShower);

        getLoaderManager().initLoader(0, null, this);
    }

    private AdapterView.OnItemClickListener contentShower = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor o = ((Cursor) adapter.getItem(position));
            if (del) {
                Uri uri = ContentUris.withAppendedId(Feed.SimpleFeed.CONTENT_URI,
                        o.getLong(Feed.SimpleFeed.ID_COLUMN));
                int cnt = getContentResolver().delete(uri, null, null);
                del = false;
                listView.setBackgroundColor(Color.WHITE);
                Log.i("x", "delete, count = " + cnt);
            } else {
                Intent feedViewIntent = new Intent(MainActivity.this, PostListActivity.class);
                feedViewIntent.putExtra("feed_name", o.getString(Feed.SimpleFeed.TITLE_COLUMN));
                feedViewIntent.putExtra("link", o.getString(Feed.SimpleFeed.URL_COLUMN));
                startActivity(feedViewIntent);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(),
                text,
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onDialogPositiveClick(String title, String url) {
        if (title.isEmpty()) {
            showToast(getString(R.string.empty_title));
        } else if (url.isEmpty()) {
            showToast(getString(R.string.empty_url));
        } else {
            ContentValues cv = new ContentValues();
            cv.put(Feed.SimpleFeed.TITLE_NAME, title);
            cv.put(Feed.SimpleFeed.URL_NAME, url);
            getContentResolver().insert(Feed.SimpleFeed.CONTENT_URI, cv);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_feed) {
            del = false;
            listView.setBackgroundColor(Color.WHITE);
            DialogFragment dialogFragment = new AddFeedDialogFragment();
            dialogFragment.show(getFragmentManager(), "add_feed");
            return true;
        }

        if (id == R.id.action_delete_feed) {
            showToast(getString(R.string.click_to_delete));
            listView.setBackgroundColor(Color.rgb(255, 200, 200));
            del = true;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (del) {
            listView.setBackgroundColor(Color.WHITE);
            del = false;
        } else {
            super.onBackPressed();
        }
    }


    static final String[] SUMMARY_PROJECTION = new String[] {
            Feed.SimpleFeed._ID,
            Feed.SimpleFeed.TITLE_NAME,
            Feed.SimpleFeed.URL_NAME
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Feed.SimpleFeed.CONTENT_URI;

//        String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
//                + Contacts.HAS_PHONE_NUMBER + "=1) AND ("
//                + Contacts.DISPLAY_NAME + " != '' ))";
        return new CursorLoader(getBaseContext(), baseUri,
                SUMMARY_PROJECTION, null, null,
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
