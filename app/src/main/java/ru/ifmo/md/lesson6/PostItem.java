package ru.ifmo.md.lesson6;

public class PostItem {
    private String postLink;
    private String postTitle;
    private String postDate;
    private String postDescription;
    private boolean er = false;

    public PostItem() {
    }

    //use only for errors
    public PostItem(String title) {
        postTitle = title;
        er = true;
    }

    public boolean isError() {
        return er;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostLink() {
        return postLink;
    }

    public void setPostLink(String postLink) {
        this.postLink = postLink;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }
}
