package com.example.studentportalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected FrameLayout containerBody;

    protected abstract int getLayoutResourceId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Gắn layout chung có header
        setContentView(R.layout.activity_base);

        // Lấy FrameLayout (nơi sẽ chèn nội dung của từng activity)
        FrameLayout contentFrame = findViewById(R.id.content_frame);

        // Inflate layout riêng của từng activity vào khung nội dung
        LayoutInflater.from(this).inflate(getLayoutResourceId(), contentFrame, true);
        setupHeaderMenu();
        setupFooterNavigation();
    }



    /** ---------------- HEADER ---------------- **/
    private void setupHeaderMenu() {
        ImageView avatar = findViewById(R.id.avatar); // ID trong header_layout.xml
        if (avatar != null) {
            avatar.setOnClickListener(view -> {
                PopupMenu popup = new PopupMenu(BaseActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.menu_avatar, popup.getMenu());

                popup.setOnMenuItemClickListener(item -> handleMenuClick(item));
                popup.show();
            });
        }
    }

    private boolean handleMenuClick(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_home) {
            startActivity(new Intent(this, HomeActivity.class));
            return true;

        } else if (id == R.id.menu_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;

        } else if (id == R.id.menu_courses) {
            startActivity(new Intent(this, CourseActivity.class));
            return true;

        } else if (id == R.id.menu_todo) {
            startActivity(new Intent(this, ToDoActivity.class));
            return true;

        } else if (id == R.id.menu_options) {
            startActivity(new Intent(this, OptionsActivity.class));
            return true;

        } else if (id == R.id.menu_logout) {
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            // Ví dụ: chuyển về LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return false;
    }

    /** ---------------- FOOTER ---------------- **/
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
