package com.example.course;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.course.adapter.AssignmentAdapter;
import com.example.course.model.Assignment;
import java.util.ArrayList;
import java.util.List;

public class AssignmentActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        // Chỉ định layout riêng của activity này
        return R.layout.activity_assignment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_assignment);

        RecyclerView recyclerViewAssignments = findViewById(R.id.recyclerViewAssignments);
        recyclerViewAssignments.setLayoutManager(new LinearLayoutManager(this));

        List<Assignment> assignmentList = new ArrayList<>();
        assignmentList.add(new Assignment("Task 8 - Group Work", "100 points", "View", "James Gosling", "July 13, 2021"));
        assignmentList.add(new Assignment("Task 7 - Group Work", "100 points", "Unsubmit", "James Gosling", "July 13, 2021"));
        assignmentList.add(new Assignment("Task 6 - Midterm", "80 points", "View", "James Gosling", "July 10, 2021"));
        assignmentList.add(new Assignment("Task 5 - Review", "60 points", "View", "James Gosling", "July 7, 2021"));
        assignmentList.add(new Assignment("Task 4 - Java IO", "90 points", "Unsubmit", "James Gosling", "July 5, 2021"));

        AssignmentAdapter adapter = new AssignmentAdapter(this, assignmentList);
        recyclerViewAssignments.setAdapter(adapter);

    }
}
