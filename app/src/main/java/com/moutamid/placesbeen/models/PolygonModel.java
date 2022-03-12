package com.moutamid.placesbeen.models;

import com.google.android.gms.maps.model.Polygon;

public class PolygonModel {
    public String title;
    public Polygon polygon;
    public boolean descNull;

    public PolygonModel(String title, Polygon polygon, boolean descNull) {
        this.title = title;
        this.polygon = polygon;
        this.descNull = descNull;
    }

    public PolygonModel() {
    }

}
