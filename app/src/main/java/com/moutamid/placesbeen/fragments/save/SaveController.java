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
//                        ArrayList<MainItemModel> savedArrayList = new ArrayList<>();
//                        ArrayList<MainItemModel> beenArrayList = new ArrayList<>();
//                        ArrayList<MainItemModel> wantToArrayList = new ArrayList<>();

                        saveFragment.savedArrayList.clear();
                                saveFragment.beenArrayList.clear();
                        saveFragment.wantToArrayList.clear();

                        if (snapshot.exists()) {

                            if (snapshot.hasChild(Constants.SAVED_ITEMS_PATH)) {
                                for (DataSnapshot savedSnapShot : snapshot.child(Constants.SAVED_ITEMS_PATH).getChildren()) {
                                    MainItemModel model = savedSnapShot.getValue(MainItemModel.class);
                                    saveFragment.savedArrayList.add(model);
                                }
                            }
                            if (snapshot.hasChild(Constants.BEEN_ITEMS_PATH)) {
                                for (DataSnapshot savedSnapShot : snapshot.child(Constants.BEEN_ITEMS_PATH).getChildren()) {
                                    MainItemModel model = savedSnapShot.getValue(MainItemModel.class);
                                    saveFragment.beenArrayList.add(model);
                                }
                            }
                            if (snapshot.hasChild(Constants.WANT_TO_ITEMS_PATH)) {
                                for (DataSnapshot savedSnapShot : snapshot.child(Constants.WANT_TO_ITEMS_PATH).getChildren()) {
                                    MainItemModel model = savedSnapShot.getValue(MainItemModel.class);
                                    saveFragment.wantToArrayList.add(model);
                                }
                            }

                            saveFragment.initRecyclerView();

                        } else {
                            saveFragment.initRecyclerView();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(saveFragment.requireContext(), error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //TODO: MAKE A TIMER TASK THAT WILL RUN ALL THE TIME AND REFRESH THE HOME RECYCLERVIEW
    // BY CALLING NOTIFYADAPTERCHANGED() WHENEVER THE USER DELETES AN ITEM FROM THE SAVED FRAGMENT
    // STORE A VARIABLE IN STASH WHICH WILL CHANGED EVERYTIME USER DELETES SOMETHING

    public void changeDotTo(View dot, TextView textView) {
        Log.d(TAG, "changeDotTo: ");
        currentDot.setVisibility(View.GONE);
        currentDot = dot;
        currentDot.setVisibility(View.VISIBLE);

        currentTextView.setTextColor(context.getResources().getColor(R.color.darkGrey));
        currentTextView = textView;
        currentTextView.setTextColor(context.getResources().getColor(R.color.yellow));
    }


    public void deleteBtnClicked(String title) {
        Constants.databaseReference()
                .child(Constants.auth().getUid())
                .child(ITEMS_PATH)
                .child(title)
                .removeValue();

        Stash.clear(title);
    }
}
