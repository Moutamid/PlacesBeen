package com.moutamid.placesbeen.activities.home;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

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

public class MainController {
    private static final String TAG = "MainController";

    Context context;
    MainActivity saveFragment;

    public ArrayList<MarkerModel> markers = new ArrayList<>();
    public ArrayList<PolygonModel> polygonModelArrayList = new ArrayList<>();

    public MainController(MainActivity saveFragment) {
        this.context = saveFragment;
        this.saveFragment = saveFragment;
    }

    public static void fetchAllPolygonBoundaries() {
        Log.d(TAG, "fetchAllPolygonBoundaries: ");
        new Thread(() -> {
            try {

                ArrayList<MainItemModel> CountryArrayList = Stash.getArrayList(Constants.PARAMS_Country, MainItemModel.class);

                for (int i = 0; i < CountryArrayList.size(); i++) {
                    String country = CountryArrayList.get(i).title;
                    Log.d(TAG, "fetchAllPolygonBoundaries: COUNTRY: " + country);

                    String polyGonStr = Stash.getString(Constants.POLYGON + country, Constants.NULL);

                    if (polyGonStr.equals(Constants.NULL)) {
                        Log.d(TAG, "fetchAllPolygonBoundaries: downloaded");
                        polyGonStr = new GetJson().AsString(Constants.GET_BOUNDARY_URL((country)));
                        Stash.put(Constants.POLYGON + country, polyGonStr);
                    }
                }

                /*ArrayList<MainItemModel> CitiesArrayList = Stash.getArrayList(Constants.PARAMS_City, MainItemModel.class);

                for (int i = 0; i < CitiesArrayList.size(); i++) {
                    String country = CitiesArrayList.get(i).title;
                    Log.d(TAG, "fetchAllPolygonBoundaries: CITY: " + country);

                    String polyGonStr = Stash.getString(Constants.POLYGON + country, Constants.NULL);

                    if (polyGonStr.equals(Constants.NULL)) {
                        Log.d(TAG, "fetchAllPolygonBoundaries: downloaded");
                        polyGonStr = new GetJson().AsString(Constants.GET_BOUNDARY_URL_FOR_CITY((country)));
                        Stash.put(Constants.POLYGON + country, polyGonStr);
                    }
                }
*/
                Log.d(TAG, "fetchAllPolygonBoundaries: done");
            } catch (InterruptedException e) {
                Log.d(TAG, "fetchAllPolygonBoundaries: error");
                e.printStackTrace();
                Log.e(TAG, "drawPolygon: ERROR: " + e.getMessage());
            } catch (ExecutionException e) {
                Log.d(TAG, "fetchAllPolygonBoundaries: error");
                e.printStackTrace();
                Log.e(TAG, "drawPolygon: ERROR: " + e.getMessage());
            }
        }).start();
    }

    // FETCH ALL LAT LONGS OF CITIES
    public void fetchAllLatLngsOfCities() {
        Log.d(TAG, "fetchAllLatLngsOfCities: ");

        ArrayList<MainItemModel> CityArrayList = Stash.getArrayList(Constants.PARAMS_City, MainItemModel.class);

        new Thread(() -> {
            for (MainItemModel model : CityArrayList) {
                if (model.lat == null || model.lat.equals(Constants.NULL) || model.lat.equals("") || TextUtils.isEmpty(model.lat)) {
                    try {
                        Log.d(TAG, "fetchAllLatLngsOfCities: try {");
                        String q = URLEncoder.encode(model.title, "utf-8");
                        Log.d(TAG, "fetchAllLatLngsOfCities: encoded");
                        JSONObject jsonObject = new JSONObject(new GetJson().AsString(Constants.GET_POSITION_URL((q))));
                        Log.d(TAG, "fetchAllLatLngsOfCities: getted object as string");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        Log.d(TAG, "fetchAllLatLngsOfCities: array get");
                        if (jsonArray.length() != 0) {
                            Log.d(TAG, "fetchAllLatLngsOfCities: if statement");
                            JSONObject innerObject = jsonArray.getJSONObject(0);

                            model.lat = String.valueOf(innerObject.getDouble("latitude"));
                            model.lng = String.valueOf(innerObject.getDouble("longitude"));

                            Stash.put(Constants.PARAMS_City, CityArrayList);

                            Stash.put(model.title + Constants.CURRENT_QUERY_LAT, model.lat);
                            Stash.put(model.title + Constants.CURRENT_QUERY_LONG, model.lng);

                        } else {
                            Log.d(TAG, "fetchAllLatLngsOfCities: else ");
                        }

                    } catch (ExecutionException | InterruptedException e) {
                        Log.d(TAG, "fetchAllLatLngsOfCities: error: " + e.getMessage());
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Log.d(TAG, "fetchAllLatLngsOfCities JSONException: error: " + e.getMessage());
                        Log.d(TAG, "fetchAllLatLngsOfCities JSONException: error: " + e.toString());
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        Log.d(TAG, "fetchAllLatLngsOfCities: error: " + e.getMessage());
                        e.printStackTrace();
                    }

                }

            }

        }).start();
    }

//    SAVE CONTROLLER DATA FOR MAPS INTEGRATION

    public void retrieveDatabaseItems() {
        Log.d(TAG, "retrieveDatabaseItems: ");
        /*new Thread(new Runnable() {
            @Override
            public void run() {*/
        Constants.databaseReference()
                .child(Constants.auth().getUid()).child(Constants.SAVED_ITEMS_PATH)
                .addChildEventListener(itemsChildValueListener(
                        Constants.SAVED_ITEMS_PATH,
                        saveFragment.savedArrayListCountries,
                        R.drawable.save_marker,
                        Color.argb(255, 55, 0, 179),
                        "Saved"));
        Constants.databaseReference()
                .child(Constants.auth().getUid()).child(Constants.BEEN_ITEMS_PATH)
                .addChildEventListener(itemsChildValueListener(
                        Constants.BEEN_ITEMS_PATH,
                        saveFragment.beenArrayListCountries,
                        R.drawable.save_marker,
                        Color.argb(255, 50, 205, 50),
                        "Been"));
        Constants.databaseReference()
                .child(Constants.auth().getUid()).child(Constants.WANT_TO_ITEMS_PATH)
                .addChildEventListener(itemsChildValueListener(
                        Constants.WANT_TO_ITEMS_PATH,
                        saveFragment.wantToArrayListCountries,
                        R.drawable.save_marker,
                        Color.argb(255, 246, 173, 33),
                        "Want to"));

    }

    private ChildEventListener itemsChildValueListener(String ITEMS_PATH, ArrayList<MainItemModel> itemArrayList, int marker, int colour, String title) {
        ArrayList<String> savedList = Stash.getArrayList(Constants.SAVED_LIST, String.class);
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildAdded: ");
                if (snapshot.exists()) {// && saveFragment.isAdded()
//                    new Thread(() -> {
                    MainItemModel model = snapshot.getValue(MainItemModel.class);
                    itemArrayList.add(model);

                    addMarkerOnMaps(model, marker, title);

                    new DrawPolygonTask(model.title, colour, model).execute();

//                        if (saveFragment.isAdded())
                    saveFragment.runOnUiThread(() -> {
                        saveFragment.mMap.setOnMapClickListener(latLng -> {
                            triggerOnClick(latLng);
                        });
                    });

                    if (ITEMS_PATH.equals(Constants.SAVED_ITEMS_PATH) && !savedList.contains(model.title)) {
                        savedList.add(model.title);
                        Stash.put(Constants.SAVED_LIST, savedList);
                    }
//                    }).start();
                }
            }

            private void addMarkerOnMaps(MainItemModel model, int marker, String title) {
                new Thread(() -> {
                    Log.d(TAG, "addMarkerOnMaps: ");
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
//                if (saveFragment.isAdded())
                    saveFragment.runOnUiThread(() -> {
                        boolean descNull = false;
                        if (model.desc == null || model.desc.equals(Constants.NULL) || model.desc.equals("") || TextUtils.isEmpty(model.desc)) {
                            descNull = true;
                        }

                        Marker marker1 = saveFragment.mMap.addMarker(new MarkerOptions().position(sydney)
                                .title(title)
                                .icon(BitmapDescriptorFactory.fromResource(marker)));

                        MarkerModel markerModel = new MarkerModel(model.title, marker1, descNull);
                        markers.add(markerModel);
                        marker1.setVisible(descNull);
                        Log.d(TAG, "addMarkerOnMaps: " + model.title + " Desc NULL: " + descNull);
                    });

                }).start();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onChildRemoved: ");
                if (snapshot.exists()) {
                    MainItemModel model = snapshot.getValue(MainItemModel.class);
                    itemArrayList.remove(model);

                    // REMOVING MARKER FROM MAP
                    for (int i = 0; i < markers.size(); i++) {
                        Log.d(TAG, "onChildRemoved: marker iteration: " + i);
                        String title = markers.get(i).title;
                        Marker marker = markers.get(i).marker;

                        if (title.equals(model.title)) {
//                            if (saveFragment.isAdded())
                            saveFragment.runOnUiThread(() -> {
                                marker.remove();
                            });
                            markers.remove(i);
                            break;
                        }

                    }

                    // REMOVING POLYGON FROM MAP
                    for (int i = 0; i < polygonModelArrayList.size(); i++) {
                        Log.d(TAG, "onChildRemoved: polygon iteration: " + i);
                        String title = polygonModelArrayList.get(i).title;
                        Polygon polygon = polygonModelArrayList.get(i).polygon;

                        if (title.equals(model.title)) {
//                            if (saveFragment.isAdded())
                            saveFragment.runOnUiThread(() -> {
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
        Log.d(TAG, "initMaps: ");
        saveFragment.runOnUiThread(() -> {
            saveFragment.mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    Log.d(TAG, "onMapReady: ");
                    saveFragment.mMap = googleMap;

                    MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle);
                    saveFragment.mMap.setMapStyle(style);

                }
            });
        });
    }

    private void triggerOnClick(LatLng latLng) {
        Log.d(TAG, "triggerOnClick: " + latLng.toString());
//        saveFragment.mMap.addMarker(new MarkerOptions().position(latLng)
//                .title(title)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
    }

    public class DrawPolygonTask extends AsyncTask<Void, Void, Void> {
        String country;
        int colour;
        MainItemModel model;

        public DrawPolygonTask(String country, int colour, MainItemModel model) {
            this.country = country;
            this.colour = colour;
            this.model = model;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

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
                        Log.d(TAG, "drawPolygon: iteration polygon: " + i);

                        double lng = latlngArray.getJSONArray(i).getDouble(0);
                        double lat = latlngArray.getJSONArray(i).getDouble(1);

                        LatLng latLng = new LatLng(lat, lng);

                        polygonOptions.add(latLng);
                    }

//                if (saveFragment.isAdded())
                    saveFragment.runOnUiThread(() -> {
                        boolean descNull = false;
                        if (model.desc == null || model.desc.equals(Constants.NULL) || model.desc.equals("") || TextUtils.isEmpty(model.desc)) {
                            descNull = true;
                        }
                        Log.d(TAG, "drawPolygon: title: " + model.title + " descIsNull: " + descNull);
                        PolygonModel polygonModel = new PolygonModel();
                        polygonModel.title = country;
                        polygonModel.polygon = saveFragment.mMap.addPolygon(polygonOptions);

                        polygonModel.polygon.setVisible(descNull);

                        polygonModel.descNull = descNull;

                        polygonModelArrayList.add(polygonModel);
                    });
                } else {
                    for (int i1 = 0; i1 < innerArray.length(); i1++) {
                        Log.d(TAG, "drawPolygon: iteration multipolygon: " + i1);
                        JSONArray array1 = innerArray.getJSONArray(i1);

                        JSONArray array2 = array1.getJSONArray(0);

                        PolygonOptions polygonOptions = new PolygonOptions();
                        polygonOptions.strokeColor(Color.WHITE);
                        polygonOptions.strokeWidth((float) 0.80);
                        polygonOptions.fillColor(colour);

                        for (int i2 = 0; i2 < array2.length(); i2++) {
                            Log.d(TAG, "drawPolygon: iteration2 multipolygon: " + i2);

                            JSONArray latlngArrray = array2.getJSONArray(i2);

                            double lng = latlngArrray.getDouble(0);
                            double lat = latlngArrray.getDouble(1);

                            LatLng latLng = new LatLng(lat, lng);

                            polygonOptions.add(latLng);

                        }

//                    if (saveFragment.isAdded())
                        saveFragment.runOnUiThread(() -> {
                            Log.d(TAG, "drawPolygon: added");
                            PolygonModel polygonModel = new PolygonModel();
                            polygonModel.title = country;
                            polygonModel.polygon = saveFragment.mMap.addPolygon(polygonOptions);

                            boolean descNull = false;
                            if (model.desc == null || model.desc.equals(Constants.NULL) || model.desc.equals("") || TextUtils.isEmpty(model.desc)) {
                                descNull = true;
                            }
                            Log.d(TAG, "drawPolygon: title: " + model.title + " descIsNull: " + descNull);

                            polygonModel.descNull = descNull;

                            polygonModelArrayList.add(polygonModel);
                        });
                    }
                }

                Log.d(TAG, "drawPolygon: done");
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

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }

    public void drawPolygon(String country, int colour, MainItemModel model) {
        Log.e(TAG, "drawPolygon: COUNTRY: " + country);

        new Thread(() -> {

        }).start();

    }

    double LAT;
    double LONG;

    public double getLat(String query) {
        Log.d(TAG, "getLat: query");
        // SAVE AND RETRIEVE FROM PREFERENCES
        String lat = Stash.getString(query + Constants.CURRENT_QUERY_LAT, Constants.NULL);
        String lng = Stash.getString(query + Constants.CURRENT_QUERY_LONG, Constants.NULL);

        // IF STORED VALUE IS NULL
        if (lat.equals(Constants.NULL)) {
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

                    Stash.put(query + Constants.CURRENT_QUERY_LAT, LAT);
                    Stash.put(query + Constants.CURRENT_QUERY_LONG, LONG);

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

        } else {
            Log.d(TAG, "getLat: } else {");
            // IF VALUES ARE ALREADY SAVED THEN RETURN THOSE VALUES
            LAT = Double.parseDouble(lat);
            LONG = Double.parseDouble(lng);
        }

        return LAT;
    }


}
