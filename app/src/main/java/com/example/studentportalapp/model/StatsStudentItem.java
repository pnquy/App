package com.example.studentportalapp.model;

import com.example.studentportalapp.data.Entity.HocVien;

public class StatsStudentItem {
    public HocVien hocVien;
    public double score;
    public boolean hasScore;

    public StatsStudentItem(HocVien hocVien, double score, boolean hasScore) {
        this.hocVien = hocVien;
        this.score = score;
        this.hasScore = hasScore;
    }
}