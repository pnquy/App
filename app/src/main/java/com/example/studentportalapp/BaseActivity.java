package com.example.studentportalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected FrameLayout containerBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        containerBody = findViewById(R.id.container_body);
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(getLayoutResourceId(), containerBody, true);

        setupFooterNavigation();
    }

    protected abstract int getLayoutResourceId();

    private void setupFooterNavigation() {
        LinearLayout navCourse = findViewById(R.id.nav_course);
        LinearLayout navAssignment = findViewById(R.id.nav_assignment);
        LinearLayout navPeople = findViewById(R.id.nav_people);

        if (navCourse != null) {
            navCourse.setOnClickListener(v -> {
                if (!(this instanceof CourseActivity)) {
                    Intent intent = new Intent(this, CourseActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
        }

        if (navAssignment != null) {
            navAssignment.setOnClickListener(v -> {
                if (!(this instanceof AssignmentActivity)) {
                    Intent intent = new Intent(this, AssignmentActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
        }

        if (navPeople != null) {
            navPeople.setOnClickListener(v -> {
                if (!(this instanceof PeopleActivity)) {
                    Intent intent = new Intent(this, PeopleActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
        }
    }
}
