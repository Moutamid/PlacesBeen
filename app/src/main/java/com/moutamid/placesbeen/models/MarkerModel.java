package com.moutamid.placesbeen.models;

import com.google.android.gms.maps.model.Marker;

public class MarkerModel {
    public String title;
    public Marker marker;

    public MarkerModel(String title, Marker marker) {
        this.title = title;
        this.marker = marker;
    }

    public MarkerModel() {
    }
}
