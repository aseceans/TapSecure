package com.example.alexandryan.tapsecure;

import android.content.Context;
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

    Switch chequingSwitch;
    Switch visaSwitch;
    Button settingsBtn;
    Boolean visaSwitchOn;
    Boolean chequingSwitchOn;
    Boolean firstTimeTapSecure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_secure_main);

        //initialize variables
        chequingSwitch = (Switch) findViewById(R.id.enableChequingIF);
        visaSwitch = (Switch) findViewById(R.id.enableVisaIF);

        //check if first time
        SharedPreferences settings = getSharedPreferences("counterState", Context.MODE_PRIVATE); //state is the preference file
        firstTimeTapSecure = settings.getBoolean("firstTimeTapSecure", true);

        visaSwitchOn = false;
        chequingSwitchOn = false;

        settingsBtn = (Button) findViewById(R.id.settingsBtn);

        //set to invisible on page load:
        settingsBtn.setVisibility(View.INVISIBLE);

        chequingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    chequingSwitchOn = true;
                else
                    chequingSwitchOn = false;

                if(chequingSwitchOn || visaSwitchOn){ //on
                    settingsBtn.setVisibility(View.VISIBLE);
                } else { //off
                    settingsBtn.setVisibility(View.INVISIBLE);
                }
            }
        });

        visaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    visaSwitchOn = true;
                else
                    visaSwitchOn = false;

                if(chequingSwitchOn || visaSwitchOn){ //on
                    settingsBtn.setVisibility(View.VISIBLE);
                } else { //off
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
        SharedPreferences settings = getSharedPreferences("counterState", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("firstTimeTapSecure",firstTimeTapSecure);
        editor.commit();
    }
}
