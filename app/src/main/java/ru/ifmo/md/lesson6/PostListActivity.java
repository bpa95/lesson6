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
    private ArrayList<PostItem> posts = new ArrayList<PostItem>();
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
        String feedLink = getIntent().getStringExtra("link");
        RssDataController async = new RssDataController();
        async.execute(feedLink);
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

    private class RssDataController extends AsyncTask<String, Integer, ArrayList<PostItem>> {
        private String errorText = null;

        @Override
        protected ArrayList<PostItem> doInBackground(String... urls) {
            ArrayList<PostItem> postItemList = null;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream is = connection.getInputStream();

                postItemList = SAXXMLParser.parse(is);
            } catch (MalformedURLException e) {
                errorText = getString(R.string.malformed_url);
                e.printStackTrace();
            } catch (IOException e) {
                errorText = getString(R.string.check_internet);
                e.printStackTrace();
            } catch (Exception e) {
                errorText = getString(R.string.unknown_error);
                e.printStackTrace();
            }

            return postItemList;
        }

        @Override
        protected void onPostExecute(ArrayList<PostItem> result) {
            if (result == null || result.isEmpty()) {
                if (errorText == null)
                    errorText = getString(R.string.no_posts);
            } else {
                try {
                    ContentValues cv = new ContentValues();
                    for (PostItem aResult : result) {
                        cv.clear();
                        cv.put(Feed.SimplePost.FEED_NAME, FEED_NAME);
                        cv.put(Feed.SimplePost.TITLE_NAME, aResult.getPostTitle());
                        cv.put(Feed.SimplePost.DESCRIPTION_NAME, aResult.getPostDescription());
                        cv.put(Feed.SimplePost.URL_NAME, aResult.getPostLink());
                        getContentResolver().insert(Feed.SimplePost.CONTENT_URI, cv);
                    }
                } catch (Exception e) {
                    if (errorText == null)
                        errorText = getString(R.string.no_posts);
                    e.printStackTrace();
                }
            }

            if (errorText != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(errorText);
                    }
                });
            }
        }
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
