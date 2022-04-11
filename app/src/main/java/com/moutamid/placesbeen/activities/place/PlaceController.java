package com.moutamid.placesbeen.activities.place;

import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.AUTOMATIC;
import static com.moutamid.placesbeen.R.color.lighterGrey;
import static com.moutamid.placesbeen.utils.Utils.encodeString;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dezlum.codelabs.getjson.GetJson;
import com.fxn.stash.Stash;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.JsonObject;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PlaceController {
    private static final String TAG = "HECK";

    PlaceItemActivity activity;
    Context context;
    boolean IS_HIDDEN = false;

    public PlaceController(PlaceItemActivity placeItemActivity) {
        this.activity = placeItemActivity;
        this.context = placeItemActivity.getApplicationContext();
    }

    public void saveUnSaveItem() {
        Log.d(TAG, "saveUnSaveItem: ");
        MainItemModel model = activity.model;
        if (model.title.equals("nullnull")) {
            Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<String> savedList = Stash.getArrayList(Constants.SAVED_LIST, String.class);
        if (savedList.contains(model.title)) {
            if (polygon != null) {
                polygon.remove();
                polygon = null;
            }
            // IF ALREADY SAVED THEN REMOVE
            activity.b.saveBtnPlace.setImageResource(R.drawable.ic_unsave_24);
            YoYo.with(Techniques.Bounce).duration(700).playOn(activity.b.saveBtnPlace);
            Constants.databaseReference()
                    .child(Constants.auth().getUid())
                    .child(Constants.SAVED_ITEMS_PATH)
                    .child(encodeString(model.title + model.desc))
                    .removeValue();

            savedList.remove(model.title);
            Stash.put(Constants.SAVED_LIST, savedList);
        } else {
            if (polygon == null)
                drawPolygon(model.title, Color.argb(255, 55, 0, 179));

            // IF NOT SAVED THEN SAVE
            activity.b.saveBtnPlace.setImageResource(R.drawable.ic_save_24);
            YoYo.with(Techniques.Bounce).duration(700).playOn(activity.b.saveBtnPlace);
            Constants.databaseReference()
                    .child(Constants.auth().getUid())
                    .child(Constants.SAVED_ITEMS_PATH)
                    .child(encodeString(model.title + model.desc))
                    .setValue(model);

            savedList.add(model.title);
            Stash.put(Constants.SAVED_LIST, savedList);
        }
    }

    public void checkIsItemSaved() {
        Log.d(TAG, "checkIsItemSaved: ");
        ArrayList<String> savedList = Stash.getArrayList(Constants.SAVED_LIST, String.class);
//        if (Stash.getBoolean(activity.mainItemModel.title, false)) {
        if (savedList.contains(activity.model.title)) {
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
        Log.d(TAG, "getImageUrl: ");
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

            } catch (Exception e) {
                Log.d(TAG, "getImageUrl: ERROR: " + e.getMessage());
                e.printStackTrace();
            }

            activity.runOnUiThread(() -> activity.loadImages());
        }).start();
    }

    public void getLatLng() {
        Log.d(TAG, "downloadJSON: ");
        new Thread(() -> {
            RequestQueue queue = Volley.newRequestQueue(activity);

            String q = "china";
            try {
                q = URLEncoder.encode(activity.model.title, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    Constants.GET_POSITION_URL(q),
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.d(TAG, "getLatLng: try {");
                            try {
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

                                } else {
                                    Log.d(TAG, "getLatLng: else ");
                                }

                            } catch (JSONException e) {
                                Log.d(TAG, "JSONException: error: " + e.getMessage());
                                Log.d(TAG, "JSONException: error: " + e.toString());
                                e.printStackTrace();
                            }

                            activity.runOnUiThread(() -> {
                                activity.loadAddress();
                            });
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    activity.LAT = "0";
                    activity.LONG = "0";

                    activity.runOnUiThread(() -> {
                        activity.loadAddress();
                    });
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsonObjectRequest);
        }).start();
    }

    public void checkBeenWantTo() {
        Log.d(TAG, "checkBeenWantTo: ");
        // IF USER BEEN
        if (Stash.getBoolean(activity.model.title + activity.model.desc + Constants.BEEN_ITEMS_PATH, false)) {
            activity.b.beenCheckBoxPlace.setChecked(true);
        }
        // IF WANT TO SAVED
        if (Stash.getBoolean(activity.model.title + Constants.WANT_TO_ITEMS_PATH, false)) {
            activity.b.wantToCheckBoxPlace.setChecked(true);
        }

    }

    public void triggerCheckBox(MainItemModel mainItemModel, boolean b, String itemsPath) {
        Log.d(TAG, "triggerCheckBox: ");
        Stash.put(mainItemModel.title + mainItemModel.desc + itemsPath, b);
        if (b) {
            Constants.databaseReference()
                    .child(Constants.auth().getUid())
                    .child(itemsPath)
                    .child(encodeString(mainItemModel.title + mainItemModel.desc))
                    .setValue(mainItemModel);
        } else {
            Constants.databaseReference()
                    .child(Constants.auth().getUid())
                    .child(itemsPath)
                    .child(encodeString(mainItemModel.title + mainItemModel.desc))
                    .removeValue();
        }
    }

    public void setImageOnMain(String URL) {
        Log.d(TAG, "setImageOnMain: ");
        with(activity.getApplicationContext())
                .asBitmap()
                .load(URL)
                .apply(new RequestOptions()
                        .placeholder(lighterGrey)
                        .error(lighterGrey)
                )
                .diskCacheStrategy(AUTOMATIC)
                .into(activity.b.imageMainPlace);
    }

    public void initMaps() {
        Log.d(TAG, "initMaps: ");
        if (activity.model.type.equals(Constants.PARAMS_NationalParks)) {
            activity.b.mapFragmentLayoutPlace.setVisibility(View.INVISIBLE);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    Log.d(TAG, "onMapReady: ");
                    activity.mMap = googleMap;

                    MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle);
                    activity.mMap.setMapStyle(style);

                    activity.runOnUiThread(() -> {
                        activity.drawPolygon(activity.model.title, Color.argb(255, 55, 0, 179));
                    });
                    double lat = Double.parseDouble(activity.LAT);
                    double lng = Double.parseDouble(activity.LONG);

                    LatLng sydney = new LatLng(lat, lng);
                    activity.mMap.addMarker(new MarkerOptions().position(sydney).title(activity.model.title));
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
    }

    public Polygon polygon;

    public void drawPolygon(String country, int colour) {
        Log.e(TAG, "drawPolygon: COUNTRY: " + country);
        new Thread(() -> {
            try {
                Log.d(TAG, "drawPolygon: try {");

                String polyGonStr = Stash.getString(Constants.POLYGON + country, Constants.NULL);

                if (polyGonStr.equals(Constants.NULL)) {
                    polyGonStr = new GetJson().AsString(Constants.GET_BOUNDARY_URL((country)));
                    Stash.put(Constants.POLYGON + country, polyGonStr);
                }

                JSONArray jsonArray = new JSONArray(polyGonStr);

                JSONObject jsonObject = jsonArray.getJSONObject(0);

                JSONObject innerObject = jsonObject.getJSONObject("geojson");

                String type = innerObject.getString("type");

                JSONArray innerArray = innerObject.getJSONArray("coordinates");

                if (type.equals("Polygon")) {
                    JSONArray latlngArray = innerArray.getJSONArray(0);
                    PolygonOptions polygonOptions = new PolygonOptions();
                    polygonOptions.strokeColor(Color.WHITE);
                    polygonOptions.strokeWidth((float) 0.80);
//                polygonOptions.fillColor(Color.argb(255, 55, 0, 179));
                    polygonOptions.fillColor(colour);

                    for (int i = 0; i < latlngArray.length(); i++) {

                        double lng = latlngArray.getJSONArray(i).getDouble(0);
                        double lat = latlngArray.getJSONArray(i).getDouble(1);

                        LatLng latLng = new LatLng(lat, lng);

                        polygonOptions.add(latLng);
                    }

                    activity.runOnUiThread(() -> {
                        polygon = activity.mMap.addPolygon(polygonOptions);
                    });
                } else {
                    for (int i1 = 0; i1 < innerArray.length(); i1++) {
                        Log.d(TAG, "drawPolygon: looping");
                        JSONArray array1 = innerArray.getJSONArray(i1);

                        JSONArray array2 = array1.getJSONArray(0);

                        PolygonOptions polygonOptions = new PolygonOptions();
                        polygonOptions.strokeColor(Color.WHITE);
                        polygonOptions.strokeWidth((float) 0.80);
                        polygonOptions.fillColor(colour);

                        for (int i2 = 0; i2 < array2.length(); i2++) {

                            JSONArray latlngArrray = array2.getJSONArray(i2);

                            double lng = latlngArrray.getDouble(0);
                            double lat = latlngArrray.getDouble(1);

                            LatLng latLng = new LatLng(lat, lng);

                            polygonOptions.add(latLng);

                        }
                        activity.runOnUiThread(() -> {
                            polygon = activity.mMap.addPolygon(polygonOptions);
                        });
                    }
                }

                Log.d(TAG, "drawPolygon: jsonArray done");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "drawPolygon: ERROR: " + e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e(TAG, "drawPolygon: ERROR: " + e.getMessage());
            } catch (ExecutionException e) {
                e.printStackTrace();
                Log.e(TAG, "drawPolygon: ERROR: " + e.getMessage());
            }
        }).start();

    }

    private void triggerOnClick() {
        Log.d(TAG, "triggerOnClick: ");
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
        Log.d(TAG, "extractMarkers: ");
        activity.b.loadingView.setVisibility(View.GONE);
        activity.b.parentLayoutPlace.setVisibility(View.VISIBLE);

        /*new Thread(() -> {
            CityArrayList = Stash.getArrayList(Constants.PARAMS_City, MainItemModel.class);*/

//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                addMarkers(CityArrayList);
//            }
//        });

//        }).start();
    }

    boolean yes = true;

    private void addMarkers(ArrayList<MainItemModel> LIST) {
        Log.d(TAG, "addMarkers: ");
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

        /*activity.b.loadingView.setVisibility(View.GONE);
        activity.b.parentLayoutPlace.setVisibility(View.VISIBLE);*/
    }

}