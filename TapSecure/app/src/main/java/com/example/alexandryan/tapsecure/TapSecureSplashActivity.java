package com.example.alexandryan.tapsecure;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TapSecureSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_secure_splash);
        setTitle("TD TapSecure");
    }

    public void OnGetStartedClick(View view) {
        Intent intent = new Intent(this, TapSecureMainActivity.class);
        startActivity(intent);
    }
}
