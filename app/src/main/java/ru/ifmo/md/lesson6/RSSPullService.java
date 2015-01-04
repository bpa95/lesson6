package ru.ifmo.md.lesson6;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RSSPullService extends IntentService {
    public static final String EXTRA_URL = "link";
    public static final String EXTRA_FEED_NAME = "feed_name";
    private Handler handler = new Handler();

    public RSSPullService() {
        super("RSSPullService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorText = null;
        String FEED_NAME = intent.getStringExtra(EXTRA_FEED_NAME);
        ArrayList<PostItem> postItemList = null;
        try {
            URL url = new URL(intent.getStringExtra(EXTRA_URL));
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

        if (postItemList == null || postItemList.isEmpty()) {
            if (errorText == null)
                errorText = getString(R.string.no_posts);
        } else {
            try {
                ContentValues cv = new ContentValues();
                for (PostItem aResult : postItemList) {
                    cv.clear();
                    cv.put(Feed.SimplePost.FEED_NAME, FEED_NAME);
                    cv.put(Feed.SimplePost.TITLE_NAME, aResult.getPostTitle());
                    cv.put(Feed.SimplePost.DESCRIPTION_NAME, aResult.getPostDescription());
                    cv.put(Feed.SimplePost.URL_NAME, aResult.getPostLink());
                    getContentResolver().insert(Feed.SimplePost.CONTENT_URI, cv);
                }
            } catch (Exception e) {
                errorText = getString(R.string.no_posts);
                e.printStackTrace();
            }
        }

        if (errorText != null) {
            showToast(errorText);
        }
    }

    private void showToast(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RSSPullService.this,
                        text,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}
