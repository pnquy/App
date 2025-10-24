package com.example.studentportalapp.model;
public class Task {
    String title, subject, dueDate;

    public Task(String title, String subject, String dueDate) {
        this.title = title;
        this.subject = subject;
        this.dueDate = dueDate;
    }

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

    public String getDueDate() {
        return dueDate;
    }
}
