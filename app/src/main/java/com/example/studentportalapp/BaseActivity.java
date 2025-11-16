package com.example.studentportalapp;
import com.example.studentportalapp.adapter.TaskAdapter;
import com.example.studentportalapp.model.Task;
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
import android.widget.ListView;
import android.widget.PopupWindow;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import android.view.Gravity;
public abstract class BaseActivity extends AppCompatActivity {

    protected FrameLayout containerBody;

    protected abstract int getLayoutResourceId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

        FrameLayout contentFrame = findViewById(R.id.content_frame);

        LayoutInflater.from(this).inflate(getLayoutResourceId(), contentFrame, true);
        setupHeaderMenu();
        setupFooterNavigation();
        setupHeader();
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
            icCheck.setOnClickListener(v -> showTaskPopup(v));
        }


        ImageView icBell = findViewById(R.id.notificationIcon);
        if (icBell != null) {
            icBell.setOnClickListener(v -> showPopupLayout(v, R.layout.popup_noti));
        }
    }


    private void showTaskPopup(View anchor) {
        View popupView = getLayoutInflater().inflate(R.layout.popup_task, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );


        ListView popupList = popupView.findViewById(R.id.list_tasks_popup);
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Task 5 - Group Work", "Programming Language", "Due Tomorrow | Aug 6"));
        tasks.add(new Task("Assignment No. 3", "Operating System", "Due Saturday | Aug 7"));
        tasks.add(new Task("Task 6 - Group Work", "Programming Language", "Due Sunday | Aug 8"));
        tasks.add(new Task("Task 1 - Create a Flowchart", "Human Computer Interaction", "Due Monday | Aug 9"));
        tasks.add(new Task("Assignment No. 5", "Logic Design", "Due Friday | Aug 13"));
        tasks.add(new Task("Assignment No. 6", "Logic Design", "Due Friday | Aug 13"));

        TaskAdapter adapter = new TaskAdapter(this, tasks);
        popupList.setAdapter(adapter);

        popupWindow.showAsDropDown(anchor, 0, 0);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
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
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
    }



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
