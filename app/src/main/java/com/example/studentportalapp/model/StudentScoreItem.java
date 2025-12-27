package com.example.studentportalapp.model;

public class StudentScoreItem {
    public String assignmentName;
    public double myScore;
    public double classAverage;

    public StudentScoreItem(String assignmentName, double myScore, double classAverage) {
        this.assignmentName = assignmentName;
        this.myScore = myScore;
        this.classAverage = classAverage;
    }
}