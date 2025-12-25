package com.example.studentportalapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.studentportalapp.adapter.TaskAdapter;
import com.example.studentportalapp.model.Task;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {

    protected abstract int getLayoutResourceId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        FrameLayout contentFrame = findViewById(R.id.content_frame);
        LayoutInflater.from(this).inflate(getLayoutResourceId(), contentFrame, true);

        setupHeader();
        setupFooterNavigation();
        updateFooterVisuals();
    }

    private void setupHeader() {
        ImageView avatar = findViewById(R.id.avatar);
        if (avatar != null) {
            avatar.setOnClickListener(view -> {
                PopupMenu popup = new PopupMenu(BaseActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.menu_avatar, popup.getMenu());
                popup.setOnMenuItemClickListener(this::handleMenuClick);
                popup.show();
            });
        }

        ImageView icCheck = findViewById(R.id.tickIcon);
        if (icCheck != null) {
            icCheck.setOnClickListener(this::showTaskPopup);
        }

        ImageView icBell = findViewById(R.id.notificationIcon);
        if (icBell != null) {
            icBell.setOnClickListener(v -> showPopupLayout(v, R.layout.popup_noti));
        }
    }

    private boolean handleMenuClick(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_home && !(this instanceof HomeActivity)) {
            startActivity(new Intent(this, HomeActivity.class));
            return true;
        } else if (id == R.id.menu_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.menu_courses && !(this instanceof CourseActivity)) {
            startActivity(new Intent(this, CourseActivity.class));
            return true;
        } else if (id == R.id.menu_logout) {
            performLogout();
            return true;
        }
        return false;
    }

    private void performLogout() {
        SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showTaskPopup(View anchor) {
        View popupView = getLayoutInflater().inflate(R.layout.popup_task, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                (int) (getResources().getDisplayMetrics().widthPixels * 0.85),
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        ListView popupList = popupView.findViewById(R.id.list_tasks_popup);
        List<Task> tasks = getMockTasks();
        TaskAdapter adapter = new TaskAdapter(this, tasks);
        popupList.setAdapter(adapter);

        popupWindow.showAsDropDown(anchor, 0, 0);
    }

    private void showPopupLayout(View anchorView, int layoutId) {
        View popupView = LayoutInflater.from(this).inflate(layoutId, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.showAsDropDown(anchorView, -150, 0, Gravity.END);
    }

    private void setupFooterNavigation() {
        LinearLayout navCourse = findViewById(R.id.nav_course);
        LinearLayout navAssignment = findViewById(R.id.nav_assignment);
        LinearLayout navPeople = findViewById(R.id.nav_people);

        if (navCourse != null) navCourse.setOnClickListener(v -> navigateTo(CourseActivity.class));
        if (navAssignment != null) navAssignment.setOnClickListener(v -> navigateTo(AssignmentActivity.class));
        if (navPeople != null) navPeople.setOnClickListener(v -> navigateTo(PeopleActivity.class));
    }

    private void navigateTo(Class<?> targetActivity) {
        if (!this.getClass().equals(targetActivity)) {
            Intent intent = new Intent(this, targetActivity);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    private void updateFooterVisuals() {
        int colorActive = ContextCompat.getColor(this, R.color.purple_500);

        if (this instanceof CourseActivity) {
            highlightFooterItem(R.id.nav_course, colorActive);
        } else if (this instanceof AssignmentActivity) {
            highlightFooterItem(R.id.nav_assignment, colorActive);
        } else if (this instanceof PeopleActivity) {
            highlightFooterItem(R.id.nav_people, colorActive);
        }
    }

    private void highlightFooterItem(int navId, int color) {
        LinearLayout navItem = findViewById(navId);
        if (navItem != null) {
            for (int i = 0; i < navItem.getChildCount(); i++) {
                View child = navItem.getChildAt(i);
                if (child instanceof ImageView) {
                    ((ImageView) child).setColorFilter(color, PorterDuff.Mode.SRC_IN);
                } else if (child instanceof TextView) {
                    ((TextView) child).setTextColor(color);
                }
            }
        }
    }

    private List<Task> getMockTasks() {
        List<Task> tasks = new ArrayList<>();
        return tasks;
    }
}