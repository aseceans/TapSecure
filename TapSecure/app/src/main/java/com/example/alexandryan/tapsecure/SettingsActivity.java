package com.example.alexandryan.tapsecure;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    Spinner accountSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        createSpinner();

    }

    public void createSpinner(){


        // Creating adapter for spinner
        accountSpinner = (Spinner) findViewById(R.id.accountSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.accounts));

        // attaching data adapter to spinner
        accountSpinner.setAdapter(adapter);
        // Drop down layout style - list view with radio button
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        setTitle("TD TapSecure Settings");
        //NOTE: back button page routing is declared in manifest as the parent
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_gear,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings_id:
                Intent intent = new Intent(this, NotificationsActivity.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
