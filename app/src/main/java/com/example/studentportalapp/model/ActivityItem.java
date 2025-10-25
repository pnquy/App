package com.example.course.model;

public class ActivityItem {
    private final String author;
    private final String date;
    private final String title;
    private final String fileName;
    private final String views;

    public ActivityItem(String author, String date, String title, String fileName, String views) {
        this.author = author;
        this.date = date;
        this.title = title;
        this.fileName = fileName;
        this.views = views;
    }

    public String getAuthor() { return author; }
    public String getDate() { return date; }
    public String getTitle() { return title; }
    public String getFileName() { return fileName; }
    public String getViews() { return views; }
}
