package com.example.course;


import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.course.adapter.PeopleAdapter;
import com.example.course.model.Person;
import java.util.ArrayList;
import java.util.List;

public class PeopleActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        // Chỉ định layout riêng của activity này
        return R.layout.activity_people;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_people);

        // Professors
        RecyclerView recyclerViewProfessors = findViewById(R.id.recyclerViewProfessors);
        recyclerViewProfessors.setLayoutManager(new LinearLayoutManager(this));
        List<Person> professors = new ArrayList<>();
        professors.add(new Person("James Gosling", "Professor"));
        recyclerViewProfessors.setAdapter(new PeopleAdapter(professors));

        // Students
        RecyclerView recyclerViewStudents = findViewById(R.id.recyclerViewStudents);
        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));
        List<Person> students = new ArrayList<>();
        students.add(new Person("Antonio, Mark", "Student"));
        students.add(new Person("Aramil, Nicole", "Student"));
        students.add(new Person("Baganian, Benladin", "Student"));
        students.add(new Person("Balili, Jayvie", "Student"));
        students.add(new Person("Banca, Bernadette", "Student"));
        students.add(new Person("Bergantinos, Eugene", "Student"));
        recyclerViewStudents.setAdapter(new PeopleAdapter(students));

    }
}


