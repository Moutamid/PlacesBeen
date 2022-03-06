package com.moutamid.placesbeen.fragments.save;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SaveController {

    private static final String TAG = "FUCKK";
    private SaveFragment saveFragment;
    private Context context;
    //    public String SELECTED_JSON = Constants.WORLD_CITIES_JSON;
    private View currentDot;
    private TextView currentTextView;

    public String ITEMS_PATH = Constants.SAVED_ITEMS_PATH;

    public SaveController(SaveFragment saveFragment) {
        this.saveFragment = saveFragment;
        this.context = saveFragment.requireContext();
        this.currentDot = saveFragment.b.dotSaved;
        this.currentTextView = saveFragment.b.textViewSaved;
    }

    public void retrieveDatabaseItems() {
        Constants.databaseReference()
                .child(Constants.auth().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        saveFragment.savedArrayList.clear();
                        saveFragment.beenArrayList.clear();
                        saveFragment.wantToArrayList.clear();

                        if (snapshot.exists()) {

                            if (snapshot.hasChild(Constants.SAVED_ITEMS_PATH)) {
                                saveFragment.b.textViewSaved.setText("Saved (" + snapshot.child(Constants.SAVED_ITEMS_PATH).getChildrenCount() + ")");
                                for (DataSnapshot savedSnapShot : snapshot.child(Constants.SAVED_ITEMS_PATH).getChildren()) {
                                    ArrayList<String> savedList = Stash.getArrayList(Constants.SAVED_LIST, String.class);
                                    MainItemModel model = savedSnapShot.getValue(MainItemModel.class);
                                    if (!savedList.contains(model.title)) {
                                        savedList.add(model.title);
                                        Stash.put(Constants.SAVED_LIST, savedList);
                                    }
                                    saveFragment.savedArrayList.add(model);
                                }
                            } else saveFragment.b.textViewSaved.setText("Saved");

                            if (snapshot.hasChild(Constants.BEEN_ITEMS_PATH)) {
                                saveFragment.b.textViewBeen.setText("Been (" + snapshot.child(Constants.BEEN_ITEMS_PATH).getChildrenCount() + ")");
                                for (DataSnapshot savedSnapShot : snapshot.child(Constants.BEEN_ITEMS_PATH).getChildren()) {
                                    MainItemModel model = savedSnapShot.getValue(MainItemModel.class);
                                    saveFragment.beenArrayList.add(model);
                                }
                            } else saveFragment.b.textViewBeen.setText("Been");

                            if (snapshot.hasChild(Constants.WANT_TO_ITEMS_PATH)) {
                                saveFragment.b.textViewWantTo.setText("Want to (" + snapshot.child(Constants.WANT_TO_ITEMS_PATH).getChildrenCount() + ")");
                                for (DataSnapshot savedSnapShot : snapshot.child(Constants.WANT_TO_ITEMS_PATH).getChildren()) {
                                    MainItemModel model = savedSnapShot.getValue(MainItemModel.class);
                                    saveFragment.wantToArrayList.add(model);
                                }
                            } else saveFragment.b.textViewWantTo.setText("Want to");

                        }

                        saveFragment.initRecyclerView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (saveFragment.isAdded())
                            Toast.makeText(saveFragment.requireContext(), error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
