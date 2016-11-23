package com.example.alexandryan.tapsecure;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class TapSecureMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_secure_main);
        setTitle("TD TapSecure");
        //NOTE: back button page routing is declared in manifest as the parent
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

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
}
