package com.moutamid.placesbeen.activities.main;

import static android.view.LayoutInflater.from;

import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.placesbeen.R.color.lighterGrey;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.request.RequestOptions;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.databinding.ActivityMainBinding;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding b;
    MainController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        controller = new MainController(this);

        controller.getContinents(Constants.CONTINENTS_LIST);

        b.optionContinent.setOnClickListener(view -> {
            controller.changeDotTo(b.dotContinent);
            controller.getContinents(Constants.CONTINENTS_LIST);
        });
        b.optionCountry.setOnClickListener(view -> {
            controller.changeDotTo(b.dotCountry);
            controller.getCountries();
        });
        b.optionStates.setOnClickListener(view -> {
            controller.changeDotTo(b.dotStates);
            controller.getStates();
        });
        b.optionCity.setOnClickListener(view -> {
            controller.changeDotTo(b.dotCity);
            controller.getCities(Constants.WORLD_CITIES_JSON);
        });
        b.optionCulturalSites.setOnClickListener(view -> {
            controller.changeDotTo(b.dotCulturalSites);
            controller.getCulturalSites();
        });
        b.optionNationalParks.setOnClickListener(view -> {
            controller.changeDotTo(b.dotNationalParks);
            controller.getNationalParks();
        });

        b.optionAirports.setOnClickListener(view -> {
            controller.changeDotTo(b.dotAirports);
            controller.getAirports();
        });

    }

    public ArrayList<MainItemModel> mainItemModelArrayList = new ArrayList<>();

    private ShimmerRecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;
    LinearLayoutManager linearLayoutManager;

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
