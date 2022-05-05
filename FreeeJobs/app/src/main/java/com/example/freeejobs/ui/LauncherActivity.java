package com.example.freeejobs.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.freeejobs.constant.Constants;
import com.example.freeejobs.ui.landingPage.LandingPage;
import com.example.freeejobs.ui.login.LoginActivity;

public class LauncherActivity extends AppCompatActivity {

    private String firstLaunch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i;

        SharedPreferences pref = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        firstLaunch = pref.getString("id", null);

        if (firstLaunch==null||firstLaunch.isEmpty()) {
            i = new Intent(LauncherActivity.this, LoginActivity.class);
            startActivity(i);
        } else {
            i = new Intent(LauncherActivity.this, LandingPage.class);
            startActivity(i);
        }

        finish();
    }
}