package com.moutamid.placesbeen.fragments.save;

import static com.moutamid.placesbeen.utils.Utils.toast;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dezlum.codelabs.getjson.GetJson;
import com.fxn.stash.Stash;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.models.MarkerModel;
import com.moutamid.placesbeen.models.PolygonModel;
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
        /*new Thread(new Runnable() {
            @Override
            public void run() {*/
        Constants.databaseReference()
                .child(Constants.auth().getUid()).child(Constants.SAVED_ITEMS_PATH)
                .addChildEventListener(itemsChildValueListener(
                        Constants.SAVED_ITEMS_PATH,
                        saveFragment.savedArrayList,
                        R.drawable.save_marker,
                        Color.argb(255, 55, 0, 179),
                        "Saved"));
        Constants.databaseReference()
                .child(Constants.auth().getUid()).child(Constants.BEEN_ITEMS_PATH)
                .addChildEventListener(itemsChildValueListener(
                        Constants.BEEN_ITEMS_PATH,
                        saveFragment.beenArrayList,
                        R.drawable.save_marker,
                        Color.argb(255, 50, 205, 50),
                        "Been"));
        Constants.databaseReference()
                .child(Constants.auth().getUid()).child(Constants.WANT_TO_ITEMS_PATH)
                .addChildEventListener(itemsChildValueListener(
                        Constants.WANT_TO_ITEMS_PATH,
                        saveFragment.wantToArrayList,
                        R.drawable.save_marker,
                        Color.argb(255, 246, 173, 33),
                        "Want to"));

    }

    private ChildEventListener itemsChildValueListener(String ITEMS_PATH, ArrayList<MainItemModel> itemArrayList, int marker, int colour, String title) {
        ArrayList<String> savedList = Stash.getArrayList(Constants.SAVED_LIST, String.class);
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists() && saveFragment.isAdded()) {
                    new Thread(() -> {
                        MainItemModel model = snapshot.getValue(MainItemModel.class);
                        itemArrayList.add(model);

                        addMarkerOnMaps(model, marker, title);

                        drawPolygon(model.title, colour);

                        if (saveFragment.isAdded())
                            saveFragment.requireActivity().runOnUiThread(() -> {
                                saveFragment.mMap.setOnMapClickListener(latLng -> {
                                    triggerOnClick(latLng);
                                });
                            });

                        if (ITEMS_PATH.equals(Constants.SAVED_ITEMS_PATH) && !savedList.contains(model.title)) {
                            savedList.add(model.title);
                            Stash.put(Constants.SAVED_LIST, savedList);
                        }
                    }).start();
                }
            }

            private void addMarkerOnMaps(MainItemModel model, int marker, String title) {
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
                if (saveFragment.isAdded())
                    saveFragment.requireActivity().runOnUiThread(() -> {
                        Marker marker1 = saveFragment.mMap.addMarker(new MarkerOptions().position(sydney)
                                .title(title)
                                .icon(BitmapDescriptorFactory.fromResource(marker)));

                        MarkerModel markerModel = new MarkerModel(model.title, marker1);
                        markers.add(markerModel);
                    });
            }

            ArrayList<MarkerModel> markers = new ArrayList<>();

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    MainItemModel model = snapshot.getValue(MainItemModel.class);
                    itemArrayList.remove(model);

                    // REMOVING MARKER FROM MAP
                    for (int i = 0; i < markers.size(); i++) {
                        String title = markers.get(i).title;
                        Marker marker = markers.get(i).marker;

                        if (title.equals(model.title)) {
                            if (saveFragment.isAdded())
                                saveFragment.requireActivity().runOnUiThread(() -> {
                                    marker.remove();
                                });
                            markers.remove(i);
                            break;
                        }

                    }

                    // REMOVING POLYGON FROM MAP
                    for (int i = 0; i < polygonModelArrayList.size(); i++) {
                        String title = polygonModelArrayList.get(i).title;
                        Polygon polygon = polygonModelArrayList.get(i).polygon;

                        if (title.equals(model.title)) {
                            if (saveFragment.isAdded())
                                saveFragment.requireActivity().runOnUiThread(() -> {
                                    polygon.remove();
                                });
                            polygonModelArrayList.remove(i);
                            break;
                        }

                    }

                    // REMOVING ITEM FROM SAVED LIST
                    if (ITEMS_PATH.equals(Constants.SAVED_ITEMS_PATH) && savedList.contains(model.title)) {
                        savedList.remove(model.title);
                        Stash.put(Constants.SAVED_LIST, savedList);
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }
//        }).start();
//    }

    public void initMaps() {
        if (saveFragment.isAdded())
            saveFragment.requireActivity().runOnUiThread(() -> {
                saveFragment.mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        saveFragment.mMap = googleMap;

                        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle);
                        saveFragment.mMap.setMapStyle(style);

                    }
                });
            });
    }

    private void triggerOnClick(LatLng latLng) {
//        saveFragment.mMap.addMarker(new MarkerOptions().position(latLng)
//                .title(title)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
    }

    ArrayList<PolygonModel> polygonModelArrayList = new ArrayList<>();

    public void drawPolygon(String country, int colour) {
        Log.e(TAG, "drawPolygon: COUNTRY: " + country);

//        new Thread(() -> {
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

                if (saveFragment.isAdded())
                    saveFragment.requireActivity().runOnUiThread(() -> {
                        PolygonModel polygonModel = new PolygonModel();
                        polygonModel.title = country;
                        polygonModel.polygon = saveFragment.mMap.addPolygon(polygonOptions);
                        polygonModelArrayList.add(polygonModel);
                    });
            } else {
                for (int i1 = 0; i1 < innerArray.length(); i1++) {
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

                    if (saveFragment.isAdded())
                        saveFragment.requireActivity().runOnUiThread(() -> {
                            PolygonModel polygonModel = new PolygonModel();
                            polygonModel.title = country;
                            polygonModel.polygon = saveFragment.mMap.addPolygon(polygonOptions);
                            polygonModelArrayList.add(polygonModel);
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
//        }).start();

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
