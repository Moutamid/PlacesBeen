package com.moutamid.placesbeen.activities.home;

import static com.bumptech.glide.Glide.with;
import static com.moutamid.placesbeen.R.color.lighterGrey;
import static com.moutamid.placesbeen.utils.Utils.toast;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dezlum.codelabs.getjson.GetJson;
import com.fxn.stash.Stash;
import com.google.android.gms.maps.model.Polygon;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.activities.saved.SavedListsActivity;
import com.moutamid.placesbeen.databinding.ActivityHomeBinding;
import com.moutamid.placesbeen.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

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
            openActivity(Constants.PARAMS_CulturalSites);
        });

        b.airportsListBtn.setOnClickListener(view -> {
            openActivity(Constants.PARAMS_Airports);
        });

        b.worldMapListBtn.setOnClickListener(view -> {
            openActivity(Constants.PARAMS_WORLD_MAP);
        });

        b.seeShowSavedBtn.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, SavedListsActivity.class));
        });

        b.viewProfileBtn.setOnClickListener(view -> {
            openActivity(Constants.PARAMS_PROFILE);
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    setvaluesOnTextviews();
                });
            }
        }, 1000, 1000);

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

                            with(getApplicationContext())
                                    .load(snapshot.getValue().toString())
                                    .apply(new RequestOptions()
                                            .placeholder(R.color.grey)
                                            .error(R.color.grey)
                                    )
                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                    .into(b.profileImg);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void setvaluesOnTextviews() {
        if (b == null)
            return;
//        b.continentsTag.setText(Stash.getInt(Constants.PARAMS_Continent + Constants.FOR_CHARTS) + " Continents");
        b.countriesTag.setText(Stash.getInt(Constants.PARAMS_Country + Constants.FOR_CHARTS) + " Countries");
//        b.statesTag.setText(Stash.getInt(Constants.PARAMS_States + Constants.FOR_CHARTS) + " States");
        b.citiesTag.setText(Stash.getInt(Constants.PARAMS_City + Constants.FOR_CHARTS) + " Cities");
        b.culturalSitesTag.setText(Stash.getInt(Constants.PARAMS_CulturalSites + Constants.FOR_CHARTS) + " Cultural Sites");
//        b.nationalParksTag.setText(Stash.getInt(Constants.PARAMS_NationalParks + Constants.FOR_CHARTS) + " National Parks");
        b.airportsTag.setText(Stash.getInt(Constants.PARAMS_Airports + Constants.FOR_CHARTS) + " Airports");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setvaluesOnTextviews();
    }
}