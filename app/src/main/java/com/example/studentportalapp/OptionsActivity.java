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


public class OptionsActivity extends BaseActivity {
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_options;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void showPopup(View anchorView, int layoutId) {
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

}
