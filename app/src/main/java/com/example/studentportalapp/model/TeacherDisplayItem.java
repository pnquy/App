package com.example.studentportalapp.model;

import com.example.studentportalapp.data.Entity.GiaoVien;

public class TeacherDisplayItem {
    public GiaoVien giaoVien;
    public int classCount;

    public TeacherDisplayItem(GiaoVien giaoVien, int classCount) {
        this.giaoVien = giaoVien;
        this.classCount = classCount;
    }
}