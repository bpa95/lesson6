package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends Activity implements AddFeedDialogFragment.NoticeDialogListener {
    private ArrayList<FeedItem> feeds = new ArrayList<FeedItem>();
    private FeedAdapter feedAdapter;
    private boolean del = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FeedItem bash = new FeedItem("bash", getString(R.string.bash_feed_url));
        feeds.add(bash);

        ListView listView = (ListView) findViewById(R.id.feed_list);
        feedAdapter = new FeedAdapter(this, R.layout.feed_item, feeds);
        listView.setAdapter(feedAdapter);
        listView.setOnItemClickListener(contentShower);
    }

    private AdapterView.OnItemClickListener contentShower = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FeedItem data = feeds.get(position);
            if (del) {
                feedAdapter.remove(data);
                del = false;
            } else {
                Intent feedViewIntent = new Intent(MainActivity.this, PostListActivity.class);
                feedViewIntent.putExtra("link", data.getFeedLink());
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
            FeedItem newItem = new FeedItem(title, url);
            feedAdapter.add(newItem);
            feedAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_feed) {
            del = false;
            DialogFragment dialogFragment = new AddFeedDialogFragment();
            dialogFragment.show(getFragmentManager(), "add_feed");
            return true;
        }

        if (id == R.id.action_delete_feed) {
            showToast(getString(R.string.click_to_delete));
            del = true;
        }

        return super.onOptionsItemSelected(item);
    }
}
