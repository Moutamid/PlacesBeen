package com.moutamid.placesbeen.onboard;

import android.content.Context;
import android.util.Log;

import com.fxn.stash.Stash;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class SplashController {
    private static final String TAG = "FUCKK";
    SplashActivity mainActivity;
    Context context;

    public SplashController(SplashActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.context = mainActivity;
    }

    public void getContinents() {
        Log.d(TAG, "getContinents: ");

        new Thread(() -> {
            mainActivity.ContinentArrayList.clear();
            for (int i = 0; i < Constants.CONTINENTS_LIST.size() - 1; i++) {
//                    Log.d(TAG, "getContinents: for loop: " + i);
                MainItemModel model = new MainItemModel();

                model.title = Constants.CONTINENTS_LIST.get(i);
                model.desc = "";
                model.lat = Constants.NULL;
                model.lng = Constants.NULL;
                model.type = Constants.PARAMS_Continent;
                model.url = Constants.NULL;

                mainActivity.ContinentArrayList.add(model);

            }

            Stash.put(Constants.PARAMS_Continent, mainActivity.ContinentArrayList);

            getCountries();
        }).start();

    }

    public void getCountries() {
        Log.d(TAG, "getCountries: ");
        mainActivity.CountryArrayList.clear();

        for (int i = 0; i < Constants.COUNTRIES_LIST.size() - 1; i++) {
//            Log.d(TAG, "getContinents: for loop: " + i);
            MainItemModel model = new MainItemModel();

            model.title = Constants.COUNTRIES_LIST.get(i);
            model.desc = "";
            model.lat = Constants.NULL;
            model.lng = Constants.NULL;
            model.type = Constants.PARAMS_Country;
            model.url = Constants.NULL;
//            model.url = getImageUrl(model.title, model.title);

            mainActivity.CountryArrayList.add(model);

        }
        Stash.put(Constants.PARAMS_Country, mainActivity.CountryArrayList);

        getStates();

    }

    public void getStates() {
        Log.d(TAG, "getStates: ");
        mainActivity.StatesArrayList.clear();

        for (int i = 0; i < Constants.STATES_LIST().size() - 1; i++) {
//            Log.d(TAG, "getContinents: for loop: " + i);
            MainItemModel model = new MainItemModel();

            model.title = Constants.STATES_LIST().get(i);
            model.desc = "";
            model.lat = Constants.NULL;
            model.lng = Constants.NULL;
            model.type = Constants.PARAMS_States;

            model.url = Constants.NULL;
//            model.url = getImageUrl(model.title, model.title);

            mainActivity.StatesArrayList.add(model);

        }

        Stash.put(Constants.PARAMS_States, mainActivity.StatesArrayList);

        getCities();

//            }


    }

    public void getCities() {
        Log.d(TAG, "getCities: ");
        try {
            Log.d(TAG, "getCities: try {");
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset(Constants.WORLD_CITIES_JSON));
            Log.d(TAG, "getCities: jsonArray done");
            mainActivity.CityArrayList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
//                Log.d(TAG, "getCities: for loop: " + i);
                JSONObject jo_inside = jsonArray.getJSONObject(i);

                MainItemModel model = new MainItemModel();

                fillModelClass(jo_inside, model, Constants.WORLD_CITIES_JSON);

                model.url = Constants.NULL;
                model.type = Constants.PARAMS_City;

                mainActivity.CityArrayList.add(model);
            }

            Stash.put(Constants.PARAMS_City, mainActivity.CityArrayList);

            getCulturalSites();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "getCities: ERROR: " + e.getMessage());
        }


    }

    public void getCulturalSites() {
        Log.d(TAG, "getCulturalSites: ");

        try {
            Log.d(TAG, "getCities: try {");
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset(Constants.CULTURAL_SITES_JSON));
            Log.d(TAG, "getCities: jsonArray done");
            mainActivity.CulturalSitesArrayList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
//                Log.d(TAG, "getCities: for loop: " + i);
                JSONObject jo_inside = jsonArray.getJSONObject(i);

                MainItemModel model = new MainItemModel();

                fillModelClass(jo_inside, model, Constants.CULTURAL_SITES_JSON);

                model.url = Constants.NULL;
                model.type = Constants.PARAMS_CulturalSites;

                mainActivity.CulturalSitesArrayList.add(model);
            }

            Stash.put(Constants.PARAMS_CulturalSites, mainActivity.CulturalSitesArrayList);

            getNationalParks();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "getCities: ERROR: " + e.getMessage());
        }


    }

    public void getNationalParks() {
        Log.d(TAG, "getNationalParks: ");
//
        try {
            Log.d(TAG, "getCities: try {");
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset(Constants.CULTURAL_SITES_JSON));
            mainActivity.NationalParksArrayList.clear();
            Log.d(TAG, "getCities: jsonArray done");
            for (int i = 0; i < jsonArray.length(); i++) {
//                Log.d(TAG, "getCities: for loop: " + i);
                JSONObject jo_inside = jsonArray.getJSONObject(i);

                MainItemModel model = new MainItemModel();

                fillModelClass(jo_inside, model, Constants.CULTURAL_SITES_JSON);

                model.url = Constants.NULL;
                model.type = Constants.PARAMS_NationalParks;
                /*if (model.desc.contains(","))
                    Log.d(TAG, "COUNTRYYY: " + model.desc);*/
                mainActivity.NationalParksArrayList.add(model);
            }

            Stash.put(Constants.PARAMS_NationalParks, mainActivity.NationalParksArrayList);

            getAirports();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "getCities: ERROR: " + e.getMessage());
        }


    }

    public void getAirports() {
        Log.d(TAG, "getAirports: ");
//
        try {
            Log.d(TAG, "getCities: try {");
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset(Constants.AIRPORTS_JSON));
            Log.d(TAG, "getCities: jsonArray done");
            mainActivity.AirportsArrayList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
//                Log.d(TAG, "getCities: for loop: " + i);
                JSONObject jo_inside = jsonArray.getJSONObject(i);

                MainItemModel model = new MainItemModel();

                fillModelClass(jo_inside, model, Constants.AIRPORTS_JSON);

                model.url = Constants.NULL;
                model.type = Constants.PARAMS_Airports;

                mainActivity.AirportsArrayList.add(model);
            }

            Stash.put(Constants.PARAMS_Airports, mainActivity.AirportsArrayList);
            Stash.put(Constants.IS_FIRST_TIME, false);

            mainActivity.openActivity();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "getCities: ERROR: " + e.getMessage());
        }


    }

    private void fillModelClass(JSONObject jo_inside, MainItemModel model, String option) {
        try {
//            Log.d(TAG, "fillModelClass: try {");
            switch (option) {
                case Constants.WORLD_CITIES_JSON:
//                    Log.d(TAG, "fillModelClass: case Constants.WORLD_CITIES_JSON:");
                    model.title = jo_inside.getString("city");
                    model.desc = jo_inside.getString("country");
                    model.lat = String.valueOf(jo_inside.getDouble("lat"));
                    model.lng = String.valueOf(jo_inside.getDouble("lng"));
                    break;
                case Constants.AIRPORTS_JSON:
//                    Log.d(TAG, "fillModelClass: case Constants.AIRPORTS_JSON:");
                    model.title = jo_inside.getString("name");
                    model.desc = jo_inside.getString("country") + ", "
                            + jo_inside.getString("code")
                            + "\n(" + jo_inside.getString("type") + ")";
//                    Log.d(TAG, "fillModelClass: desc: " + model.desc);
                    String coordinates = jo_inside.getString("coordinates");
                    String[] lnglat = coordinates.split(",");

                    model.lat = lnglat[1].trim();
                    model.lng = lnglat[0].trim();
//                    Log.d(TAG, "fillModelClass: lat: " + model.lat);
//                    Log.d(TAG, "fillModelClass: lng: " + model.lng);
                    break;
                case Constants.CULTURAL_SITES_JSON:
                    model.title = jo_inside.getString("name");
                    /*String[] cc = jo_inside.getString("country").split(",");
                    model.desc = cc[0];*/
                    model.desc = jo_inside.getString("country").split(",")[0];
                    model.lat = jo_inside.getString("lat");
                    model.lng = jo_inside.getString("lng");
                    break;
                default:
//                    Log.d(TAG, "fillModelClass: default:");
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

    private String loadJSONFromAsset(String currentJson) {
        Log.d(TAG, "loadJSONFromAsset: SelectedJson: " + currentJson);
        String json = null;
        try {
            InputStream is = context.getAssets().open(currentJson);
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

}
