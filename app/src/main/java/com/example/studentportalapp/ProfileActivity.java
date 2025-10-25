package com.example.studentportalapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ View
        TextView tvStudentNo = findViewById(R.id.tvStudentNo);
        TextView tvCourse = findViewById(R.id.tvCourse);
        TextView tvYearSec = findViewById(R.id.tvYearSec);
        TextView tvBirth = findViewById(R.id.tvBirth);
        TextView tvGender = findViewById(R.id.tvGender);
        TextView tvNationality = findViewById(R.id.tvNationality);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvContact = findViewById(R.id.tvContact);
        TextView tvAddress = findViewById(R.id.tvAddress);
        ImageView imgAvatar = findViewById(R.id.imgAvatar);

        // Gán dữ liệu tạm
        tvStudentNo.setText("19-02031-t");
        tvCourse.setText("Bachelor of Science in Computer Science");
        tvYearSec.setText("2ND Year, J2019");
        tvBirth.setText("04-21-2001");
        tvGender.setText("Male");
        tvNationality.setText("Filipino");
        tvEmail.setText("balilijayvie201@gmail.com");
        tvContact.setText("09619910340");
        tvAddress.setText("Block 5 Lot 12 southern valley katwiran extension ibayo tipas taguig city");

        imgAvatar.setImageResource(R.mipmap.ic_launcher); // thay ảnh thật của bạn ở đây
    }
}

