package com.example.course.model;

public class Assignment {
    private String title, points, buttonText, author, date;

    public Assignment(String title, String points, String buttonText, String author, String date) {
        this.title = title;
        this.points = points;
        this.buttonText = buttonText;
        this.author = author;
        this.date = date;
    }

    public String getTitle() { return title; }
    public String getPoints() { return points; }
    public String getButtonText() { return buttonText; }
    public String getAuthor() { return author; }
    public String getDate() { return date; }
}

