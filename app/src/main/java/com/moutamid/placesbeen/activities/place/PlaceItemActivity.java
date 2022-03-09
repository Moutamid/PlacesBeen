package com.moutamid.placesbeen.activities.place;

import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.placesbeen.R.color.lighterGrey;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.fxn.stash.Stash;
import com.google.android.gms.maps.GoogleMap;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.databinding.ActivityPlaceItemBinding;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;

import java.util.Random;

public class PlaceItemActivity extends AppCompatActivity {

    public GoogleMap mMap;

    public ActivityPlaceItemBinding b;
    private PlaceController controller;
    public MainItemModel mainItemModel;

    public String IMAGE_URL_1 = Constants.NULL;
    public String IMAGE_URL_2 = Constants.NULL;
    public String IMAGE_URL_3 = Constants.NULL;

    public String LAT = Constants.NULL;
    public String LONG = Constants.NULL;
    public String COUNTRY = Constants.NULL;
    public String CONTINENT = Constants.NULL;

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

        controller.checkBeenWantTo();

        b.saveBtnPlace.setOnClickListener(view -> {
            controller.saveUnSaveItem();
        });

        b.backBtnPlace.setOnClickListener(view -> finish());

        controller.getImageUrl(mainItemModel.title, mainItemModel.desc);

        controller.getLatLng();

        b.beenCheckBoxPlace.setOnCheckedChangeListener((compoundButton, b) -> {
            controller.triggerCheckBox(mainItemModel, b, Constants.BEEN_ITEMS_PATH);
        });

        b.wantToCheckBoxPlace.setOnCheckedChangeListener((compoundButton, b) -> {
            controller.triggerCheckBox(mainItemModel, b, Constants.WANT_TO_ITEMS_PATH);
        });

        b.imageItem1Place.setOnClickListener(view -> {
            controller.setImageOnMain(IMAGE_URL_1);
        });
        b.imageItem2Place.setOnClickListener(view -> {
            controller.setImageOnMain(IMAGE_URL_2);
        });
        b.imageItem3Place.setOnClickListener(view -> {
            controller.setImageOnMain(IMAGE_URL_3);
        });

    }

    public void loadImages() {
        with(getApplicationContext())
                .asBitmap()
                .load(IMAGE_URL_1)
                .apply(new RequestOptions()
                        .placeholder(lighterGrey)
                        .error(lighterGrey)
                )
                .diskCacheStrategy(DATA)
                .into(b.imageMainPlace);

        with(PlaceItemActivity.this)
                .asBitmap()
                .load(IMAGE_URL_1)
                .apply(new RequestOptions()
                        .placeholder(lighterGrey)
                        .error(lighterGrey)
                )
                .diskCacheStrategy(DATA)
                .into(b.imageItem1Place);

        with(PlaceItemActivity.this)
                .asBitmap()
                .load(IMAGE_URL_2)
                .apply(new RequestOptions()
                        .placeholder(lighterGrey)
                        .error(lighterGrey)
                )
                .diskCacheStrategy(DATA)
                .into(b.imageItem2Place);

        with(PlaceItemActivity.this)
                .asBitmap()
                .load(IMAGE_URL_3)
                .apply(new RequestOptions()
                        .placeholder(lighterGrey)
                        .error(lighterGrey)
                )
                .diskCacheStrategy(DATA)
                .into(b.imageItem3Place);
    }

    public void loadAddress() {
        if (COUNTRY.equals(Constants.NULL) || COUNTRY == null) {

            if (CONTINENT.equals(Constants.NULL) || CONTINENT == null) {
                b.otherTextView1Place.setText("World");
            } else b.otherTextView1Place.setText(CONTINENT);

        } else {
            b.otherTextView1Place.setText(COUNTRY);
            b.otherTextView2Place.setText(CONTINENT);
        }

        controller.initMaps();

    }

}