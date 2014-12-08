package ru.ifmo.md.lesson6;

public class FeedItem {
    private String feedTitle;
    private String feedLink;

    public FeedItem (String title, String link) {
        feedLink = link;
        feedTitle = title;
    }

    public String getFeedTitle() {
        return feedTitle;
    }

//    public void setFeedTitle(String feedTitle) {
//        this.feedTitle = feedTitle;
//    }
//
//    public String getFeedLink() {
//        return feedLink;
//    }
//
//    public void setFeedLink(String feedLink) {
//        this.feedLink = feedLink;
//    }
}
