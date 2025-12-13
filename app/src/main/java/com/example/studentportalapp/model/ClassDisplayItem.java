package com.example.studentportalapp.model;

import com.example.studentportalapp.data.Entity.LopHoc;

public class ClassDisplayItem {
    public LopHoc lopHoc;
    public String tenGV;
    public int siSo;

    public ClassDisplayItem(LopHoc lopHoc, String tenGV, int siSo) {
        this.lopHoc = lopHoc;
        this.tenGV = tenGV;
        this.siSo = siSo;
    }
}