package com.example.studentportalapp;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

import com.example.studentportalapp.model.Task;
import com.example.studentportalapp.adapter.TaskAdapter;

public class ToDoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listView = findViewById(R.id.list_tasks);
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Task 5 - Group Work", "Programming Language", "Due Tomorrow | Aug 6"));
        tasks.add(new Task("Assignment No. 3", "Operating System", "Due Saturday | Aug 7"));
        tasks.add(new Task("Task 6 - Group Work", "Programming Language", "Due Sunday | Aug 8"));
        tasks.add(new Task("Task 1 - Create a Flowchart", "Human Computer Interaction", "Due Monday | Aug 9"));
        tasks.add(new Task("Assignment No. 5", "Logic Design", "Due Friday | Aug 13"));
        tasks.add(new Task("Assignment No. 6", "Logic Design", "Due Friday | Aug 13"));

        TaskAdapter adapter = new TaskAdapter(this, tasks);
        listView.setAdapter(adapter);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_todo;
    }
}
