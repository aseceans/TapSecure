package com.example.alexandryan.tapsecure;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import static com.example.alexandryan.tapsecure.R.id.accountSpinner;

public class NotificationsActivity extends AppCompatActivity {
    Spinner accountSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        createSpinner();
        setTitle("TD TapSecure Notifications");
        //NOTE: back button page routing is declared in manifest as the parent
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void createSpinner(){
        // Creating adapter for spinner
        accountSpinner = (Spinner) findViewById(R.id.accountSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, getResources().getStringArray(R.array.accounts)); //

        // attaching data adapter to spinner
        accountSpinner.setAdapter(adapter);
        // Drop down layout style - list view with radio button
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        setTitle("TD TapSecure Settings");
        //NOTE: back button page routing is declared in manifest as the parent
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
