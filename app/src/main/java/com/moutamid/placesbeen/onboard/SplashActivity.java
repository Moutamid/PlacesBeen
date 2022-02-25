package com.moutamid.placesbeen.onboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.moutamid.placesbeen.activities.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        startActivity(new Intent(SplashActivity.this, MainActivity.class));

//        if (Stash.getBoolean(Constants.IS_LOGGED_IN, false))
//            startActivity(new Intent(this, MainActivity.class));
//
//        else startActivity(new Intent(this, OnBoardingActivity.class));


    }
}
