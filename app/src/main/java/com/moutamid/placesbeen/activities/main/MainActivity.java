package com.moutamid.placesbeen.activities.main;

import static android.view.LayoutInflater.from;

import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.placesbeen.R.color.lighterGrey;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.request.RequestOptions;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.fxn.stash.Stash;
import com.google.android.material.card.MaterialCardView;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.activities.RegistrationActivity;
import com.moutamid.placesbeen.databinding.ActivityMainBinding;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding b;
    MainController controller;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(b.getRoot());

        controller = new MainController(this);

        b.mainRecyclerView.showShimmerAdapter();

//        ContinentArrayList = (ArrayList<MainItemModel>) getIntent().getSerializableExtra(Constants.PARAMS_Continent);
//        CountryArrayList = (ArrayList<MainItemModel>) getIntent().getSerializableExtra(Constants.PARAMS_Country);
//        StatesArrayList = (ArrayList<MainItemModel>) getIntent().getSerializableExtra(Constants.PARAMS_States);
//        CityArrayList = (ArrayList<MainItemModel>) getIntent().getSerializableExtra(Constants.PARAMS_City);
//        CulturalSitesArrayList = (ArrayList<MainItemModel>) getIntent().getSerializableExtra(Constants.PARAMS_CulturalSites);
//        NationalParksArrayList = (ArrayList<MainItemModel>) getIntent().getSerializableExtra(Constants.PARAMS_NationalParks);
//        AirportsArrayList = (ArrayList<MainItemModel>) getIntent().getSerializableExtra(Constants.PARAMS_Airports);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ContinentArrayList = Stash.getArrayList(Constants.PARAMS_Continent, MainItemModel.class);
                CountryArrayList = Stash.getArrayList(Constants.PARAMS_Country, MainItemModel.class);
                StatesArrayList = Stash.getArrayList(Constants.PARAMS_States, MainItemModel.class);
                CityArrayList = Stash.getArrayList(Constants.PARAMS_City, MainItemModel.class);
                CulturalSitesArrayList = Stash.getArrayList(Constants.PARAMS_CulturalSites, MainItemModel.class);
                NationalParksArrayList = Stash.getArrayList(Constants.PARAMS_NationalParks, MainItemModel.class);
                AirportsArrayList = Stash.getArrayList(Constants.PARAMS_Airports, MainItemModel.class);

                mainItemModelArrayList = ContinentArrayList;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initRecyclerView();
                    }
                });
            }
        }).start();

        b.optionContinent.setOnClickListener(view -> {
            controller.changeDotTo(b.dotContinent);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = ContinentArrayList;
            initRecyclerView();
        });
        b.optionCountry.setOnClickListener(view -> {
            controller.changeDotTo(b.dotCountry);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = CountryArrayList;
            initRecyclerView();
        });
        b.optionStates.setOnClickListener(view -> {
            controller.changeDotTo(b.dotStates);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = StatesArrayList;
            initRecyclerView();
        });
        b.optionCity.setOnClickListener(view -> {
            controller.changeDotTo(b.dotCity);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = CityArrayList;
            initRecyclerView();
        });
        b.optionCulturalSites.setOnClickListener(view -> {
            controller.changeDotTo(b.dotCulturalSites);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = CulturalSitesArrayList;
            initRecyclerView();
        });
        b.optionNationalParks.setOnClickListener(view -> {
            controller.changeDotTo(b.dotNationalParks);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = NationalParksArrayList;
            initRecyclerView();
        });

        b.optionAirports.setOnClickListener(view -> {
            controller.changeDotTo(b.dotAirports);
            b.mainRecyclerView.showShimmerAdapter();

            mainItemModelArrayList = AirportsArrayList;
            initRecyclerView();
        });

    }

    public void initRecyclerView() {

        conversationRecyclerView = b.mainRecyclerView;
        adapter = new RecyclerViewAdapterMessages();
        linearLayoutManager = new LinearLayoutManager(this);
        conversationRecyclerView.setLayoutManager(linearLayoutManager);

        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);
        conversationRecyclerView.setItemViewCacheSize(10);

        b.mainRecyclerView.hideShimmerAdapter();

    }

    private class RecyclerViewAdapterMessages extends Adapter
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

            loadImage(holder.imageView, model.title, model.desc, holder.getAdapterPosition());

            holder.parenLayout.setOnClickListener(view -> {
                Toast.makeText(MainActivity.this, "" + model.url + holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
            });

        }

        String link;

        private void loadImage(ImageView view, String title, String desc, int adapterPosition) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {

                        link = mainItemModelArrayList.get(adapterPosition).getUrl();
                        if (link.equals(Constants.NULL)) {
                            link = controller.getImageUrl(title, desc);
                            mainItemModelArrayList.get(adapterPosition).setUrl(link);
                        }

                        runOnUiThread(() -> {
                            with(MainActivity.this)
                                    .asBitmap()
                                    .load(link)
                                    .apply(new RequestOptions()
                                            .placeholder(lighterGrey)
                                            .error(lighterGrey)
                                    )
                                    .diskCacheStrategy(DATA)
                                    .into(view);
                        });
                    } catch (Exception e) {
                        Log.e("TAG", "run: ERROR: " + e.getMessage());
                    }
                }
            }).start();
        }

        @Override
        public int getItemCount() {
            if (mainItemModelArrayList == null)
                return 0;
            return mainItemModelArrayList.size();
        }

        public class ViewHolderRightMessage extends ViewHolder {

            TextView title, desc, ratingText;
            AppCompatRatingBar ratingBar;
            ImageView imageView;
            MaterialCardView parenLayout;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                title = v.findViewById(R.id.title_main);
                imageView = v.findViewById(R.id.imageview_main);
                parenLayout = v.findViewById(R.id.parent_layout_main);
                desc = v.findViewById(R.id.desc_main);
                ratingBar = v.findViewById(R.id.ratingBarMain);
                ratingText = v.findViewById(R.id.ratingTextMain);

            }
        }

    }


}
