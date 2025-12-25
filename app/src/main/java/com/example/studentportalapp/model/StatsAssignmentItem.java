package com.example.studentportalapp.model;

import com.example.studentportalapp.data.Entity.BaiTap;

public class StatsAssignmentItem {
    public BaiTap baiTap;
    public double averageScore;
    public int submissionCount;

    public StatsAssignmentItem(BaiTap baiTap, double averageScore, int submissionCount) {
        this.baiTap = baiTap;
        this.averageScore = averageScore;
        this.submissionCount = submissionCount;
    }
}