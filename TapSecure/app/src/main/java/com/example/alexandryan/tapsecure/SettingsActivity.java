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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    Spinner accountSpinner;
    EditText tapDollarDisplay;
    Switch tapSecureEnabledSwitch;
    ScrollView tapSecureSettingsHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        tapSecureSettingsHolder = (ScrollView) findViewById(R.id.tapSecureSettingsHolder);
        tapSecureSettingsHolder.setVisibility(View.INVISIBLE);
        tapSecureEnabledSwitch = (Switch) findViewById(R.id.tapSecureEnabledSwitch);
        tapSecureEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    tapSecureSettingsHolder.setVisibility(View.VISIBLE);
                else
                    tapSecureSettingsHolder.setVisibility(View.INVISIBLE);
            }
        });

        setTitle("TD TapSecure Settings");
        tapDollarDisplay = (EditText) findViewById(R.id.amountEntered);
        setCursorVisibility();
        createSpinner();
    }

    public void setCursorVisibility(){
        //this line prevents keyboard from opening when activity launches
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        tapDollarDisplay.setFocusable(false);
    }

    public void createSpinner(){
        List<String> accounts = new ArrayList<String>();
        if(BankService.getSharedPrefs().getBoolean("VInteracFlashEnabled", false)){
            accounts.add(BankService.VisaCard.getCardDescription());
        }
        if (BankService.getSharedPrefs().getBoolean("DInteracFlashEnabled", false)){
            accounts.add(BankService.DebitCard.getCardDescription());
        }

        // Creating adapter for spinner
        accountSpinner = (Spinner) findViewById(R.id.accountSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, accounts); //

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
