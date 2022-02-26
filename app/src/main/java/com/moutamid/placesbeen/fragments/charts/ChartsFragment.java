package com.moutamid.placesbeen.fragments.charts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.moutamid.placesbeen.databinding.FragmentChartsBinding;
import com.moutamid.placesbeen.databinding.FragmentHomeBinding;

public class ChartsFragment extends Fragment {
    public FragmentChartsBinding b;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = FragmentChartsBinding.inflate(inflater, container, false);

        return b.getRoot();

    }

}
