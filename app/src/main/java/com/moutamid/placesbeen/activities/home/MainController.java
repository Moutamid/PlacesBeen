package com.moutamid.placesbeen.activities.home;

import android.util.Log;

import com.dezlum.codelabs.getjson.GetJson;
import com.fxn.stash.Stash;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainController {
    private static final String TAG = "MainController";

    public static void fetchAllPolygonBoundaries() {
        new Thread(() -> {
            try {
                Log.d(TAG, "drawPolygon: try {");

                ArrayList<MainItemModel> CountryArrayList = Stash.getArrayList(Constants.PARAMS_Country, MainItemModel.class);

                for (int i = 0; i < CountryArrayList.size(); i++) {
                    String country = CountryArrayList.get(i).title;

                    String polyGonStr = Stash.getString(Constants.POLYGON + country, Constants.NULL);

                    if (polyGonStr.equals(Constants.NULL)) {
                        polyGonStr = new GetJson().AsString(Constants.GET_BOUNDARY_URL((country)));
                        Stash.put(Constants.POLYGON + country, polyGonStr);
                    }
                }

                Log.d(TAG, "drawPolygon: jsonArray done");
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e(TAG, "drawPolygon: ERROR: " + e.getMessage());
            } catch (ExecutionException e) {
                e.printStackTrace();
                Log.e(TAG, "drawPolygon: ERROR: " + e.getMessage());
            }
        }).start();
    }
}
