package com.moutamid.placesbeen.activities.place;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.fxn.stash.Stash;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;

public class PlaceController {

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
        if (Stash.getBoolean(activity.mainItemModel.title, false)){
            activity.b.saveBtnPlace.setImageResource(R.drawable.ic_save_24);
        }
    }

}
