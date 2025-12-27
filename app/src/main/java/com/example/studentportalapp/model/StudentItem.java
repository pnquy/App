package com.example.studentportalapp.model;

import com.example.studentportalapp.data.Entity.HocVien;

public class StudentItem {
    public HocVien hocVien;
    public String classNames;

    public StudentItem(HocVien hocVien, String classNames) {
        this.hocVien = hocVien;
        this.classNames = classNames;
    }
}