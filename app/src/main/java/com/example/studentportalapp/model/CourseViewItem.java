package com.example.studentportalapp.model;

import com.example.studentportalapp.data.Entity.LopHoc;

public class CourseViewItem {
    public LopHoc lopHoc;
    public String progressText;
    public int progressValue;

    public CourseViewItem(LopHoc lopHoc, String progressText, int progressValue) {
        this.lopHoc = lopHoc;
        this.progressText = progressText;
        this.progressValue = progressValue;
    }
}