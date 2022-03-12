package com.moutamid.placesbeen.models;

import com.google.android.gms.maps.model.Marker;

public class MarkerModel {
    public String title;
    public Marker marker;
    public boolean descNull;

    public MarkerModel(String title, Marker marker, boolean descNull) {
        this.title = title;
        this.marker = marker;
        this.descNull = descNull;
    }

    public MarkerModel() {
    }
}
