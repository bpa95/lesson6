package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PostAdapter extends ArrayAdapter<PostData> {
    private Activity context;
    private ArrayList<PostData> posts;
    private int viewId;

    public PostAdapter(Context context, int viewId, ArrayList<PostData> posts) {
        super(context, viewId, posts);
        this.context = (Activity) context;
        this.viewId = viewId;
        this.posts = posts;
    }

    private static class ViewHolder {
        TextView postTitleView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(viewId, null);

            viewHolder = new ViewHolder();
            viewHolder.postTitleView = (TextView) convertView.findViewById(R.id.post_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.postTitleView.setText(posts.get(position).getPostTitle());

        return convertView;
    }
}