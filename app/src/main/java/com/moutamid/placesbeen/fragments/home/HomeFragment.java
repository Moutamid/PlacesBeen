package com.moutamid.placesbeen.fragments.home;

import static android.view.LayoutInflater.from;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.fxn.stash.Stash;
import com.google.android.material.card.MaterialCardView;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.activities.home.MainActivity;
import com.moutamid.placesbeen.activities.place.PlaceItemActivity;
import com.moutamid.placesbeen.databinding.FragmentHomeBinding;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;
import com.moutamid.placesbeen.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class HomeFragment extends Fragment {
    public FragmentHomeBinding b;

    HomeController controller;

    public ArrayList<MainItemModel> mainItemModelArrayList = new ArrayList<>();

    public ArrayList<MainItemModel> ContinentArrayList = new ArrayList<>();
    public ArrayList<MainItemModel> CountryArrayList = new ArrayList<>();
    public ArrayList<MainItemModel> StatesArrayList = new ArrayList<>();
    public ArrayList<MainItemModel> CityArrayList = new ArrayList<>();
    public ArrayList<MainItemModel> CulturalSitesArrayList = new ArrayList<>();
    public ArrayList<MainItemModel> NationalParksArrayList = new ArrayList<>();
    public ArrayList<MainItemModel> AirportsArrayList = new ArrayList<>();

    public ArrayList<String> savedList = new ArrayList<>();

    private ShimmerRecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;
    LinearLayoutManager linearLayoutManager;

    public String CURRENT_TYPE = Constants.PARAMS_Continent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = FragmentHomeBinding.inflate(inflater, container, false);

        controller = new HomeController(this);

        controller.getSavedList();

        b.mainRecyclerView.showShimmerAdapter();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContinentArrayList = Stash.getArrayList(Constants.PARAMS_Continent, MainItemModel.class);
                CountryArrayList = Stash.getArrayList(Constants.PARAMS_Country, MainItemModel.class);
                StatesArrayList = Stash.getArrayList(Constants.PARAMS_States, MainItemModel.class);
                CityArrayList = Stash.getArrayList(Constants.PARAMS_City, MainItemModel.class);
                CulturalSitesArrayList = Stash.getArrayList(Constants.PARAMS_CulturalSites, MainItemModel.class);
                NationalParksArrayList = Stash.getArrayList(Constants.PARAMS_NationalParks, MainItemModel.class);
                Collections.reverse(NationalParksArrayList);
                AirportsArrayList = Stash.getArrayList(Constants.PARAMS_Airports, MainItemModel.class);

                mainItemModelArrayList = ContinentArrayList;

                controller.retrieveDatabaseItems();

                if (isAdded())
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initRecyclerView();
                        }
                    });
            }
        }).start();

        b.optionContinent.setOnClickListener(view -> {
            controller.changeDotTo(b.dotContinent, b.textViewContinent);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = ContinentArrayList;
            initRecyclerView();
            isAirport = false;
            CURRENT_TYPE = Constants.PARAMS_Continent;
        });
        b.optionCountry.setOnClickListener(view -> {
            controller.changeDotTo(b.dotCountry, b.textViewCountry);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = CountryArrayList;
            initRecyclerView();
            isAirport = false;
            CURRENT_TYPE = Constants.PARAMS_Country;
        });
        b.optionStates.setOnClickListener(view -> {
            controller.changeDotTo(b.dotStates, b.textViewStates);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = StatesArrayList;
            initRecyclerView();
            isAirport = false;
            CURRENT_TYPE = Constants.PARAMS_States;
        });
        b.optionCity.setOnClickListener(view -> {
            controller.changeDotTo(b.dotCity, b.textViewCity);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = CityArrayList;
            initRecyclerView();
            isAirport = false;
            CURRENT_TYPE = Constants.PARAMS_City;
        });
        b.optionCulturalSites.setOnClickListener(view -> {
            controller.changeDotTo(b.dotCulturalSites, b.textViewCulturalSites);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = CulturalSitesArrayList;
            initRecyclerView();
            isAirport = false;
            CURRENT_TYPE = Constants.PARAMS_CulturalSites;
        });
        b.optionNationalParks.setOnClickListener(view -> {
            controller.changeDotTo(b.dotNationalParks, b.textViewNationalParks);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = NationalParksArrayList;
            initRecyclerView();

            isAirport = false;
            CURRENT_TYPE = Constants.PARAMS_NationalParks;
        });

        b.optionAirports.setOnClickListener(view -> {
            controller.changeDotTo(b.dotAirports, b.textViewAirports);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = AirportsArrayList;
            initRecyclerView();

            isAirport = true;
            CURRENT_TYPE = Constants.PARAMS_Airports;
        });

        b.profileImageMain.setOnClickListener(view -> {
            ((MainActivity) getActivity()).openProfilePage();
        });

        controller.getImageProfileUrl();

        return b.getRoot();

    }


    public boolean isAirport = false;

    public void initRecyclerView() {

        conversationRecyclerView = b.mainRecyclerView;
        adapter = new RecyclerViewAdapterMessages();
        if (isAdded())
        linearLayoutManager = new LinearLayoutManager(requireContext());
        conversationRecyclerView.setLayoutManager(linearLayoutManager);

        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);
        conversationRecyclerView.setItemViewCacheSize(10);

        b.mainRecyclerView.hideShimmerAdapter();

    }

    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = from(parent.getContext()).inflate(R.layout.layout_item_main, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position) {
            MainItemModel model = mainItemModelArrayList.get(holder.getAdapterPosition());

            holder.title.setText(model.title);
            holder.desc.setText(model.desc);

            int nmbr = new Random().nextInt(2);
            nmbr += 4;
            holder.ratingBar.setRating(nmbr);
            holder.ratingText.setText(nmbr + "");

                if (isAdded())
            Utils.loadImage(requireActivity(), holder.imageView, model.title, model.desc, isAirport, false);

            holder.parenLayout.setOnClickListener(view -> {
                Stash.put(Constants.CURRENT_MODEL_CLASS, model);
                if (isAdded())
                startActivity(new Intent(requireContext(), PlaceItemActivity.class));
            });

            controller.isSaved(model, holder.saveBtn);

            holder.saveBtn.setOnClickListener(view -> {
                controller.saveUnSaveItem(model, holder.saveBtn);
            });
        }

        @Override
        public int getItemCount() {
            Log.d("fuckher", "getItemCount: " + CURRENT_TYPE + ": " + mainItemModelArrayList.size());
            if (mainItemModelArrayList == null)
                return 0;
            return mainItemModelArrayList.size();
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            TextView title, desc, ratingText;
            AppCompatRatingBar ratingBar;
            ImageView imageView, saveBtn;
            MaterialCardView parenLayout;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                title = v.findViewById(R.id.title_main);
                saveBtn = v.findViewById(R.id.saveBtn_item_main);
                imageView = v.findViewById(R.id.imageview_main);
                parenLayout = v.findViewById(R.id.parent_layout_main);
                desc = v.findViewById(R.id.desc_main);
                ratingBar = v.findViewById(R.id.ratingBarMain);
                ratingText = v.findViewById(R.id.ratingTextMain);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            b.mainRecyclerView.showShimmerAdapter();
            initRecyclerView();
        }
    }
}