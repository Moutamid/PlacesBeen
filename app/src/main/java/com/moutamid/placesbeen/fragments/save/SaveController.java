package com.moutamid.placesbeen.fragments.save;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.moutamid.placesbeen.R;

public class SaveController {

    private static final String TAG = "FUCKK";
    private SaveFragment saveFragment;
    private Context context;
    //    public String SELECTED_JSON = Constants.WORLD_CITIES_JSON;
    private View currentDot;
    private TextView currentTextView;

    public SaveController(SaveFragment saveFragment) {
        this.saveFragment = saveFragment;
        this.context = saveFragment.requireContext();
        this.currentDot = saveFragment.b.dotSaved;
        this.currentTextView = saveFragment.b.textViewSaved;
    }


    public void changeDotTo(View dot, TextView textView) {
        Log.d(TAG, "changeDotTo: ");
        currentDot.setVisibility(View.GONE);
        currentDot = dot;
        currentDot.setVisibility(View.VISIBLE);

        currentTextView.setTextColor(context.getResources().getColor(R.color.darkGrey));
        currentTextView = textView;
        currentTextView.setTextColor(context.getResources().getColor(R.color.yellow));
    }

}
