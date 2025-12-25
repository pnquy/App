package com.example.studentportalapp.model;

public class Task {
    String title, subject, dueDate, maBT, userRole;
    int submissionCount, totalStudents;

    // Constructor for students
    public Task(String title, String subject, String dueDate, String maBT, String userRole) {
        this.title = title;
        this.subject = subject;
        this.dueDate = dueDate;
        this.maBT = maBT;
        this.userRole = userRole;
    }

    // Constructor for teachers
    public Task(String title, String subject, String dueDate, String maBT, String userRole, int submissionCount, int totalStudents) {
        this.title = title;
        this.subject = subject;
        this.dueDate = dueDate;
        this.maBT = maBT;
        this.userRole = userRole;
        this.submissionCount = submissionCount;
        this.totalStudents = totalStudents;
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

    public String getMaBT() {
        return maBT;
    }

    public String getUserRole() {
        return userRole;
    }

    public int getSubmissionCount() {
        return submissionCount;
    }

    public int getTotalStudents() {
        return totalStudents;
    }
}
