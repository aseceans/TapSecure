package com.example.alexandryan.tapsecure;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    Spinner accountSpinner;
    EditText tapDollarDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("TD TapSecure Settings");
        tapDollarDisplay = (EditText) findViewById(R.id.amountEntered);
        setCursorVisibility();
        createSpinner();
    }

    public void setCursorVisibility(){
        tapDollarDisplay.setCursorVisible(false);

        tapDollarDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tapDollarDisplay.setCursorVisible(true);
            }
        });

        tapDollarDisplay.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                tapDollarDisplay.setCursorVisible(false);
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(tapDollarDisplay.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "back pressed", Toast.LENGTH_SHORT).show();
        tapDollarDisplay.setFocusable(false);
    }

    public void createSpinner(){
        // Creating adapter for spinner
        accountSpinner = (Spinner) findViewById(R.id.accountSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, getResources().getStringArray(R.array.accounts)); //

        // attaching data adapter to spinner
        accountSpinner.setAdapter(adapter);
        // Drop down layout style - list view with radio button
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //NOTE: back button page routing is declared in manifest as the parent
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onDollarAmtClick(View view) {

        switch (view.getId()){
            case R.id.button50:
                tapDollarDisplay.setText("50");
                break;
            case R.id.button100:
                tapDollarDisplay.setText("100");
                break;
            case R.id.button250:
                tapDollarDisplay.setText("250");
                break;
        }

    }
}
