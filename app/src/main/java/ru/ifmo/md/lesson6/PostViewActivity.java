package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class PostViewActivity extends Activity {
    public static final String EXTRA_URL = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_post_view);

        String postUrl = getIntent().getStringExtra(EXTRA_URL);
        WebView webView = (WebView) this.findViewById(R.id.web_view);
//        webView.loadData(postDescription, "text/html; charset=utf-8", "utf-8");
        webView.loadUrl(postUrl);
    }
}
