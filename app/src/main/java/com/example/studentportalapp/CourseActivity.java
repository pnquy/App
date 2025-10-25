package com.example.studentportalapp;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.course.adapter.CourseAdapter;
import com.example.course.model.ActivityItem;
import java.util.ArrayList;

public class CourseActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_course;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView recyclerView = findViewById(R.id.rvActivityCourse);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<ActivityItem> activityList = new ArrayList<>();
        activityList.add(new ActivityItem("James Gosling", "July 13, 2021", "Java Stack Program", "JavaStack.docx", "26"));
        activityList.add(new ActivityItem("James Gosling", "July 12, 2021", "Data Structures - Lesson 6", "Lesson6.docx", "28"));
        activityList.add(new ActivityItem("Bjarne Stroustrup", "July 10, 2021", "C++ Template Programming", "C++Templates.pdf", "19"));
        activityList.add(new ActivityItem("Bjarne Stroustrup", "July 9, 2021", "C++ Basics", "C++.pdf", "25"));

        CourseAdapter adapter = new CourseAdapter(activityList);
        recyclerView.setAdapter(adapter);
    }
}
