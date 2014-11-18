package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class PostViewActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_post_view);

        String postDescription = getIntent().getStringExtra("description");
        WebView webView = (WebView) this.findViewById(R.id.web_view);
        webView.loadData(postDescription, "text/html; charset=utf-8", "utf-8");
    }
}
