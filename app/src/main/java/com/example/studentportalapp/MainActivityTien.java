package com.example.studentportalapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.example.studentportalapp.R;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivityTien extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tien);

        Button btnOpenTodo = findViewById(R.id.btnOpenTodo);

        btnOpenTodo.setOnClickListener(v -> {
            // Mở trang activity_todo
            Intent intent = new Intent(MainActivityTien.this, ToDoActivity.class);
            startActivity(intent);
        });
        Button btnGoToOptions = findViewById(R.id.btnOpenOptions);

        btnGoToOptions.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivityTien.this, OptionsActivity.class);
            startActivity(intent);
        });


    }
}
