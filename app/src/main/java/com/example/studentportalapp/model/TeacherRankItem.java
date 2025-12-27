package com.example.studentportalapp.model;

public class TeacherRankItem {
    public String TenGV;
    public int Count;

    // QUAN TRỌNG: Phải có Constructor rỗng để Room sử dụng
    public TeacherRankItem() {
    }

    // Constructor này để bạn dùng nếu cần (optional)
    public TeacherRankItem(String tenGV, int count) {
        this.TenGV = tenGV;
        this.Count = count;
    }
}