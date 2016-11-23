package com.example.alexandryan.tapsecure;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        setTitle("TD TapSecure Notifications");
        //NOTE: back button page routing is declared in manifest as the parent
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



}
