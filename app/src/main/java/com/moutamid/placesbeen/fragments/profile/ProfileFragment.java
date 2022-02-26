package com.moutamid.placesbeen.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.moutamid.placesbeen.databinding.FragmentHomeBinding;
import com.moutamid.placesbeen.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    public FragmentProfileBinding b;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = FragmentProfileBinding.inflate(inflater, container, false);

        return b.getRoot();

    }

}
