package com.moutamid.placesbeen.onboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;
import com.moutamid.placesbeen.activities.home.HomeActivity;
import com.moutamid.placesbeen.activities.home.MainActivity;
import com.moutamid.placesbeen.activities.place.PlaceItemActivity;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    public ArrayList<MainItemModel> ContinentArrayList = new ArrayList<>();
    public ArrayList<MainItemModel> CountryArrayList = new ArrayList<>();
    public ArrayList<MainItemModel> StatesArrayList = new ArrayList<>();
    public ArrayList<MainItemModel> CityArrayList = new ArrayList<>();
    public ArrayList<MainItemModel> CulturalSitesArrayList = new ArrayList<>();
    public ArrayList<MainItemModel> NationalParksArrayList = new ArrayList<>();
    public ArrayList<MainItemModel> AirportsArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        if (Stash.getBoolean(Constants.IS_FIRST_TIME, true)) {
            progressDialog.show();

            SplashController controller = new SplashController(this);

            controller.getContinents();
        } else {
            openActivity();
        }

    }

    private ProgressDialog progressDialog;

    public void openActivity() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                if (Stash.getBoolean(Constants.IS_LOGGED_IN, false))
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));

                else startActivity(new Intent(SplashActivity.this, OnBoardingActivity.class));
            }
        });

    }

    boolean tt = false;

    @Override
    public void onBackPressed() {
        if (tt)
            super.onBackPressed();
        else {

        }
    }
}
