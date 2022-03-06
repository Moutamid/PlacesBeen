package com.moutamid.placesbeen.activities.place;

import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.placesbeen.R.color.lighterGrey;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dezlum.codelabs.getjson.GetJson;
import com.fxn.stash.Stash;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;
import com.moutamid.placesbeen.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class PlaceController {
    private static final String TAG = "PlaceController";

    PlaceItemActivity activity;
    Context context;
    boolean IS_HIDDEN = false;

    public PlaceController(PlaceItemActivity placeItemActivity) {
        this.activity = placeItemActivity;
        this.context = placeItemActivity.getApplicationContext();
    }

    public void saveUnSaveItem() {
        MainItemModel model = activity.mainItemModel;
        if (model.title.equals("nullnull")) {
            Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<String> savedList = Stash.getArrayList(Constants.SAVED_LIST, String.class);

        if (savedList.contains(model.title)) {
//        if (Stash.getBoolean(model.title, false)) {
            // IF ALREADY SAVED THEN REMOVE
            activity.b.saveBtnPlace.setImageResource(R.drawable.ic_unsave_24);
            YoYo.with(Techniques.Bounce).duration(700).playOn(activity.b.saveBtnPlace);
            Constants.databaseReference()
                    .child(Constants.auth().getUid())
                    .child(Constants.SAVED_ITEMS_PATH)
                    .child(model.title)
                    .removeValue();

            savedList.remove(model.title);
            Stash.put(Constants.SAVED_LIST, savedList);
//            Stash.clear(model.title);
        } else {
            // IF NOT SAVED THEN SAVE
            activity.b.saveBtnPlace.setImageResource(R.drawable.ic_save_24);
            YoYo.with(Techniques.Bounce).duration(700).playOn(activity.b.saveBtnPlace);
            Constants.databaseReference()
                    .child(Constants.auth().getUid())
                    .child(Constants.SAVED_ITEMS_PATH)
                    .child(model.title)
                    .setValue(model);

//            Stash.put(model.title, true);
            savedList.add(model.title);
            Stash.put(Constants.SAVED_LIST, savedList);
        }
    }

    public void checkIsItemSaved() {
        ArrayList<String> savedList = Stash.getArrayList(Constants.SAVED_LIST, String.class);
//        if (Stash.getBoolean(activity.mainItemModel.title, false)) {
        if (savedList.contains(activity.mainItemModel.title)) {
            activity.b.saveBtnPlace.setImageResource(R.drawable.ic_save_24);
        }
    }

    public JSONObject downloadJSON(String title, String desc) {
        Log.d(TAG, "downloadJSON: ");
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(new GetJson().AsString(Constants.GET_PIXABAY_URL((title))));

            // IF ABOVE IS NULL
            if (jsonObject.getInt("totalHits") == 0)
                jsonObject = new JSONObject(new GetJson().AsString(Constants.GET_PIXABAY_URL(title + "+by+" + desc)));

            // IF ABOVE IS NULL
            if (jsonObject.getInt("totalHits") == 0)
                jsonObject = new JSONObject(new GetJson().AsString(Constants.GET_PIXABAY_URL(desc)));

        } catch (ExecutionException e) {
            Log.d(TAG, "downloadJSON: error: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.d(TAG, "downloadJSON: error: " + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d(TAG, "downloadJSON: error: " + e.getMessage());
            e.printStackTrace();
        }

        return jsonObject;
    }

    public void getImageUrl(String tt, String dd) {
        String link = "null";
        new Thread(() -> {
            try {
                String title = URLEncoder.encode(tt, "utf-8");
                String desc = URLEncoder.encode(dd, "utf-8");

                JSONObject jsonObject;

                jsonObject = downloadJSON(title, desc);

                JSONArray jsonArray = jsonObject.getJSONArray("hits");

                activity.IMAGE_URL_1 = jsonArray.getJSONObject(0).getString("webformatURL");
                activity.IMAGE_URL_2 = jsonArray.getJSONObject(1).getString("webformatURL");
                activity.IMAGE_URL_3 = jsonArray.getJSONObject(2).getString("webformatURL");

                activity.runOnUiThread(() -> activity.loadImages());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void getLatLng() {
        Log.d(TAG, "downloadJSON: ");
        new Thread(() -> {
            try {
                Log.d(TAG, "getLatLng: try {");
                String q = URLEncoder.encode(activity.mainItemModel.title, "utf-8");
                Log.d(TAG, "getLatLng: encoded");
                JSONObject jsonObject = new JSONObject(new GetJson().AsString(Constants.GET_POSITION_URL((q))));
                Log.d(TAG, "getLatLng: getted object as string");
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                Log.d(TAG, "getLatLng: array get");
                if (jsonArray.length() != 0) {
                    Log.d(TAG, "getLatLng: if statement");
                    JSONObject innerObject = jsonArray.getJSONObject(0);

                    activity.LAT = String.valueOf(innerObject.getDouble("latitude"));
                    activity.LONG = String.valueOf(innerObject.getDouble("longitude"));

                    activity.COUNTRY = innerObject.getString("country");

                    activity.CONTINENT = innerObject.getString("continent");

                    activity.runOnUiThread(() -> {
                        activity.loadAddress();
                    });

                } else {
                    Log.d(TAG, "getLatLng: else ");
                }

            } catch (ExecutionException | InterruptedException e) {
                Log.d(TAG, "downloadJSON: error: " + e.getMessage());
                e.printStackTrace();
            } catch (JSONException e) {
                Log.d(TAG, "JSONException: error: " + e.getMessage());
                Log.d(TAG, "JSONException: error: " + e.toString());
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                Log.d(TAG, "getLatLng: error: " + e.getMessage());
                e.printStackTrace();
            }

        }).start();
    }

    public void checkBeenWantTo() {
        // IF USER BEEN
        if (Stash.getBoolean(activity.mainItemModel.title + Constants.BEEN_ITEMS_PATH, false)) {
            activity.b.beenCheckBoxPlace.setChecked(true);
        }
        // IF WANT TO SAVED
        if (Stash.getBoolean(activity.mainItemModel.title + Constants.WANT_TO_ITEMS_PATH, false)) {
            activity.b.wantToCheckBoxPlace.setChecked(true);
        }

    }

    public void triggerCheckBox(MainItemModel mainItemModel, boolean b, String itemsPath) {
        Stash.put(mainItemModel.title + itemsPath, b);

        if (b) {
            Constants.databaseReference()
                    .child(Constants.auth().getUid())
                    .child(itemsPath)
                    .child(mainItemModel.title)
                    .setValue(mainItemModel);
        } else {
            Constants.databaseReference()
                    .child(Constants.auth().getUid())
                    .child(itemsPath)
                    .child(mainItemModel.title)
                    .removeValue();
        }
    }

    public void setImageOnMain(String URL) {
        with(activity.getApplicationContext())
                .asBitmap()
                .load(URL)
                .apply(new RequestOptions()
                        .placeholder(lighterGrey)
                        .error(lighterGrey)
                )
                .diskCacheStrategy(DATA)
                .into(activity.b.imageMainPlace);
    }

    public void initMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                activity.mMap = googleMap;

                double lat = Double.parseDouble(activity.LAT);
                double lng = Double.parseDouble(activity.LONG);

                LatLng sydney = new LatLng(lat, lng);
//                LatLng sydney = new LatLng(-34, 151);
                activity.mMap.addMarker(new MarkerOptions().position(sydney).title(activity.mainItemModel.title)
//                        .snippet("Population: 4,627,300")
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                );
                activity.mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                activity.mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        activity.mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        triggerOnClick();
                        return false;
                    }
                });

                activity.mMap.setOnMapClickListener(latLng -> {
                    activity.mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    triggerOnClick();
                });

                extractMarkers();

            }
        });
    }

    private void triggerOnClick() {

        if (IS_HIDDEN) {
            IS_HIDDEN = false;

            activity.mMap.animateCamera(CameraUpdateFactory.zoomTo(1.0f));

            activity.b.topLayoutForMaps.animate().alpha(1.0f).translationY(0).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    activity.b.bottomLayoutForMaps.animate().alpha(1.0f).translationY(0).start();
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    activity.b.topLayoutForMaps.setVisibility(View.VISIBLE);
                    activity.b.bottomLayoutForMaps.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            }).start();
        } else {
            IS_HIDDEN = true;

            activity.mMap.animateCamera(CameraUpdateFactory.zoomTo(4.0f));

            activity.b.topLayoutForMaps.animate().alpha(0.0f).translationY(-activity.b.topLayoutForMaps.getHeight()).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    activity.b.bottomLayoutForMaps.animate().alpha(0.0f).translationY(activity.b.bottomLayoutForMaps.getHeight()).start();
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    activity.b.topLayoutForMaps.setVisibility(View.GONE);
                    activity.b.bottomLayoutForMaps.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            }).start();
        }
    }

    public ArrayList<MainItemModel> CityArrayList = new ArrayList<>();

    public void extractMarkers() {

        new Thread(() -> {
            CityArrayList = Stash.getArrayList(Constants.PARAMS_City, MainItemModel.class);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addMarkers(CityArrayList);
                }
            });

        }).start();
    }

    boolean yes = true;

    private void addMarkers(ArrayList<MainItemModel> LIST) {
        /*for (int i = 0; i <= LIST.size() - 1; i += 2) {

            MainItemModel model = LIST.get(i);
            if (yes) {
                yes = false;
                double lat = Double.parseDouble(model.lat);
                double lng = Double.parseDouble(model.lng);

                LatLng sydney = new LatLng(lat, lng);

                activity.mMap.addMarker(new MarkerOptions().position(sydney).title(model.title)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_small)));
            } else {
                yes = true;
            }
        }*/

        activity.b.loadingView.setVisibility(View.GONE);
        activity.b.parentLayoutPlace.setVisibility(View.VISIBLE);
    }

}