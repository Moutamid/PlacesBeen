package com.moutamid.placesbeen.onboard;

import android.app.Application;

import com.fxn.stash.Stash;
import com.moutamid.placesbeen.utils.Utils;

public class AppContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stash.init(this);
        Utils.init(this);
    }
}
