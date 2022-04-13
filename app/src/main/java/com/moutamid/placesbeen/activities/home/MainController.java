package com.moutamid.placesbeen.activities.home;

import static com.moutamid.placesbeen.utils.Utils.toast;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.moutamid.placesbeen.activities.place.PlaceItemActivity;
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
    MainActivity activity;

    public ArrayList<MarkerModel> markers = new ArrayList<>();
    public ArrayList<PolygonModel> polygonModelArrayList = new ArrayList<>();

    public MainController(MainActivity saveFragment) {
        this.context = saveFragment;
        this.activity = saveFragment;
    }

    /*public void fetchAllPolygonBoundaries() {
        Log.d(TAG, "fetchAllPolygonBoundaries: ");
        new Thread(() -> {
            RequestQueue queue = Volley.newRequestQueue(activity);

            ArrayList<MainItemModel> CountryArrayList = Stash.getArrayList(Constants.PARAMS_Country, MainItemModel.class);

            for (int i = 0; i < CountryArrayList.size(); i++) {
                String country = CountryArrayList.get(i).title;
                Log.d(i + " HUFFF", "fetchAllPolygonBoundaries: COUNTRY: " + country);

                PolygonOptions polygonOptions = (PolygonOptions) Stash.getObject(Constants.POLYGON_OPTIONS + country + 0, PolygonOptions.class);
                if (polygonOptions == null) {
                    Log.d("HUFF", "POLYGON IS NULL" + i);
                    queue.add(getPolygonDataRequest(country));
                }
            }
            Log.d(TAG, "fetchAllPolygonBoundaries: done");
        }).start();
    }

    private JsonArrayRequest getPolygonDataRequest(String country) {
        Log.d("HUFFF", "getPolygonDataRequest: started: " + country);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                Constants.GET_BOUNDARY_URL((country)),
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
                                polygonOptions.strokeWidth(1);
                                polygonOptions.fillColor(Color.argb(255, 55, 0, 179));

                                for (int i = 0; i < latlngArray.length(); i++) {

                                    double lng = latlngArray.getJSONArray(i).getDouble(0);
                                    double lat = latlngArray.getJSONArray(i).getDouble(1);

                                    LatLng latLng = new LatLng(lat, lng);

                                    polygonOptions.add(latLng);
                                }

                                Stash.put(Constants.POLYGON_OPTIONS + country + 0, polygonOptions);

                            } else {
                                Log.d("HUFF", "drawPolygon: looping");
                                for (int i1 = 0; i1 < innerArray.length(); i1++) {
                                    Stash.put(Constants.POLYGON_OPTIONS_INDEX + country, i1);
                                    JSONArray array1 = innerArray.getJSONArray(i1);

                                    JSONArray array2 = array1.getJSONArray(0);

                                    PolygonOptions polygonOptions = new PolygonOptions();
                                    polygonOptions.strokeColor(Color.WHITE);
                                    polygonOptions.strokeWidth(1);
                                    polygonOptions.fillColor(Color.argb(255, 55, 0, 179));

                                    for (int i2 = 0; i2 < array2.length(); i2++) {

                                        JSONArray latlngArrray = array2.getJSONArray(i2);

                                        double lng = latlngArrray.getDouble(0);
                                        double lat = latlngArrray.getDouble(1);

                                        LatLng latLng = new LatLng(lat, lng);

                                        polygonOptions.add(latLng);

                                    }
                                    Stash.put(Constants.POLYGON_OPTIONS + country + i1, polygonOptions);
                                }
                            }

                            Log.d("HUFFF", "getPolygonDataRequest: completed: " + country);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("HUFFF", country + "getPolygonDataRequest: error: " + e.getMessage());

                            Log.e("HUFF", "drawPolygon: ERROR: " + e.getMessage());
                        }
                    }
                }, error -> {
            toast("Failed to get data: " + error.getMessage());
            Log.d("HUFFF", country + "getPolygonDataRequest: ERROR: " + error.getMessage());
            if (error instanceof NetworkError) {
                Log.d("HUFFF", "NetworkError " + country);
            } else if (error instanceof ServerError) {
                Log.d("HUFFF", "ServerError " + country);
            } else if (error instanceof AuthFailureError) {
                Log.d("HUFFF", "AuthFailureError " + country);
            } else if (error instanceof ParseError) {
                Log.d("HUFFF", "ParseError " + country);
            } else if (error instanceof NoConnectionError) {
                Log.d("HUFFF", "NoConnectionError " + country);
            } else if (error instanceof TimeoutError) {
                Log.d("HUFFF", "TimeoutError " + country);
            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return jsonArrayRequest;
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
    }*/

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
                        activity.savedArrayListCountries,
                        R.drawable.save_marker,
                        Color.argb(255, 55, 0, 179),
                        "Saved"));
        Constants.databaseReference()
                .child(Constants.auth().getUid()).child(Constants.BEEN_ITEMS_PATH)
                .addChildEventListener(itemsChildValueListener(
                        Constants.BEEN_ITEMS_PATH,
                        activity.beenArrayListCountries,
                        R.drawable.been_marker,
                        Color.argb(255, 73, 238, 69),
                        "Been"));
        Constants.databaseReference()
                .child(Constants.auth().getUid()).child(Constants.WANT_TO_ITEMS_PATH)
                .addChildEventListener(itemsChildValueListener(
                        Constants.WANT_TO_ITEMS_PATH,
                        activity.wantToArrayListCountries,
                        R.drawable.want_to_marker,
                        Color.argb(255, 254, 154, 0),
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

//                    new DrawPolygonTask(model.title, colour, model).execute();
                    drawCountryPolygon(model.title, colour, model);
//                        if (saveFragment.isAdded())
                    activity.runOnUiThread(() -> {
                        activity.mMap.setOnMapClickListener(latLng -> {
                            triggerOnClick(latLng);
                        });
                    });

                    if (ITEMS_PATH.equals(Constants.SAVED_ITEMS_PATH) && !savedList.contains(model.title)) {
                        savedList.add(model.title + model.desc);
                        Stash.put(Constants.SAVED_LIST, savedList);
                    }
//                    }).start();
                }
            }

            private void addMarkerOnMaps(MainItemModel model, final int marker, String title) {
                new Thread(() -> {
                    Log.d(TAG, "addMarkerOnMaps: " + model.title);

                    if (Constants.COUNTRIES_LIST.contains(model.title))
                        return;

                    double lat;
                    double lng;

                    try {
                        if (model.lat.equals(Constants.NULL)) {
                            lat = getLat(model.title);
                            lng = LONG;
                        } else {
                            lat = Double.parseDouble(model.lat);
                            lng = Double.parseDouble(model.lng);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        lat = 0;
                        lng = 0;
                    }

                    if (lat == 0 && lng == 0)
                        return;

                    LatLng sydney = new LatLng(lat, lng);
//                if (saveFragment.isAdded())
                    activity.runOnUiThread(() -> {
                        boolean descNull = false;
                        if (model.desc == null || model.desc.equals(Constants.NULL) || model.desc.equals("") || TextUtils.isEmpty(model.desc)) {
                            descNull = true;
                        }

                        Marker marker1;
                        if (model.type.equals(Constants.PARAMS_NationalParks) || model.type.equals(Constants.PARAMS_CulturalSites)) {
                            marker1 = activity.mMap.addMarker(new MarkerOptions().position(sydney)
                                    .title(model.title)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.parks_marker)));
                        } else {
                            marker1 = activity.mMap.addMarker(new MarkerOptions().position(sydney)
                                    .title(model.title)
                                    .icon(BitmapDescriptorFactory.fromResource(marker)));
                        }
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

                try {
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
                                activity.runOnUiThread(() -> {
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

                            if (title.equals(model.title)) {
                                Polygon polygon = polygonModelArrayList.get(i).polygon;
                                activity.runOnUiThread(() -> {
                                    polygon.remove();
                                });
                                polygonModelArrayList.remove(i);
                                break;
                            }

                        }

                        // REMOVING ITEM FROM SAVED LIST
                        if (ITEMS_PATH.equals(Constants.SAVED_ITEMS_PATH) && savedList.contains(model.title)) {
                            savedList.remove(model.title + model.desc);
                            Stash.put(Constants.SAVED_LIST, savedList);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "onChildRemoved: ERROR: " + e.getMessage());
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
        activity.runOnUiThread(() -> {
            activity.mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    Log.d(TAG, "onMapReady: ");
                    activity.mMap = googleMap;

                    MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle);
                    activity.mMap.setMapStyle(style);
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

    public void retrieveSearchListItems() {
        new Thread(() -> {
            ArrayList<MainItemModel> mainItemModelArrayList = new ArrayList<>();

            ArrayList<MainItemModel> CountryArrayList = new ArrayList<>();
            ArrayList<MainItemModel> CityArrayList = new ArrayList<>();
            ArrayList<MainItemModel> CulturalSitesArrayList = new ArrayList<>();
            ArrayList<MainItemModel> AirportsArrayList = new ArrayList<>();

            CountryArrayList = Stash.getArrayList(Constants.PARAMS_Country, MainItemModel.class);
            CityArrayList = Stash.getArrayList(Constants.PARAMS_City, MainItemModel.class);
            CulturalSitesArrayList = Stash.getArrayList(Constants.PARAMS_CulturalSites, MainItemModel.class);
            AirportsArrayList = Stash.getArrayList(Constants.PARAMS_Airports, MainItemModel.class);

            mainItemModelArrayList.addAll(CountryArrayList);
            mainItemModelArrayList.addAll(CityArrayList);
            mainItemModelArrayList.addAll(CulturalSitesArrayList);
            mainItemModelArrayList.addAll(AirportsArrayList);

            activity.mainItemModelArrayListAll.addAll(mainItemModelArrayList);

            if (CountryArrayList.size() > 0)
                activity.mainItemModelArrayList.add(CountryArrayList.get(0));

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.initRecyclerView();
                }
            });

        }).start();
    }

    public void drawCountryPolygon(String country, int colour, MainItemModel model) {
        Log.e("HUFF", "drawPolygon: COUNTRY: " + country);
        new Thread(() -> {
            RequestQueue queue = Volley.newRequestQueue(activity);

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
                                    polygonOptions.strokeWidth(2);
                                    polygonOptions.fillColor(colour);

                                    for (int i = 0; i < latlngArray.length(); i++) {

                                        double lng = latlngArray.getJSONArray(i).getDouble(0);
                                        double lat = latlngArray.getJSONArray(i).getDouble(1);

                                        LatLng latLng = new LatLng(lat, lng);

                                        polygonOptions.add(latLng);
                                    }

                                    activity.runOnUiThread(() -> {
                                        boolean descNull = false;
                                        if (model.desc == null || model.desc.equals(Constants.NULL) || model.desc.equals("") || TextUtils.isEmpty(model.desc)) {
                                            descNull = true;
                                        }
                                        Log.d(TAG, "drawPolygon: title: " + model.title + " descIsNull: " + descNull);
                                        PolygonModel polygonModel = new PolygonModel();
                                        polygonModel.title = country;
                                        polygonModel.polygon = activity.mMap.addPolygon(polygonOptions);

                                        polygonModel.polygon.setVisible(descNull);

                                        polygonModel.descNull = descNull;

                                        polygonModelArrayList.add(polygonModel);

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
                                        polygonOptions.strokeWidth(2);
                                        polygonOptions.fillColor(colour);

                                        for (int i2 = 0; i2 < array2.length(); i2++) {

                                            JSONArray latlngArrray = array2.getJSONArray(i2);

                                            double lng = latlngArrray.getDouble(0);
                                            double lat = latlngArrray.getDouble(1);

                                            LatLng latLng = new LatLng(lat, lng);

                                            polygonOptions.add(latLng);

                                        }
                                        Stash.put(Constants.POLYGON_OPTIONS + country + i1, polygonOptions);
                                        activity.runOnUiThread(() -> {
                                            PolygonModel polygonModel = new PolygonModel();
                                            polygonModel.title = country;
                                            polygonModel.polygon = activity.mMap.addPolygon(polygonOptions);

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

                                Log.d("HUFF", "drawPolygon: jsonArray done");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("HUFF", "drawPolygon: ERROR: " + e.getMessage());
                            }
                        }
                    }, error -> {
//                toast("Failed to get data: " + error.getMessage());
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
                    activity.runOnUiThread(() -> {
                        boolean descNull = false;
                        if (model.desc == null || model.desc.equals(Constants.NULL) || model.desc.equals("") || TextUtils.isEmpty(model.desc)) {
                            descNull = true;
                        }
                        Log.d(TAG, "drawPolygon: title: " + model.title + " descIsNull: " + descNull);
                        polygonOptions.fillColor(colour);
                        PolygonModel polygonModel = new PolygonModel();
                        polygonModel.title = country;
                        polygonModel.polygon = activity.mMap.addPolygon(polygonOptions);

                        polygonModel.polygon.setVisible(descNull);

                        polygonModel.descNull = descNull;

                        polygonModelArrayList.add(polygonModel);
                    });
                }
            }
        }).start();
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
//                        Log.d(TAG, "drawPolygon: iteration polygon: " + i);

                        double lng = latlngArray.getJSONArray(i).getDouble(0);
                        double lat = latlngArray.getJSONArray(i).getDouble(1);

                        LatLng latLng = new LatLng(lat, lng);

                        polygonOptions.add(latLng);
                    }

//                if (saveFragment.isAdded())
                    activity.runOnUiThread(() -> {
                        boolean descNull = false;
                        if (model.desc == null || model.desc.equals(Constants.NULL) || model.desc.equals("") || TextUtils.isEmpty(model.desc)) {
                            descNull = true;
                        }
                        Log.d(TAG, "drawPolygon: title: " + model.title + " descIsNull: " + descNull);
                        PolygonModel polygonModel = new PolygonModel();
                        polygonModel.title = country;
                        polygonModel.polygon = activity.mMap.addPolygon(polygonOptions);

                        polygonModel.polygon.setVisible(descNull);

                        polygonModel.descNull = descNull;

                        polygonModelArrayList.add(polygonModel);
                    });
                } else {
                    for (int i1 = 0; i1 < innerArray.length(); i1++) {
//                        Log.d(TAG, "drawPolygon: iteration multipolygon: " + i1);
                        JSONArray array1 = innerArray.getJSONArray(i1);

                        JSONArray array2 = array1.getJSONArray(0);

                        PolygonOptions polygonOptions = new PolygonOptions();
                        polygonOptions.strokeColor(Color.WHITE);
                        polygonOptions.strokeWidth((float) 0.80);
                        polygonOptions.fillColor(colour);

                        for (int i2 = 0; i2 < array2.length(); i2++) {
//                            Log.d(TAG, "drawPolygon: iteration2 multipolygon: " + i2);

                            JSONArray latlngArrray = array2.getJSONArray(i2);

                            double lng = latlngArrray.getDouble(0);
                            double lat = latlngArrray.getDouble(1);

                            LatLng latLng = new LatLng(lat, lng);

                            polygonOptions.add(latLng);

                        }

//                    if (saveFragment.isAdded())
                        activity.runOnUiThread(() -> {
                            Log.d(TAG, "drawPolygon: added");
                            PolygonModel polygonModel = new PolygonModel();
                            polygonModel.title = country;
                            polygonModel.polygon = activity.mMap.addPolygon(polygonOptions);

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
