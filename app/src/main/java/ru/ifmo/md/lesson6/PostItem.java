package ru.ifmo.md.lesson6;

public class PostItem {
    private String postLink = null;
    private String postTitle = null;
    private String postDate = null;
    private String postDescription = null;
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


    public void appPostTitle(String postTitle) {
        if (this.postTitle == null)
            this.postTitle = postTitle;
        else
            this.postTitle += postTitle;
    }

    public void appPostLink(String postLink) {
        if (this.postLink == null)
            this.postLink = postLink;
        else
            this.postLink += postLink;
    }

    public void appPostDate(String postDate) {
        if (this.postDate == null)
            this.postDate = postDate;
        else
            this.postDate += postDate;
    }

    public void appPostDescription(String postDescription) {
        if (this.postDescription == null)
            this.postDescription = postDescription;
        else
            this.postDescription += postDescription;
    }
}
