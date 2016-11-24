package com.example.alexandryan.tapsecure;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class TapSecureMainActivity extends AppCompatActivity {

    Switch enableSwitch;
    Switch visaSwitch;
    Button notificationsBtn;
    Button settingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_secure_main);
        //initialize variables
        enableSwitch = (Switch) findViewById(R.id.enableTsSwitch);
        visaSwitch = (Switch) findViewById(R.id.visaSwitch);
        notificationsBtn = (Button) findViewById(R.id.notificationsBtn);
        settingsBtn = (Button) findViewById(R.id.settingsBtn);

        //set to invisible on page load:
        visaSwitch.setVisibility(View.INVISIBLE);
        notificationsBtn.setVisibility(View.INVISIBLE);
        settingsBtn.setVisibility(View.INVISIBLE);

        enableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){ //on
                    visaSwitch.setVisibility(View.VISIBLE);
                    notificationsBtn.setVisibility(View.VISIBLE);
                    settingsBtn.setVisibility(View.VISIBLE);
                } else { //off
                    visaSwitch.setVisibility(View.INVISIBLE);
                    notificationsBtn.setVisibility(View.INVISIBLE);
                    settingsBtn.setVisibility(View.INVISIBLE);
                }
            }
        });
        setTitle("TD TapSecure");

        //NOTE: back button page routing is declared in manifest as the parent
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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


    public void onNotificationsClick(View view) {
        startActivity(new Intent(this, NotificationsActivity.class));
    }
    public void onSettingsClick(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
