package com.example.studentportalapp.model;

public class Person {
    private String id; // Thêm ID
    private String name;
    private String role;

    public Person(String id, String name, String role) { // Cập nhật Constructor
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public String getId() { return id; } // Getter mới
    public String getName() { return name; }
    public String getRole() { return role; }
}