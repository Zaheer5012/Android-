package com.example.exceptionapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.exceptionapp.R;
import com.example.exceptionapp.service.ForegroundService;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(this, serviceIntent);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                    startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
                    finish();
            }
        }, 3000);
    }
}