package com.moutamid.placesbeen.activities.maps;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.moutamid.placesbeen.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity {

    private ActivityMapsBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

//        YoYo.with(Techniques.Hinge).duration(2000).delay(2000).playOn(b.logoMain);

    }

}