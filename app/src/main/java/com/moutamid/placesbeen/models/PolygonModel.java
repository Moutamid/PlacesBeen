package com.moutamid.placesbeen.models;

import com.google.android.gms.maps.model.Polygon;

public class PolygonModel {
    public String title;
    public Polygon polygon;

    public PolygonModel() {
    }

    public PolygonModel(String title, Polygon polygon) {
        this.title = title;
        this.polygon = polygon;
    }
}
