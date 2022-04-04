package com.moutamid.placesbeen.activities.mapdata;

import static com.moutamid.placesbeen.utils.Utils.toast;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

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
import com.dezlum.codelabs.getjson.GetJson;
import com.fxn.stash.Stash;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.onboard.OnBoardingActivity;
import com.moutamid.placesbeen.onboard.SplashActivity;
import com.moutamid.placesbeen.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DownloadMapDataActivity extends AppCompatActivity {
    private static final String TAG = "DownloadMapData";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_map_data);

        boolean connected = new GetJson().isConnected(this);

        if (connected) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            fetchAllPolygonBoundaries();
        } else {
            toast("Internet is not connected. Please connect to a wifi/cellular and true again!");
        }
    }

    public void fetchAllPolygonBoundaries() {
        Log.d(TAG, "fetchAllPolygonBoundaries: ");
        new Thread(() -> {
            RequestQueue queue = Volley.newRequestQueue(DownloadMapDataActivity.this);

            ArrayList<MainItemModel> CountryArrayList = Stash.getArrayList(Constants.PARAMS_Country, MainItemModel.class);

            for (int i = 0; i < CountryArrayList.size(); i++) {
                String country = CountryArrayList.get(i).title;
                Log.d(i + " HUFFF", "fetchAllPolygonBoundaries: COUNTRY: " + country);

                PolygonOptions polygonOptions = (PolygonOptions) Stash.getObject(Constants.POLYGON_OPTIONS + country + 0, PolygonOptions.class);
                if (polygonOptions == null) {
                    Log.d("HUFF", "POLYGON IS NULL" + i);
                    queue.add(getPolygonDataRequest(country, i));
                }
            }
            Log.d(TAG, "fetchAllPolygonBoundaries: done");
        }).start();
    }

    int count = 0;// <233

    private JsonArrayRequest getPolygonDataRequest(String country, int position) {
        Log.d("HUFFF", "getPolygonDataRequest: started: " + country);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                Constants.GET_BOUNDARY_URL((country)),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        updateProgressBar();

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

                        if (position == Constants.COUNTRIES_LIST.size() - 1) {
                            // ACTIVITY SHOULD BE ENDED BECAUSE ALL DATA HAS BEEN DOWNLOADED
                            runOnUiThread(() -> {
                                progressDialog.dismiss();
                                toast("Download completed");
                                startActivity(new Intent(DownloadMapDataActivity.this, OnBoardingActivity.class));
                            });
                        }

                    }
                }, error -> {
            updateProgressBar();
            if (position == Constants.COUNTRIES_LIST.size() - 1) {
//            if (country.equals("Palestine")) {
                // ACTIVITY SHOULD BE ENDED BECAUSE ALL DATA HAS BEEN DOWNLOADED
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    toast("Download completed");
                    startActivity(new Intent(DownloadMapDataActivity.this, OnBoardingActivity.class));

                });
            }
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

    private void updateProgressBar() {
        count++;
        runOnUiThread(() -> {
            progressDialog.setMessage("Downloading maps data (" + count + "/240)" + "\nDON'T CLOSE THE APP");
        });
    }

}