package com.moutamid.placesbeen.fragments.save;

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
//    private View currentDot;
//    private TextView currentTextView;

//    public String ITEMS_PATH = Constants.SAVED_ITEMS_PATH;

    public SaveController(SaveFragment saveFragment) {
        this.saveFragment = saveFragment;
        this.context = saveFragment.requireContext();
//        this.currentDot = saveFragment.b.dotSaved;
//        this.currentTextView = saveFragment.b.textViewSaved;
    }

/*
    public void changeDotTo(View dot, TextView textView) {
        Log.d(TAG, "changeDotTo: ");
        currentDot.setVisibility(View.GONE);
        currentDot = dot;
        currentDot.setVisibility(View.VISIBLE);

        currentTextView.setTextColor(context.getResources().getColor(R.color.darkGrey));
        currentTextView = textView;
        currentTextView.setTextColor(context.getResources().getColor(R.color.yellow));
    }
*/
}
