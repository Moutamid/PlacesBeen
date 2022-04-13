package com.moutamid.placesbeen.activities.place;

import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.AUTOMATIC;
import static com.moutamid.placesbeen.R.color.lighterGrey;
import static com.moutamid.placesbeen.utils.Utils.toast;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.stash.Stash;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.databinding.ActivityPlaceItemBinding;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;
import com.moutamid.placesbeen.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class PlaceItemActivity extends AppCompatActivity {
    private static final String TAG = "HECK";
    public GoogleMap mMap;

    public ActivityPlaceItemBinding b;
    private PlaceController controller;
    public MainItemModel model;

    public String IMAGE_URL_1 = Constants.NULL;
    public String IMAGE_URL_2 = Constants.NULL;
    public String IMAGE_URL_3 = Constants.NULL;

    public String LAT = "0";
    public String LONG = "0";
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
        Log.d(TAG, "onCreate: ");
        controller = new PlaceController(this);

        model = (MainItemModel) Stash.getObject(Constants.CURRENT_MODEL_CLASS, MainItemModel.class);

        if (model.type.equals(Constants.PARAMS_NationalParks) || model.type.equals(Constants.PARAMS_CulturalSites)) {
            b.wantToCheckBoxPlace.setVisibility(View.GONE);
            b.saveBtnPlace.setVisibility(View.GONE);
        }

        b.titleTextViewPlace.setText(model.title);

        if (!model.desc.equals(Constants.NULL))
            b.descTextViewPlace.setText(model.desc);

        int nmbr = new Random().nextInt(2);
        nmbr += 4;
        b.ratingTextViewPlace.setText(nmbr + "");

        controller.checkIsItemSaved();

        controller.checkBeenWantTo();

        b.saveBtnPlace.setOnClickListener(view -> {
            controller.saveUnSaveItem();
        });

        b.backBtnPlace.setOnClickListener(view -> finish());

        controller.getImageUrl(model.title, model.desc);

        controller.getLatLng();

        b.beenCheckBoxPlace.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                // ADDING CITY NAME TO EXTRA LIST
                if (!model.desc.equals(Constants.NULL) && !model.desc.isEmpty()) {
                    ArrayList<String> extraCitiesList = Stash.getArrayList(model.desc + Constants.EXTRA_LIST, String.class);
                    extraCitiesList.add(model.title);
                    Stash.put(model.desc + Constants.EXTRA_LIST, extraCitiesList);
                }

                Utils.changeChartsValue(model, true);
                if (controller.polygon == null)
                    controller.drawPolygon(model.title, Color.argb(255, 50, 205, 50));
            } else {
                // REMOVING CITY NAME TO EXTRA LIST
                if (!model.desc.equals(Constants.NULL) && !model.desc.isEmpty()) {
                    ArrayList<String> extraCitiesList = Stash.getArrayList(model.desc + Constants.EXTRA_LIST, String.class);
                    extraCitiesList.remove(model.title);
                    Stash.put(model.desc + Constants.EXTRA_LIST, extraCitiesList);
                }

                Utils.changeChartsValue(model, false);
                if (controller.polygon != null) {
                    controller.polygon.remove();
                    controller.polygon = null;
                }
            }
            controller.triggerCheckBox(model, b, Constants.BEEN_ITEMS_PATH);
        });

        b.wantToCheckBoxPlace.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                // ADDING CITY NAME TO EXTRA LIST
                if (!model.desc.equals(Constants.NULL) && !model.desc.isEmpty()) {
                    ArrayList<String> extraCitiesList = Stash.getArrayList(model.desc + Constants.EXTRA_LIST_WANT, String.class);
                    extraCitiesList.add(model.title);
                    Stash.put(model.desc + Constants.EXTRA_LIST_WANT, extraCitiesList);
                }

                if (controller.polygon == null)
                    controller.drawPolygon(model.title, Color.argb(255, 246, 173, 33));
            } else {
                // REMOVING CITY NAME TO EXTRA LIST
                if (!model.desc.equals(Constants.NULL) && !model.desc.isEmpty()) {
                    ArrayList<String> extraCitiesList = Stash.getArrayList(model.desc + Constants.EXTRA_LIST_WANT, String.class);
                    extraCitiesList.remove(model.title);
                    Stash.put(model.desc + Constants.EXTRA_LIST_WANT, extraCitiesList);
                }

                if (controller.polygon != null) {
                    controller.polygon.remove();
                    controller.polygon = null;
                }
            }
            controller.triggerCheckBox(model, b, Constants.WANT_TO_ITEMS_PATH);
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
        Log.d(TAG, "loadImages: ");
        with(getApplicationContext())
                .asBitmap()
                .load(IMAGE_URL_1)
                .apply(new RequestOptions()
                        .placeholder(lighterGrey)
                        .error(lighterGrey)
                )
                .diskCacheStrategy(AUTOMATIC)
                .into(b.imageMainPlace);

        with(getApplicationContext())
                .asBitmap()
                .load(IMAGE_URL_1)
                .apply(new RequestOptions()
                        .placeholder(lighterGrey)
                        .error(lighterGrey)
                )
                .diskCacheStrategy(AUTOMATIC)
                .into(b.imageItem1Place);

        with(getApplicationContext())
                .asBitmap()
                .load(IMAGE_URL_2)
                .apply(new RequestOptions()
                        .placeholder(lighterGrey)
                        .error(lighterGrey)
                )
                .diskCacheStrategy(AUTOMATIC)
                .into(b.imageItem2Place);

        with(getApplicationContext())
                .asBitmap()
                .load(IMAGE_URL_3)
                .apply(new RequestOptions()
                        .placeholder(lighterGrey)
                        .error(lighterGrey)
                )
                .diskCacheStrategy(AUTOMATIC)
                .into(b.imageItem3Place);
    }

    public void loadAddress() {
        Log.d(TAG, "loadAddress: ");
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

    public void drawPolygon(String country, int colour) {
        Log.e("HUFF", "drawPolygon: COUNTRY: " + country);
        new Thread(() -> {
            RequestQueue queue = Volley.newRequestQueue(PlaceItemActivity.this);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    Constants.GET_BOUNDARY_URL((country)),
//                    Constants.GET_BOUNDARY_URL((country)),
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            try {
                                Log.d("HUFF", "drawPolygon: try {");
                                JSONObject jsonObject = jsonArray.getJSONObject(0);

                                JSONObject innerObject = jsonObject.getJSONObject("geojson");

                                String type = innerObject.getString("type");

                                JSONArray innerArray = innerObject.getJSONArray("coordinates");

                                if (type.equals("Polygon")) {
                                    Log.d("HUFF", "drawPolygon: ");
                                    JSONArray latlngArray = innerArray.getJSONArray(0);
                                    PolygonOptions polygonOptions = new PolygonOptions();
                                    polygonOptions.strokeColor(Color.WHITE);
//                                    polygonOptions.strokeWidth((float) 0.80);
                                    polygonOptions.strokeWidth(1);
//                polygonOptions.fillColor(Color.argb(255, 55, 0, 179));
                                    polygonOptions.fillColor(colour);

                                    for (int i = 0; i < latlngArray.length(); i++) {

                                        double lng = latlngArray.getJSONArray(i).getDouble(0);
                                        double lat = latlngArray.getJSONArray(i).getDouble(1);

                                        LatLng latLng = new LatLng(lat, lng);

                                        polygonOptions.add(latLng);
                                    }

                                    runOnUiThread(() -> {
                                        mMap.addPolygon(polygonOptions);

                                        Stash.put(Constants.POLYGON_OPTIONS + country + 0, polygonOptions);

                                    });
                                } else {
                                    Log.d("HUFF", "drawPolygon: looping");
                                    for (int i1 = 0; i1 < innerArray.length(); i1++) {
                                        Stash.put(Constants.POLYGON_OPTIONS_INDEX + country, i1);
                                        JSONArray array1 = innerArray.getJSONArray(i1);

                                        JSONArray array2 = array1.getJSONArray(0);

                                        PolygonOptions polygonOptions = new PolygonOptions();
                                        polygonOptions.strokeColor(Color.WHITE);
//                                        polygonOptions.strokeWidth((float) 0.80);
                                        polygonOptions.strokeWidth(1);
                                        polygonOptions.fillColor(colour);

                                        for (int i2 = 0; i2 < array2.length(); i2++) {

                                            JSONArray latlngArrray = array2.getJSONArray(i2);

                                            double lng = latlngArrray.getDouble(0);
                                            double lat = latlngArrray.getDouble(1);

                                            LatLng latLng = new LatLng(lat, lng);

                                            polygonOptions.add(latLng);

                                        }
                                        Stash.put(Constants.POLYGON_OPTIONS + country + i1, polygonOptions);
                                        runOnUiThread(() -> {
                                            mMap.addPolygon(polygonOptions);

                                        });
                                    }
                                }

                                Log.d("HUFF", "drawPolygon: jsonArray done");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("HUFF", "drawPolygon: ERROR: " + e.getMessage());
                            }/* catch (InterruptedException e) {
                                e.printStackTrace();
                                Log.e("HUFF", "drawPolygon: ERROR: " + e.getMessage());
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                                Log.e("HUFF", "drawPolygon: ERROR: " + e.getMessage());
                            }*/
                        }
                    }, error -> {
                toast("Failed to get data: " + error.getMessage());
                if (error instanceof NetworkError) {
                    Log.d("HUFF", "NetworkError");
                } else if (error instanceof ServerError) {
                    Log.d("HUFF", "ServerError");
                } else if (error instanceof AuthFailureError) {
                    Log.d("HUFF", "AuthFailureError");
                } else if (error instanceof ParseError) {
                    Log.d("HUFF", "ParseError");
                } else if (error instanceof NoConnectionError) {
                    Log.d("HUFF", "NoConnectionError");
                } else if (error instanceof TimeoutError) {
                    Log.d("HUFF", "TimeoutError");
                }
            });
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            int index = Stash.getInt(Constants.POLYGON_OPTIONS_INDEX + country, 0);

            // IF INDEX IS GREATER THAN 1 THEN IT'S A MULTI-POLYGON
            for (int i = 0; i <= index; i++) {
                Log.d("HUFF", "INDEX: " + i);
                PolygonOptions polygonOptions = (PolygonOptions) Stash.getObject(Constants.POLYGON_OPTIONS + country + i, PolygonOptions.class);
                if (polygonOptions == null) {
                    Log.d("HUFF", "POLYGON IS NULL" + i);
                    queue.add(jsonArrayRequest);
                } else {
                    Log.d("HUFF", "POLYGON EXIST" + i);
                    runOnUiThread(() -> {
                        polygonOptions.strokeWidth(2);
                        mMap.addPolygon(polygonOptions);
                    });
                }
            }
        }).start();
    }

}