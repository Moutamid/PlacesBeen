package com.moutamid.placesbeen.activities.place;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dezlum.codelabs.getjson.GetJson;
import com.fxn.stash.Stash;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class PlaceController {
    private static final String TAG = "PlaceController";

    PlaceItemActivity activity;
    Context context;

    public PlaceController(PlaceItemActivity placeItemActivity) {
        this.activity = placeItemActivity;
        this.context = placeItemActivity.getApplicationContext();
    }

    public void saveUnSaveItem() {
        MainItemModel model = activity.mainItemModel;
        if (model.title.equals("nullnull")) {
            Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Stash.getBoolean(model.title, false)) {
            // IF ALREADY SAVED THEN REMOVE
            activity.b.saveBtnPlace.setImageResource(R.drawable.ic_unsave_24);
            YoYo.with(Techniques.Bounce).duration(700).playOn(activity.b.saveBtnPlace);
            Constants.databaseReference()
                    .child(Constants.auth().getUid())
                    .child(Constants.SAVED_ITEMS_PATH)
                    .child(model.title)
                    .removeValue();

            Stash.clear(model.title);
        } else {
            // IF NOT SAVED THEN SAVE
            activity.b.saveBtnPlace.setImageResource(R.drawable.ic_save_24);
            YoYo.with(Techniques.Bounce).duration(700).playOn(activity.b.saveBtnPlace);
            Constants.databaseReference()
                    .child(Constants.auth().getUid())
                    .child(Constants.SAVED_ITEMS_PATH)
                    .child(model.title)
                    .setValue(model);

            Stash.put(model.title, true);
        }
    }

    public void checkIsItemSaved() {
        if (Stash.getBoolean(activity.mainItemModel.title, false)) {
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

                activity.runOnUiThread(() -> activity.loadImages());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void getLatLng() {

        Log.d(TAG, "downloadJSON: ");

        new Thread(() -> {
            try {
                Log.d(TAG, "getLatLng: try {");
                String q = URLEncoder.encode(activity.mainItemModel.title, "utf-8");
                Log.d(TAG, "getLatLng: encoded");
                JSONObject jsonObject = new JSONObject(new GetJson().AsString(Constants.GET_POSITION_URL((q))));
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

                    activity.runOnUiThread(() -> activity.loadAddress());

                } else {
                    Log.d(TAG, "getLatLng: else ");
                }

            } catch (ExecutionException | InterruptedException e) {
                Log.d(TAG, "downloadJSON: error: " + e.getMessage());
                e.printStackTrace();
            } catch (JSONException e) {
                Log.d(TAG, "JSONException: error: " + e.getMessage());
                Log.d(TAG, "JSONException: error: " + e.toString());
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                Log.d(TAG, "getLatLng: error: " + e.getMessage());
                e.printStackTrace();
            }

        }).start();
    }

    public void checkBeenWantTo() {
        // IF USER BEEN
        if (Stash.getBoolean(activity.mainItemModel.title + Constants.BEEN_ITEMS_PATH, false)) {
            activity.b.beenCheckBoxPlace.setChecked(true);
        }
        // IF WANT TO SAVED
        if (Stash.getBoolean(activity.mainItemModel.title + Constants.WANT_TO_ITEMS_PATH, false)) {
            activity.b.wantToCheckBoxPlace.setChecked(true);
        }

    }
}