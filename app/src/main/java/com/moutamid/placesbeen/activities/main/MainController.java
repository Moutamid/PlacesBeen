package com.moutamid.placesbeen.activities.main;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.dezlum.codelabs.getjson.GetJson;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MainController {
    private static final String TAG = "FUCKK";
    MainActivity mainActivity;
    Context context;
    public String SELECTED_JSON = Constants.WORLD_CITIES_JSON;
    public View currentDot;

    public MainController(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.context = mainActivity;
        this.currentDot = mainActivity.b.dotContinent;
    }

    public void getContinents(ArrayList<String> LIST) {
        Log.d(TAG, "getContinents: ");
        mainActivity.b.mainRecyclerView.showShimmerAdapter();
        mainActivity.mainItemModelArrayList.clear();

        for (int i = 0; i < LIST.size() - 1; i++) {
            Log.d(TAG, "getContinents: for loop: " + i);
            MainItemModel model = new MainItemModel();

            model.title = LIST.get(i);
            model.desc = "";
            model.lat = Constants.NULL;
            model.lng = Constants.NULL;

            model.url = Constants.NULL;
//            model.url = getImageUrl(model.title, model.title);

            mainActivity.mainItemModelArrayList.add(model);

        }

        mainActivity.runOnUiThread(() -> mainActivity.initRecyclerView());

    }

    public void getCountries() {
        Log.d(TAG, "getCountries: ");
        getContinents(Constants.COUNTRIES_LIST);
    }

    public void getStates() {
        Log.d(TAG, "getStates: ");
        getContinents(Constants.STATES_LIST());
    }

    public void getCities(String currentJson) {
        Log.d(TAG, "getCities: ");
        mainActivity.b.mainRecyclerView.showShimmerAdapter();
        SELECTED_JSON = currentJson;
        new Thread(() -> {
            try {
                Log.d(TAG, "getCities: try {");
                JSONArray jsonArray = new JSONArray(loadJSONFromAsset());
                Log.d(TAG, "getCities: jsonArray done");
                mainActivity.mainItemModelArrayList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.d(TAG, "getCities: for loop: " + i);
                    JSONObject jo_inside = jsonArray.getJSONObject(i);

                    MainItemModel model = new MainItemModel();

                    fillModelClass(jo_inside, model, SELECTED_JSON);

                    model.url = Constants.NULL;

                    mainActivity.mainItemModelArrayList.add(model);
                }

                mainActivity.runOnUiThread(() -> mainActivity.initRecyclerView());

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "getCities: ERROR: " + e.getMessage());
            }

        }).start();

    }

    public void getCulturalSites() {
        Log.d(TAG, "getCulturalSites: ");
        getCities(Constants.CULTURAL_SITES_JSON);
    }

    public void getNationalParks() {
        Log.d(TAG, "getNationalParks: ");
        getCities(Constants.CULTURAL_SITES_JSON);
    }

    public void getAirports() {
        Log.d(TAG, "getAirports: ");
        getCities(Constants.AIRPORTS_JSON);
    }

    public void changeDotTo(View dot) {
        Log.d(TAG, "changeDotTo: ");
        currentDot.setVisibility(View.GONE);
        currentDot = dot;
        currentDot.setVisibility(View.VISIBLE);
    }

    private void fillModelClass(JSONObject jo_inside, MainItemModel model, String option) {
        try {
            Log.d(TAG, "fillModelClass: try {");
            switch (option) {
                case Constants.WORLD_CITIES_JSON:
                    Log.d(TAG, "fillModelClass: case Constants.WORLD_CITIES_JSON:");
                    model.title = jo_inside.getString("city");
                    model.desc = jo_inside.getString("country");
                    model.lat = String.valueOf(jo_inside.getDouble("lat"));
                    model.lng = String.valueOf(jo_inside.getDouble("lng"));
                    break;
                case Constants.AIRPORTS_JSON:
                    Log.d(TAG, "fillModelClass: case Constants.AIRPORTS_JSON:");
                    model.title = jo_inside.getString("name");
                    model.desc = jo_inside.getString("country") + ", "
                            + jo_inside.getString("code")
                            + "\n(" + jo_inside.getString("type") + ")";
                    Log.d(TAG, "fillModelClass: desc: " + model.desc);
                    String coordinates = jo_inside.getString("coordinates");
                    String[] lnglat = coordinates.split(",");

                    model.lat = lnglat[1].trim();
                    model.lng = lnglat[0].trim();
                    Log.d(TAG, "fillModelClass: lat: " + model.lat);
                    Log.d(TAG, "fillModelClass: lng: " + model.lng);
                    break;
                case Constants.CULTURAL_SITES_JSON:
                    Log.d(TAG, "fillModelClass: case Constants.CULTURAL_SITES_JSON:");
                    model.title = jo_inside.getString("name");
                    model.desc = jo_inside.getString("country");
                    model.lat = jo_inside.getString("lat");
                    model.lng = jo_inside.getString("lng");
                    break;
                default:
                    Log.d(TAG, "fillModelClass: default:");
                    model.title = Constants.NULL;
                    model.desc = Constants.NULL;
                    model.lat = Constants.NULL;
                    model.lng = Constants.NULL;
                    break;
            }


        } catch (JSONException e) {
            Log.e(TAG, "fillModelClass: ERROR: " + e.getMessage());
            e.printStackTrace();
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

    private String loadJSONFromAsset() {
        Log.d(TAG, "loadJSONFromAsset: SelectedJson: " + SELECTED_JSON);
        String json = null;
        try {
            InputStream is = context.getAssets().open(SELECTED_JSON);
//            InputStream is = context.getAssets().open("worldcities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Log.d(TAG, "loadJSONFromAsset: error: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public String getImageUrl(String tt, String dd) {
        String link = "null";

        try {
            String title = URLEncoder.encode(tt, "utf-8");
            String desc = URLEncoder.encode(dd, "utf-8");

            JSONObject jsonObject;
            if (SELECTED_JSON.equals(Constants.AIRPORTS_JSON))
                jsonObject = downloadJSON("airport", "american airports");
            else
                jsonObject = downloadJSON(title, desc);

            JSONArray jsonArray = jsonObject.getJSONArray("hits");

            JSONObject innerObject;
            if (SELECTED_JSON.equals(Constants.AIRPORTS_JSON))
                innerObject = jsonArray.getJSONObject(new Random().nextInt(200));
            else
                innerObject = jsonArray.getJSONObject(0);

            link = innerObject.getString("previewURL");
//            link = innerObject.getString("webformatURL");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return link;

    }

}
