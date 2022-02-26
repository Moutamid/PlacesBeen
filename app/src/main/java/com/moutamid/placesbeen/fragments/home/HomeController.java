package com.moutamid.placesbeen.fragments.home;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dezlum.codelabs.getjson.GetJson;
import com.fxn.stash.Stash;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.activities.home.MainActivity;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class HomeController {
    private static final String TAG = "FUCKK";
    HomeFragment mainActivity;
    Context context;
    //    public String SELECTED_JSON = Constants.WORLD_CITIES_JSON;
    public View currentDot;
    public TextView currentTextView;

    public HomeController(HomeFragment mainActivity) {
        this.mainActivity = mainActivity;
        this.context = mainActivity.requireContext();
        this.currentDot = mainActivity.b.dotContinent;
        this.currentTextView = mainActivity.b.textViewContinent;
    }

    public void changeDotTo(View dot, TextView textView) {
        Log.d(TAG, "changeDotTo: ");
        currentDot.setVisibility(View.GONE);
        currentDot = dot;
        currentDot.setVisibility(View.VISIBLE);

        currentTextView.setTextColor(mainActivity.getResources().getColor(R.color.darkGrey));
        currentTextView = textView;
        currentTextView.setTextColor(mainActivity.getResources().getColor(R.color.yellow));
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

    public String getImageUrl(String tt, String dd) {
        String link = "null";

        try {
            String title = URLEncoder.encode(tt, "utf-8");
            String desc = URLEncoder.encode(dd, "utf-8");

            JSONObject jsonObject;
            if (mainActivity.isAirport)
                jsonObject = downloadJSON("airport", "american airports");
            else
                jsonObject = downloadJSON(title, desc);

            JSONArray jsonArray = jsonObject.getJSONArray("hits");

            JSONObject innerObject;
            if (mainActivity.isAirport)
                innerObject = jsonArray.getJSONObject(new Random().nextInt(jsonArray.length()) - 2);
            else
                innerObject = jsonArray.getJSONObject(0);

            link = innerObject.getString("previewURL");
//            link = innerObject.getString("webformatURL");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return link;

    }

    public void saveUnSaveItem(MainItemModel model, ImageView saveBtn) {
        if (model.title.equals("nullnull")) {
            Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Stash.getBoolean(model.title, false)) {
            // IF ALREADY SAVED THEN REMOVE
            saveBtn.setImageResource(R.drawable.ic_unsave_24);
            YoYo.with(Techniques.Bounce).duration(700).playOn(saveBtn);
            Constants.databaseReference()
                    .child(Constants.auth().getUid())
                    .child(Constants.SAVED_ITEMS_PATH)
                    .child(model.title)
                    .removeValue();

            Stash.clear(model.title);
        } else {
            // IF NOT SAVED THEN SAVE
            saveBtn.setImageResource(R.drawable.ic_save_24);
            YoYo.with(Techniques.Bounce).duration(700).playOn(saveBtn);
            Constants.databaseReference()
                    .child(Constants.auth().getUid())
                    .child(Constants.SAVED_ITEMS_PATH)
                    .child(model.title)
                    .setValue(model);

            Stash.put(model.title, true);
        }
    }

    public void isSaved(MainItemModel model, ImageView saveBtn) {
        if (Stash.getBoolean(model.title, false)) {
            Log.d("MFUCKER", "isSaved: " + model.title);
            saveBtn.setImageResource(R.drawable.ic_save_24);
        }
    }

    public void retrieveDatabaseItems() {
        Constants.databaseReference()
                .child(Constants.auth().getUid())
                .child(Constants.SAVED_ITEMS_PATH)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()) {
                            try {

                                MainItemModel model = snapshot.getValue(MainItemModel.class);

                                Stash.put(model.title, true);
                            } catch (Exception e) {
                                Log.e(TAG, "onChildAdded: ERROR: " + snapshot.getKey());
                            }

                        }

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            try {

                                MainItemModel model = snapshot.getValue(MainItemModel.class);

                                Stash.clear(model.title);

                            } catch (Exception e) {
                                Log.e(TAG, "onChildRemoved: ERROR: " + snapshot.getKey());
                            }

                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    /*

    public void getContinents(boolean b) {
        Log.d(TAG, "getContinents: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mainActivity.ContinentArrayList.clear();
                for (int i = 0; i < Constants.CONTINENTS_LIST.size() - 1; i++) {
                    Log.d(TAG, "getContinents: for loop: " + i);
                    MainItemModel model = new MainItemModel();

                    model.title = Constants.CONTINENTS_LIST.get(i);
                    model.desc = "";
                    model.lat = Constants.NULL;
                    model.lng = Constants.NULL;

                    model.url = Constants.NULL;
//            model.url = getImageUrl(model.title, model.title);

                    mainActivity.ContinentArrayList.add(model);

                }
                if (b) {
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.mainItemModelArrayList = mainActivity.ContinentArrayList;
                            mainActivity.initRecyclerView();
                        }
                    });
                }
            }
        }).start();

    }

    public void getCountries(boolean b) {
        Log.d(TAG, "getCountries: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mainActivity.CountryArrayList.clear();

                for (int i = 0; i < Constants.COUNTRIES_LIST.size() - 1; i++) {
                    Log.d(TAG, "getContinents: for loop: " + i);
                    MainItemModel model = new MainItemModel();

                    model.title = Constants.COUNTRIES_LIST.get(i);
                    model.desc = "";
                    model.lat = Constants.NULL;
                    model.lng = Constants.NULL;

                    model.url = Constants.NULL;
//            model.url = getImageUrl(model.title, model.title);

                    mainActivity.CountryArrayList.add(model);

                }

//        mainActivity.runOnUiThread(() -> mainActivity.initRecyclerView());
                if (b) {
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.mainItemModelArrayList = mainActivity.CountryArrayList;
                            mainActivity.initRecyclerView();
                        }
                    });
                }
            }
        }).start();

    }

    public void getStates(boolean b) {
        Log.d(TAG, "getStates: ");
        new Thread(new Runnable() {
            @Override
            public void run() {

                mainActivity.StatesArrayList.clear();

                for (int i = 0; i < Constants.STATES_LIST().size() - 1; i++) {
                    Log.d(TAG, "getContinents: for loop: " + i);
                    MainItemModel model = new MainItemModel();

                    model.title = Constants.STATES_LIST().get(i);
                    model.desc = "";
                    model.lat = Constants.NULL;
                    model.lng = Constants.NULL;

                    model.url = Constants.NULL;
//            model.url = getImageUrl(model.title, model.title);

                    mainActivity.StatesArrayList.add(model);

                }

//        mainActivity.runOnUiThread(() -> mainActivity.initRecyclerView());
                if (b) {
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.mainItemModelArrayList = mainActivity.StatesArrayList;
                            mainActivity.initRecyclerView();
                        }
                    });
                }
            }
        }).start();

    }

    public void getCities(boolean b) {
        Log.d(TAG, "getCities: ");

        new Thread(() -> {
            try {
                Log.d(TAG, "getCities: try {");
                JSONArray jsonArray = new JSONArray(loadJSONFromAsset(Constants.WORLD_CITIES_JSON));
                Log.d(TAG, "getCities: jsonArray done");
                mainActivity.CityArrayList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.d(TAG, "getCities: for loop: " + i);
                    JSONObject jo_inside = jsonArray.getJSONObject(i);

                    MainItemModel model = new MainItemModel();

                    fillModelClass(jo_inside, model, Constants.WORLD_CITIES_JSON);

                    model.url = Constants.NULL;

                    mainActivity.CityArrayList.add(model);
                }

//                mainActivity.runOnUiThread(() -> mainActivity.initRecyclerView());
                if (b) {
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.mainItemModelArrayList = mainActivity.CityArrayList;
                            mainActivity.initRecyclerView();
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "getCities: ERROR: " + e.getMessage());
            }

        }).start();

    }

    public void getCulturalSites(boolean b) {
        Log.d(TAG, "getCulturalSites: ");
        new Thread(() -> {
            try {
                Log.d(TAG, "getCities: try {");
                JSONArray jsonArray = new JSONArray(loadJSONFromAsset(Constants.CULTURAL_SITES_JSON));
                Log.d(TAG, "getCities: jsonArray done");
                mainActivity.CulturalSitesArrayList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.d(TAG, "getCities: for loop: " + i);
                    JSONObject jo_inside = jsonArray.getJSONObject(i);

                    MainItemModel model = new MainItemModel();

                    fillModelClass(jo_inside, model, Constants.CULTURAL_SITES_JSON);

                    model.url = Constants.NULL;

                    mainActivity.CulturalSitesArrayList.add(model);
                }

//                mainActivity.runOnUiThread(() -> mainActivity.initRecyclerView());

                if (b) {
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.mainItemModelArrayList = mainActivity.CulturalSitesArrayList;
                            mainActivity.initRecyclerView();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "getCities: ERROR: " + e.getMessage());
            }

        }).start();

    }

    public void getNationalParks(boolean b) {
        Log.d(TAG, "getNationalParks: ");
        new Thread(() -> {
            try {
                Log.d(TAG, "getCities: try {");
                JSONArray jsonArray = new JSONArray(loadJSONFromAsset(Constants.CULTURAL_SITES_JSON));
                mainActivity.NationalParksArrayList.clear();
                Log.d(TAG, "getCities: jsonArray done");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.d(TAG, "getCities: for loop: " + i);
                    JSONObject jo_inside = jsonArray.getJSONObject(i);

                    MainItemModel model = new MainItemModel();

                    fillModelClass(jo_inside, model, Constants.CULTURAL_SITES_JSON);

                    model.url = Constants.NULL;

                    mainActivity.NationalParksArrayList.add(model);
                }

//                mainActivity.runOnUiThread(() -> mainActivity.initRecyclerView());

                if (b) {
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.mainItemModelArrayList = mainActivity.NationalParksArrayList;
                            mainActivity.initRecyclerView();
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "getCities: ERROR: " + e.getMessage());
            }

        }).start();

    }

    public void getAirports(boolean b) {
        Log.d(TAG, "getAirports: ");
        new Thread(() -> {
            try {
                Log.d(TAG, "getCities: try {");
                JSONArray jsonArray = new JSONArray(loadJSONFromAsset(Constants.AIRPORTS_JSON));
                Log.d(TAG, "getCities: jsonArray done");
                mainActivity.AirportsArrayList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.d(TAG, "getCities: for loop: " + i);
                    JSONObject jo_inside = jsonArray.getJSONObject(i);

                    MainItemModel model = new MainItemModel();

                    fillModelClass(jo_inside, model, Constants.AIRPORTS_JSON);

                    model.url = Constants.NULL;

                    mainActivity.AirportsArrayList.add(model);
                }

//                mainActivity.runOnUiThread(() -> mainActivity.initRecyclerView());

                if (b) {
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.mainItemModelArrayList = mainActivity.AirportsArrayList;
                            mainActivity.initRecyclerView();
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "getCities: ERROR: " + e.getMessage());
            }

        }).start();

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

     */
}
