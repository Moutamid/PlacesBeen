package com.moutamid.placesbeen.onboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fxn.stash.Stash;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;
import com.moutamid.placesbeen.utils.Utils;

public class AppContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stash.init(this);
        Utils.init(this);

        if (Constants.auth().getCurrentUser() == null)
            return;

        Constants.databaseReference()
                .child(Constants.auth().getUid())
                .child(Constants.BEEN_ITEMS_PATH)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()) {
                            MainItemModel model = snapshot.getValue(MainItemModel.class);
                            Stash.put(model.title + model.desc + Constants.BEEN_ITEMS_PATH, true);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            MainItemModel model = snapshot.getValue(MainItemModel.class);
                            Stash.put(model.title + model.desc + Constants.BEEN_ITEMS_PATH, false);
                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        Constants.databaseReference()
                .child(Constants.auth().getUid())
                .child(Constants.WANT_TO_ITEMS_PATH)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()) {
                            MainItemModel model = snapshot.getValue(MainItemModel.class);
                            Stash.put(model.title + model.desc + Constants.WANT_TO_ITEMS_PATH, true);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            MainItemModel model = snapshot.getValue(MainItemModel.class);
                            Stash.put(model.title + model.desc + Constants.WANT_TO_ITEMS_PATH, false);
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
}
