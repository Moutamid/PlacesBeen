package com.moutamid.placesbeen.fragments.save;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dezlum.codelabs.getjson.GetJson;
import com.fxn.stash.Stash;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SaveController {

    private static final String TAG = "FUCKK";
    private SaveFragment saveFragment;
    private Context context;
    //    public String SELECTED_JSON = Constants.WORLD_CITIES_JSON;
    private View currentDot;
    private TextView currentTextView;

    public String ITEMS_PATH = Constants.SAVED_ITEMS_PATH;

    public SaveController(SaveFragment saveFragment) {
        this.saveFragment = saveFragment;
        this.context = saveFragment.requireContext();
        this.currentDot = saveFragment.b.dotSaved;
        this.currentTextView = saveFragment.b.textViewSaved;
    }

    public void retrieveDatabaseItems() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Constants.databaseReference()
                        .child(Constants.auth().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                saveFragment.savedArrayList.clear();
                                saveFragment.beenArrayList.clear();
                                saveFragment.wantToArrayList.clear();

                                if (snapshot.exists() && saveFragment.isAdded()) {

                                    if (snapshot.hasChild(Constants.SAVED_ITEMS_PATH)) {
                                        saveFragment.requireActivity().runOnUiThread(() -> {
                                            saveFragment.b.textViewSaved.setText("Saved (" + snapshot.child(Constants.SAVED_ITEMS_PATH).getChildrenCount() + ")");
                                        });
                                        for (DataSnapshot savedSnapShot : snapshot.child(Constants.SAVED_ITEMS_PATH).getChildren()) {
                                            ArrayList<String> savedList = Stash.getArrayList(Constants.SAVED_LIST, String.class);
                                            MainItemModel model = savedSnapShot.getValue(MainItemModel.class);
                                            if (!savedList.contains(model.title)) {
                                                savedList.add(model.title);
                                                Stash.put(Constants.SAVED_LIST, savedList);
                                            }
                                            saveFragment.savedArrayList.add(model);
                                        }
                                    } else {
                                        saveFragment.requireActivity().runOnUiThread(() -> {
                                            saveFragment.b.textViewSaved.setText("Saved");
                                        });
                                    }

                                    if (snapshot.hasChild(Constants.BEEN_ITEMS_PATH)) {
                                        saveFragment.requireActivity().runOnUiThread(() -> {
                                            saveFragment.b.textViewBeen.setText("Been (" + snapshot.child(Constants.BEEN_ITEMS_PATH).getChildrenCount() + ")");
                                        });
                                        for (DataSnapshot savedSnapShot : snapshot.child(Constants.BEEN_ITEMS_PATH).getChildren()) {
                                            MainItemModel model = savedSnapShot.getValue(MainItemModel.class);
                                            saveFragment.beenArrayList.add(model);
                                        }
                                    } else {
                                        saveFragment.requireActivity().runOnUiThread(() -> {
                                            saveFragment.b.textViewBeen.setText("Been");
                                        });
                                    }

                                    if (snapshot.hasChild(Constants.WANT_TO_ITEMS_PATH)) {
                                        saveFragment.requireActivity().runOnUiThread(() -> {
                                            saveFragment.b.textViewWantTo.setText("Want to (" + snapshot.child(Constants.WANT_TO_ITEMS_PATH).getChildrenCount() + ")");
                                        });
                                        for (DataSnapshot savedSnapShot : snapshot.child(Constants.WANT_TO_ITEMS_PATH).getChildren()) {
                                            MainItemModel model = savedSnapShot.getValue(MainItemModel.class);
                                            saveFragment.wantToArrayList.add(model);
                                        }
                                    } else {
                                        saveFragment.requireActivity().runOnUiThread(() -> {
                                            saveFragment.b.textViewWantTo.setText("Want to");
                                        });
                                    }

                                }

                                initMaps();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                if (saveFragment.isAdded())
                                    Toast.makeText(saveFragment.requireContext(), error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        }).start();
    }

    public void initMaps() {
        saveFragment.requireActivity().runOnUiThread(() -> {
            saveFragment.mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    saveFragment.mMap = googleMap;

                    MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle);
                    saveFragment.mMap.setMapStyle(style);

                    runLoop(saveFragment.savedArrayList, "Saved", R.drawable.save_marker);
                    runLoop(saveFragment.beenArrayList, "Been", R.drawable.been_marker);
                    runLoop(saveFragment.wantToArrayList, "Want to", R.drawable.want_to_marker);

                    saveFragment.mMap.setOnMapClickListener(latLng -> {
                        triggerOnClick(latLng);
                    });
                }

                private void runLoop(ArrayList<MainItemModel> LIST, String title, int markerDrawable) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (MainItemModel model : LIST) {
                                double lat;
                                double lng;

                                if (model.lat.equals(Constants.NULL)) {
                                    lat = getLat(model.title);
                                    lng = LONG;
                                } else {
                                    lat = Double.parseDouble(model.lat);
                                    lng = Double.parseDouble(model.lng);
                                }

                                LatLng sydney = new LatLng(lat, lng);
                                saveFragment.requireActivity().runOnUiThread(() -> {
                                    saveFragment.mMap.addMarker(new MarkerOptions().position(sydney)
                                            .title(title)
                                            .icon(BitmapDescriptorFactory.fromResource(markerDrawable)));
                                });
                                drawPolygon(model.title);
                            }
                        }
                    }).start();
                }
            });
        });
    }

    private void triggerOnClick(LatLng latLng) {
//        saveFragment.mMap.addMarker(new MarkerOptions().position(latLng)
//                .title(title)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
    }

    public void drawPolygon(String country) {
        Log.e(TAG, "drawPolygon: COUNTRY: " + country);
        new Thread(() -> {
            try {
                Log.d(TAG, "drawPolygon: try {");

                JSONArray jsonArray = new JSONArray(new GetJson().AsString(Constants.GET_BOUNDARY_URL((country))));

                JSONObject jsonObject = jsonArray.getJSONObject(0);

                JSONObject innerObject = jsonObject.getJSONObject("geojson");

                String type = innerObject.getString("type");

                JSONArray innerArray = innerObject.getJSONArray("coordinates");

                if (type.equals("Polygon")) {
                    JSONArray latlngArray = innerArray.getJSONArray(0);
                    PolygonOptions polygonOptions = new PolygonOptions();
                    polygonOptions.strokeColor(Color.BLACK);
                    polygonOptions.strokeWidth((float) 0.50);
                    polygonOptions.fillColor(Color.argb(255, 55, 0, 179));

                    for (int i = 0; i < latlngArray.length(); i++) {

//                        JSONArray coordinatesArray = latlngArray.getJSONArray(i);
//                    if (type.equals("Polygon")) {

                        Log.d(TAG, "drawPolygon: if (type.equals(\"Polygon\")) {");
                        double lng = latlngArray.getJSONArray(i).getDouble(0);
                        double lat = latlngArray.getJSONArray(i).getDouble(1);

                        LatLng latLng = new LatLng(lat, lng);

                        polygonOptions.add(latLng);
                    }

                    saveFragment.requireActivity().runOnUiThread(() -> {
                        saveFragment.mMap.addPolygon(polygonOptions);
                    });
                } else {
                    for (int i1 = 0; i1 < innerArray.length(); i1++) {
                        JSONArray array1 = innerArray.getJSONArray(i1);

                        JSONArray array2 = array1.getJSONArray(0);

                        PolygonOptions polygonOptions = new PolygonOptions();
                        polygonOptions.strokeColor(Color.BLACK);
                        polygonOptions.strokeWidth((float) 0.50);
                        polygonOptions.fillColor(Color.argb(255, 55, 0, 179));

                        for (int i2 = 0; i2 < array2.length(); i2++) {

                            JSONArray latlngArrray = array2.getJSONArray(i2);

                            double lng = latlngArrray.getDouble(0);
                            double lat = latlngArrray.getDouble(1);

                            LatLng latLng = new LatLng(lat, lng);

                            polygonOptions.add(latLng);

                        }

                        saveFragment.requireActivity().runOnUiThread(() -> {
                            saveFragment.mMap.addPolygon(polygonOptions);
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

    double LAT;
    double LONG;

    public double getLat(String query) {
        Log.d(TAG, "downloadJSON: ");
        try {
            Log.d(TAG, "getLatLng: try {");
            String q = URLEncoder.encode(query, "utf-8");
            Log.d(TAG, "getLatLng: encoded");
            JSONObject jsonObject = new JSONObject(new GetJson().AsString(Constants.GET_POSITION_URL((q))));
            Log.d(TAG, "getLatLng: getted object as string");
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            Log.d(TAG, "getLatLng: array get");
            if (jsonArray.length() != 0) {
                Log.d(TAG, "getLatLng: if statement");
                JSONObject innerObject = jsonArray.getJSONObject(0);

                LAT = innerObject.getDouble("latitude");
                LONG = innerObject.getDouble("longitude");

            } else {
                Log.d(TAG, "getLatLng: else ");
            }

        } catch (ExecutionException | InterruptedException e) {
            LAT = 0;
            LONG = 0;
            Log.d(TAG, "downloadJSON: error: " + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            LAT = 0;
            LONG = 0;
            Log.d(TAG, "JSONException: error: " + e.getMessage());
            Log.d(TAG, "JSONException: error: " + e.toString());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            LAT = 0;
            LONG = 0;
            Log.d(TAG, "getLatLng: error: " + e.getMessage());
            e.printStackTrace();
        }

        return LAT;
    }

    public void changeDotTo(View dot, TextView textView) {
        Log.d(TAG, "changeDotTo: ");
        currentDot.setVisibility(View.GONE);
        currentDot = dot;
        currentDot.setVisibility(View.VISIBLE);

        currentTextView.setTextColor(context.getResources().getColor(R.color.darkGrey));
        currentTextView = textView;
        currentTextView.setTextColor(context.getResources().getColor(R.color.yellow));
    }
}
