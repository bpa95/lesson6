package ru.ifmo.md.lesson6;

import android.net.Uri;
import android.provider.BaseColumns;

public class Feed {
    public static final int ID_COLUMN = 0;
    public static final int TITLE_COLUMN = 1;
    public static final int URL_COLUMN = 2;

    public static final String AUTHORITY =
            "ru.ifmo.md.lesson6.provider.feed";

    public static final class SimpleFeed implements BaseColumns {
        public static final String DEFAULT_SORT_ORDER = "modified ASC";

        private SimpleFeed() {}

        public static final Uri FEED_URI = Uri.parse("content://" +
                AUTHORITY + "/" + SimpleFeed.FEED_NAME);

        public static final Uri CONTENT_URI = FEED_URI;

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.feed.data";

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.feed.data";

        public static final String FEED_NAME = "feed";

        public static final String TITLE_NAME = "title";

        public static final String URL_NAME = "url";
    }
}
