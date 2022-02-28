package com.moutamid.placesbeen.fragments.home;

import static android.view.LayoutInflater.from;
import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.placesbeen.R.color.lighterGrey;
import static com.moutamid.placesbeen.utils.Utils.toast;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
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

    private ShimmerRecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;
    LinearLayoutManager linearLayoutManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = FragmentHomeBinding.inflate(inflater, container, false);

        controller = new HomeController(this);

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
        });
        b.optionCountry.setOnClickListener(view -> {
            controller.changeDotTo(b.dotCountry, b.textViewCountry);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = CountryArrayList;
            initRecyclerView();
            isAirport = false;
        });
        b.optionStates.setOnClickListener(view -> {
            controller.changeDotTo(b.dotStates, b.textViewStates);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = StatesArrayList;
            initRecyclerView();
            isAirport = false;
        });
        b.optionCity.setOnClickListener(view -> {
            controller.changeDotTo(b.dotCity, b.textViewCity);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = CityArrayList;
            initRecyclerView();
            isAirport = false;
        });
        b.optionCulturalSites.setOnClickListener(view -> {
            controller.changeDotTo(b.dotCulturalSites, b.textViewCulturalSites);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = CulturalSitesArrayList;
            initRecyclerView();
            isAirport = false;
        });
        b.optionNationalParks.setOnClickListener(view -> {
            controller.changeDotTo(b.dotNationalParks, b.textViewNationalParks);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = NationalParksArrayList;
            initRecyclerView();

            isAirport = false;
        });

        b.optionAirports.setOnClickListener(view -> {
            controller.changeDotTo(b.dotAirports, b.textViewAirports);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = AirportsArrayList;
            initRecyclerView();

            isAirport = true;
        });

        b.profileImageMain.setOnClickListener(view -> {
            ((MainActivity) getActivity()).openProfilePage();
        });

        return b.getRoot();

    }


    public boolean isAirport = false;

    public void initRecyclerView() {

        conversationRecyclerView = b.mainRecyclerView;
        adapter = new RecyclerViewAdapterMessages();
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
            Log.d("TAG", "onBindViewHolder: " + position);
            MainItemModel model = mainItemModelArrayList.get(holder.getAdapterPosition());

            holder.title.setText(model.title);
            holder.desc.setText(model.desc);

            int nmbr = new Random().nextInt(2);
            nmbr += 4;
            holder.ratingBar.setRating(nmbr);
            holder.ratingText.setText(nmbr + "");

            Utils.loadImage(requireActivity(), holder.imageView, model.title, model.desc, isAirport, false);

            holder.parenLayout.setOnClickListener(view -> {
//                toast("Please wait!");
//                Utils.getLatLng(requireActivity(), model.title);

                Stash.put(Constants.CURRENT_MODEL_CLASS, model);
                startActivity(new Intent(requireContext(), PlaceItemActivity.class));

            });

            controller.isSaved(model, holder.saveBtn);

            holder.saveBtn.setOnClickListener(view -> {
                controller.saveUnSaveItem(model, holder.saveBtn);
            });

        }

        @Override
        public int getItemCount() {
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

}
