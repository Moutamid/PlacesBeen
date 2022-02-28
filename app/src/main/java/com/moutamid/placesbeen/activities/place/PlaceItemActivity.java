package com.moutamid.placesbeen.activities.place;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fxn.stash.Stash;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.databinding.ActivityPlaceItemBinding;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;

import java.util.Random;

public class PlaceItemActivity extends AppCompatActivity {

    public ActivityPlaceItemBinding b;
    private PlaceController controller;
    public MainItemModel mainItemModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_from_left, R.anim.hold);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        b = ActivityPlaceItemBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        controller = new PlaceController(this);

        mainItemModel = (MainItemModel) Stash.getObject(Constants.CURRENT_MODEL_CLASS, MainItemModel.class);

        b.titleTextViewPlace.setText(mainItemModel.title);

        if (!mainItemModel.desc.equals(Constants.NULL))
            b.descTextViewPlace.setText(mainItemModel.desc);

        int nmbr = new Random().nextInt(2);
        nmbr += 4;
        b.ratingTextViewPlace.setText(nmbr + "");

        controller.checkIsItemSaved();

        b.backBtnPlace.setOnClickListener(view -> finish());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                b.loadingView.setVisibility(View.GONE);
                b.parentLayoutPlace.setVisibility(View.VISIBLE);
            }
        }, 3000);

    }
}











