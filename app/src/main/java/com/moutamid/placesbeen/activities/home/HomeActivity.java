package com.moutamid.placesbeen.activities.home;

import static com.moutamid.placesbeen.utils.Utils.toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.databinding.ActivityHomeBinding;
import com.moutamid.placesbeen.utils.Constants;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityHomeBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(b.getRoot());

        getImageProfileUrl();

        b.continentsListBtn.setOnClickListener(view -> {
            openActivity(Constants.PARAMS_Continent);
        });

        b.culturalSitesListBtn.setOnClickListener(view -> {
            toast("Under process");
            openActivity(Constants.PARAMS_CulturalSites);
        });

        b.airportsListBtn.setOnClickListener(view -> {
            toast("Under process");
            openActivity(Constants.PARAMS_Airports);
        });

        b.worldMapListBtn.setOnClickListener(view -> {
            openActivity(Constants.PARAMS_WORLD_MAP);
        });

        b.seeChartsBtn.setOnClickListener(view -> {
            openActivity(Constants.PARAMS_CHARTS);
        });

        b.viewProfileBtn.setOnClickListener(view -> {
            openActivity(Constants.PARAMS_PROFILE);
        });

    }

    private void openActivity(String params) {
        startActivity(new Intent(HomeActivity.this, MainActivity.class)
        .putExtra(Constants.PARAMS, params));
    }

    public void getImageProfileUrl() {
        Constants.databaseReference()
                .child(Constants.auth().getUid())
                .child(Constants.PROFILE_URL)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Stash.put(Constants.PROFILE_URL, snapshot.getValue().toString());

                            Glide.with(getApplicationContext())
                                    .load(snapshot.getValue().toString())
                                    .apply(new RequestOptions()
                                            .placeholder(R.color.grey)
                                            .error(R.color.grey)
                                    )
                                    .into(b.profileImg);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void setvaluesOnTextviews() {
        b.continentsTag.setText(Stash.getInt(Constants.PARAMS_Continent + Constants.FOR_CHARTS) + " Continents");
        b.countriesTag.setText(Stash.getInt(Constants.PARAMS_Country + Constants.FOR_CHARTS) + " Countries");
        b.statesTag.setText(Stash.getInt(Constants.PARAMS_States + Constants.FOR_CHARTS) + " States");
        b.citiesTag.setText(Stash.getInt(Constants.PARAMS_City + Constants.FOR_CHARTS) + " Cities");
        b.culturalSitesTag.setText(Stash.getInt(Constants.PARAMS_CulturalSites + Constants.FOR_CHARTS) + " Cultural Sites");
        b.nationalParksTag.setText(Stash.getInt(Constants.PARAMS_NationalParks + Constants.FOR_CHARTS) + " National Parks");
        b.airportsTag.setText(Stash.getInt(Constants.PARAMS_Airports + Constants.FOR_CHARTS) + " Airports");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setvaluesOnTextviews();
    }
}