package com.example.studentportalapp;

import android.os.Bundle;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Không cần setContentView vì BaseActivity đã lo việc đó
    }

    @Override
    protected int getLayoutResourceId() {
        // Đây là layout riêng của Home
        return R.layout.activity_home;
    }
}
