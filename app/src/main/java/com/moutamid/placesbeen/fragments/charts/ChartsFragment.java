package com.moutamid.placesbeen.fragments.charts;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.fxn.stash.Stash;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.moutamid.placesbeen.databinding.FragmentChartsBinding;
import com.moutamid.placesbeen.utils.Constants;

public class ChartsFragment extends Fragment {
    public FragmentChartsBinding b;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = FragmentChartsBinding.inflate(inflater, container, false);

        refreshArcs();

        return b.getRoot();
    }

    public void refreshArcs(){
        initArcViews(b.continentsArcView, 6, Stash.getInt(Constants.PARAMS_Continent + Constants.FOR_CHARTS));
        initArcViews(b.countriesArcView, 239, Stash.getInt(Constants.PARAMS_Country + Constants.FOR_CHARTS));
        initArcViews(b.statesArcView, 239, Stash.getInt(Constants.PARAMS_States + Constants.FOR_CHARTS));
        initArcViews(b.citiesArcView, 41001, Stash.getInt(Constants.PARAMS_City + Constants.FOR_CHARTS));
        initArcViews(b.culturalSitesArcView, 1155, Stash.getInt(Constants.PARAMS_CulturalSites + Constants.FOR_CHARTS));
        initArcViews(b.nationalParksArcView, 1155, Stash.getInt(Constants.PARAMS_NationalParks + Constants.FOR_CHARTS));
        initArcViews(b.airportsArcView, 57421, Stash.getInt(Constants.PARAMS_Airports + Constants.FOR_CHARTS));

    }

    private void initArcViews(DecoView arcView, int totalValue, int currentValue) {
        // Create background track
        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 235, 235, 235))
                .setRange(0, 100, 100)
                .setLineWidth(32f)
                .build());

        //Create data series track
        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 55, 0, 179))
                .setRange(0, totalValue, currentValue)
                .setLineWidth(32f)
                .build();

        int series1Index = arcView.addSeries(seriesItem1);
    }
}