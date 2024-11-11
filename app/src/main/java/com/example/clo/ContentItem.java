package com.example.clo;

public class ContentItem {
    private String title;
    private String url;

    public ContentItem(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}