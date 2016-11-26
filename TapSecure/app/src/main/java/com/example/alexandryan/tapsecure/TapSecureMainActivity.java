package com.example.alexandryan.tapsecure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class TapSecureMainActivity extends AppCompatActivity {

    Switch debitSwitch;
    Switch visaSwitch;
    Button settingsBtn;
    Boolean visaSwitchOn;
    Boolean debitSwitchOn;
    Boolean firstTimeTapSecure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_secure_main);
        //set page title
        setTitle("TD TapSecure");

        //initialize variables
        debitSwitch = (Switch) findViewById(R.id.enableChequingIF);
        visaSwitch = (Switch) findViewById(R.id.enableVisaIF);
        settingsBtn = (Button) findViewById(R.id.settingsBtn);

        //check shared prefs if first time
        firstTimeTapSecure = BankService.getSharedPrefs().getBoolean("firstTimeTapSecure", true);

        //set initial values
        getInitialSwitchValues();
        createSwitchListeners();

        settingsBtn.setVisibility(View.INVISIBLE); //set to invisible on page load

        //NOTE: back button page routing is declared in manifest as the parent
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void getInitialSwitchValues(){
        SharedPreferences.Editor editor = BankService.getSharedPrefs().edit();
        //get value from shared prefs
        visaSwitchOn = BankService.getSharedPrefs().getBoolean("VInteracFlashEnabled", false);
        System.out.println("V:" + visaSwitchOn);
        debitSwitchOn = BankService.getSharedPrefs().getBoolean("DInteracFlashEnabled", false);
        System.out.println("D:" + debitSwitchOn);
        //set switch toggles in gui
        visaSwitch.setChecked(visaSwitchOn);
        debitSwitch.setChecked(debitSwitchOn);
    }

    public void showHideAdvancedSettings(){
        //show advanced settings
        if(debitSwitchOn || visaSwitchOn){ //on
            settingsBtn.setVisibility(View.VISIBLE);
        } else { //off
            settingsBtn.setVisibility(View.INVISIBLE);
        }
    }

    public void createSwitchListeners(){
        debitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    debitSwitchOn = true;
                    BankService.DebitCard.setInteracFlashEnabled(debitSwitchOn);
                }
                else {
                    debitSwitchOn = false;
                    BankService.DebitCard.setInteracFlashEnabled(debitSwitchOn);
                }
                showHideAdvancedSettings();
            }
        });

        visaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    visaSwitchOn = true;
                    BankService.VisaCard.setInteracFlashEnabled(visaSwitchOn);
                }
                else {
                    visaSwitchOn = false;
                    BankService.VisaCard.setInteracFlashEnabled(visaSwitchOn);
                }
                showHideAdvancedSettings();
            }
        });
    }

    //These 2 methods are for the gear at the top
    public boolean onCreateOptionsMenu(Menu menu) {
        //creates settings gear in top right corner
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_gear,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //if gear is selected in top right corner
        if(item.getItemId() == R.id.settings_id){
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSettingsClick(View view) {
        if(firstTimeTapSecure) {
            startActivity(new Intent(this, TapSecureSplashActivity.class));
            firstTimeTapSecure = false;
        } else {
            startActivity(new Intent(this, SettingsActivity.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //save in shared prefs that it has been visited
        SharedPreferences.Editor editor = BankService.getSharedPrefs().edit();
        editor.putBoolean("firstTimeTapSecure",firstTimeTapSecure);
        editor.commit();
    }
}
