package com.example.studentportalapp;
import com.example.studentportalapp.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentportalapp.adapter.TaskAdapter;
import com.example.studentportalapp.model.Task;

import java.util.ArrayList;
import java.util.List;


public class OptionsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        // Gán sự kiện bấm icon để hiển thị popup
        ImageView icCheck = findViewById(R.id.img_check);
        icCheck.setOnClickListener(v -> {
            // Inflate popup layout
            View popupView = getLayoutInflater().inflate(R.layout.popup_task, null);

            // Tạo popup window
            PopupWindow popupWindow = new PopupWindow(popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true);

            // Ánh xạ ListView trong popup
            ListView popupList = popupView.findViewById(R.id.list_tasks_popup);

            // Tạo dữ liệu giống trong ToDoActivity
            List<Task> tasks = new ArrayList<>();
            tasks.add(new Task("Task 5 - Group Work", "Programming Language", "Due Tomorrow | Aug 6"));
            tasks.add(new Task("Assignment No. 3", "Operating System", "Due Saturday | Aug 7"));
            tasks.add(new Task("Task 6 - Group Work", "Programming Language", "Due Sunday | Aug 8"));
            tasks.add(new Task("Task 1 - Create a Flowchart", "Human Computer Interaction", "Due Monday | Aug 9"));
            tasks.add(new Task("Assignment No. 5", "Logic Design", "Due Friday | Aug 13"));
            tasks.add(new Task("Assignment No. 6", "Logic Design", "Due Friday | Aug 13"));

            // Gán adapter vào ListView trong popup
            TaskAdapter adapter = new TaskAdapter(this, tasks);
            popupList.setAdapter(adapter);

            // Hiển thị popup ở dưới icon
            popupWindow.showAsDropDown(icCheck, 0, 0);
        });

        ImageView icBell = findViewById(R.id.img_bell);
        icBell.setOnClickListener(v -> showPopup(v, R.layout.popup_noti));

        // Gán sự kiện bấm avatar để quay lại trang main
        ImageView imgAvatar = findViewById(R.id.img_avatar);

        imgAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(OptionsActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // kết thúc ToDoActivity (tùy bạn có muốn quay lại hay không)
        });

        TextView contactText = findViewById(R.id.tv_mail);
        ImageView mailIcon = findViewById(R.id.img_mail);

        View.OnClickListener showContactPopup = v -> {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_contact, null);

            PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true
            );

            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            // Gắn sự kiện cho các thành phần
            TextView tvPhone = popupView.findViewById(R.id.tv_phone);
            TextView tvMessenger = popupView.findViewById(R.id.tv_messenger);
            TextView tvGmail = popupView.findViewById(R.id.tv_gmail);

            tvPhone.setOnClickListener(view->{
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:0123456789"));
                startActivity(intent);
            });

            tvMessenger.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"));
                startActivity(intent);
            });

            tvGmail.setOnClickListener(view -> {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:nhom6@gmail.com"));
                startActivity(emailIntent);
            });
        };

        // Áp dụng cho cả icon và text
        contactText.setOnClickListener(showContactPopup);
        mailIcon.setOnClickListener(showContactPopup);

    }

    private void showPopup(View anchorView, int layoutId) {
        View popupView = LayoutInflater.from(this).inflate(layoutId, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        // Hiển thị popup ngay bên dưới icon
        popupWindow.showAsDropDown(anchorView, -150, 0, Gravity.END);

        // Cho phép bấm ra ngoài để đóng popup
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
    }

}
