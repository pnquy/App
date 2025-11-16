package com.example.studentportalapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddAssignmentActivity extends BaseActivity {

    private MaterialToolbar toolbar;
    private EditText etAssignmentTitle, etAssignmentInstructions, etAssignmentPoints, etAssignmentDueDate;
    private Button btnAttachFile, btnAssign;
    private Calendar myCalendar;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_add_assignment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        toolbar = findViewById(R.id.toolbar_assignment);
        etAssignmentTitle = findViewById(R.id.et_assignment_title);
        etAssignmentInstructions = findViewById(R.id.et_assignment_instructions);
        etAssignmentPoints = findViewById(R.id.et_assignment_points);
        etAssignmentDueDate = findViewById(R.id.et_assignment_due_date);
        btnAttachFile = findViewById(R.id.btn_attach_file_assignment);
        btnAssign = findViewById(R.id.btn_assign_assignment);

        myCalendar = Calendar.getInstance();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };


        etAssignmentDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddAssignmentActivity.this, dateSetListener,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        btnAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etAssignmentTitle.getText().toString().trim();
                String points = etAssignmentPoints.getText().toString().trim();
                String dueDate = etAssignmentDueDate.getText().toString().trim();

                if (title.isEmpty()) {
                    etAssignmentTitle.setError("Tiêu đề không được trống");
                    return;
                }
                if (dueDate.isEmpty()) {
                    etAssignmentDueDate.setError("Vui lòng chọn hạn nộp");
                    return;
                }




                Toast.makeText(AddAssignmentActivity.this, "Đã giao bài: " + title, Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        btnAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddAssignmentActivity.this, "Mở trình chọn tệp...", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; // Định dạng ngày
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        etAssignmentDueDate.setText(sdf.format(myCalendar.getTime()));
    }
}