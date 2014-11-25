package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.IOError;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;


public class PostListActivity extends Activity {
    private ArrayList<PostItem> posts = new ArrayList<PostItem>();
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        ListView listView = (ListView) findViewById(R.id.post_list);
        postAdapter = new PostAdapter(this, R.layout.post_item, posts);
        listView.setAdapter(postAdapter);
        listView.setOnItemClickListener(contentShower);

        String feedLink = getIntent().getStringExtra("link");
        RssDataController async = new RssDataController();
        async.execute(feedLink);
    }

    private AdapterView.OnItemClickListener contentShower = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PostItem data = posts.get(position);
            if (!data.isError()) {
                Intent postViewIntent = new Intent(PostListActivity.this, PostViewActivity.class);
                postViewIntent.putExtra("description", data.getPostDescription());
                startActivity(postViewIntent);
            }
        }
    };

    private class RssDataController extends AsyncTask<String, Integer, ArrayList<PostItem>> {
        private String errorText = null;

        @Override
        protected ArrayList<PostItem> doInBackground(String... urls) {
            ArrayList<PostItem> postItemList = null;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                PostParser postParser = new PostParser(connection.getInputStream());
                postItemList = postParser.parse();
            } catch (MalformedURLException e) {
                errorText = getString(R.string.malformed_url);
                e.printStackTrace();
            } catch (IOException e) {
                errorText = getString(R.string.check_internet);
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                errorText = getString(R.string.unsupported_rss);
                e.printStackTrace();
            } catch (SAXException e) {
                errorText = getString(R.string.unsupported_rss);
                e.printStackTrace();
            } catch (Exception e) {
                errorText = getString(R.string.unknown_error);
                e.printStackTrace();
            }

            return postItemList;
        }

        @Override
        protected void onPostExecute(ArrayList<PostItem> result) {
            try {
                for (PostItem aResult : result) {
                    posts.add(aResult);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (errorText != null) {
                postAdapter.add(new PostItem(errorText));
            }

            if (postAdapter.isEmpty()) {
                postAdapter.add(new PostItem(getString(R.string.no_posts)));
            }

            postAdapter.notifyDataSetChanged();
        }
    }
}
