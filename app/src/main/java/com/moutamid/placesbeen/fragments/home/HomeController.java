package com.moutamid.placesbeen.fragments.home;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dezlum.codelabs.getjson.GetJson;
import com.fxn.stash.Stash;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.activities.home.MainActivity;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class HomeController {
    private static final String TAG = "FUCKK";
    HomeFragment mainActivity;
    Context context;

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

    public void saveUnSaveItem(MainItemModel model, ImageView saveBtn) {
        if (model.title.equals("nullnull")) {
            Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mainActivity.savedList.contains(model.title+model.desc)) {
//            if (Stash.getBoolean(model.title, false)) {
            // IF ALREADY SAVED THEN REMOVE
            saveBtn.setImageResource(R.drawable.ic_unsave_24);
            YoYo.with(Techniques.Bounce).duration(700).playOn(saveBtn);
            Constants.databaseReference()
                    .child(Constants.auth().getUid())
                    .child(Constants.SAVED_ITEMS_PATH)
                    .child(model.title+model.desc)
                    .removeValue();

            mainActivity.savedList.remove(model.title+model.desc);
            Stash.put(Constants.SAVED_LIST, mainActivity.savedList);
//                Stash.clear(model.title);
            // DECREASE 1 FROM CURRENT QUANTITY FOR CHARTS
            int count = Stash.getInt(mainActivity.CURRENT_TYPE + Constants.FOR_CHARTS, 0);
            if (count != 0) {
                count -= 1;
                Stash.put(mainActivity.CURRENT_TYPE + Constants.FOR_CHARTS, count);
            }
        } else {
            // IF NOT SAVED THEN SAVE
            saveBtn.setImageResource(R.drawable.ic_save_24);
            YoYo.with(Techniques.Bounce).duration(700).playOn(saveBtn);
            Constants.databaseReference()
                    .child(Constants.auth().getUid())
                    .child(Constants.SAVED_ITEMS_PATH)
                    .child(model.title+model.desc)
                    .setValue(model);

            mainActivity.savedList.add(model.title+model.desc);
            Stash.put(Constants.SAVED_LIST, mainActivity.savedList);
//                Stash.put(model.title, true);

            // INCREASE 1 FROM CURRENT QUANTITY FOR CHARTS
            int count = Stash.getInt(mainActivity.CURRENT_TYPE + Constants.FOR_CHARTS, 0);
            count += 1;
            Stash.put(mainActivity.CURRENT_TYPE + Constants.FOR_CHARTS, count);
        }
    }

    public void isSaved(MainItemModel model, ImageView saveBtn) {
        if (mainActivity.savedList.contains(model.title+model.desc)) {
            Log.d("MFUCKER", "isSaved: " + model.title);
            saveBtn.setImageResource(R.drawable.ic_save_24);
            mainActivity.CURRENT_COUNT++;
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

                                Stash.put(model.title+model.desc, true);
                                Log.d(TAG, "onChildAdded: " + model.title);
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

                                Stash.clear(model.title+model.desc);

                                Log.d(TAG, "onChildRemoved: " + model.title);
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

    public void getImageProfileUrl() {
        Constants.databaseReference()
                .child(Constants.auth().getUid())
                .child(Constants.PROFILE_URL)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Stash.put(Constants.PROFILE_URL, snapshot.getValue().toString());

                            if (mainActivity.isAdded())
                                Glide.with(mainActivity.requireActivity().getApplicationContext())
                                        .load(snapshot.getValue().toString())
                                        .apply(new RequestOptions()
                                                .placeholder(R.color.grey)
                                                .error(R.drawable.test)
                                        )
                                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                        .into(mainActivity.b.profileImageMain);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void getSavedList() {
        mainActivity.savedList = Stash.getArrayList(Constants.SAVED_LIST, String.class);
    }

    public void setvaluesOnTextviews() {
        mainActivity.b.textViewContinent.setText("Continents (" + Stash.getInt(Constants.PARAMS_Continent + Constants.FOR_CHARTS) + ")");
        mainActivity.b.textViewCountry.setText("Countries (" + Stash.getInt(Constants.PARAMS_Country + Constants.FOR_CHARTS) + ")");
        mainActivity.b.textViewStates.setText("States (" + Stash.getInt(Constants.PARAMS_States + Constants.FOR_CHARTS) + ")");
        mainActivity.b.textViewCity.setText("Cities (" + Stash.getInt(Constants.PARAMS_City + Constants.FOR_CHARTS) + ")");
        mainActivity.b.textViewCulturalSites.setText("Cultural Sites (" + Stash.getInt(Constants.PARAMS_CulturalSites + Constants.FOR_CHARTS) + ")");
        mainActivity.b.textViewNationalParks.setText("National Parks (" + Stash.getInt(Constants.PARAMS_NationalParks + Constants.FOR_CHARTS) + ")");
        mainActivity.b.textViewAirports.setText("Airports (" + Stash.getInt(Constants.PARAMS_Airports + Constants.FOR_CHARTS) + ")");
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
