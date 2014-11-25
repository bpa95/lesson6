package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FeedAdapter extends ArrayAdapter<FeedItem> {
    private Activity context;
    private ArrayList<FeedItem> feeds;
    private int viewId;

    public FeedAdapter(Context context, int viewId, ArrayList<FeedItem> feeds) {
        super(context, viewId, feeds);
        this.context = (Activity) context;
        this.viewId = viewId;
        this.feeds = feeds;
    }

    private static class ViewHolder {
        TextView feedTitleView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(viewId, null);

            viewHolder = new ViewHolder();
            viewHolder.feedTitleView = (TextView) convertView.findViewById(R.id.feed_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.feedTitleView.setText(feeds.get(position).getFeedTitle());

        return convertView;
    }
}