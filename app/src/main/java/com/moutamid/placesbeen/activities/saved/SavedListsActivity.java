package com.moutamid.placesbeen.activities.saved;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fxn.stash.Stash;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.databinding.ActivitySavedListsBinding;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SavedListsActivity extends AppCompatActivity {

    SavedListController controller;
    JSONObject jsonObject;
    public ArrayList<MainItemModel> CountryArrayList = new ArrayList<>();
    private ActivitySavedListsBinding b;
    public String CURRENT_PATH = Constants.BEEN_ITEMS_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        b = ActivitySavedListsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        controller = new SavedListController(this);
//        CURRENT_PATH = Stash.getString(Constants.CURRENT_PATH_FOR_SAVED_LIST, );

        new Thread(() -> {
            try {
                CountryArrayList = Stash.getArrayList(Constants.PARAMS_Country, MainItemModel.class);

                jsonObject = new JSONObject(controller.loadJSONFromAsset());

                runOnUiThread(() -> {
                    initSavedListRecyclerView();
                    initairportListRecyclerView();
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();

        // Declare in and out animations and load them using AnimationUtils class
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        // set the animation type to ViewSwitcher
        b.viewSwitcher.setInAnimation(in);
        b.viewSwitcher.setOutAnimation(out);

        b.switchRecyclerViewBtn.setOnClickListener(view -> {
            b.viewSwitcher.showNext();
        });

        b.toggleButtonSavedList.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.beenCheckBtn) {
                    CURRENT_PATH = Constants.BEEN_ITEMS_PATH;
                }
                if (checkedId == R.id.wantToCheckBtn) {
                    CURRENT_PATH = Constants.WANT_TO_ITEMS_PATH;
                }

                initSavedListRecyclerView();
                initairportListRecyclerView();
            }
        });

    }

    private RecyclerView savedListRecyclerView;
    private RecyclerViewAdapterMessages savedListAdapter;

    private void initSavedListRecyclerView() {
        savedListRecyclerView = b.savedlistrecyclerview;
        savedListRecyclerView.addItemDecoration(new DividerItemDecoration(savedListRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        savedListAdapter = new RecyclerViewAdapterMessages();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        savedListRecyclerView.setLayoutManager(linearLayoutManager);
        savedListRecyclerView.setHasFixedSize(true);
        savedListRecyclerView.setNestedScrollingEnabled(false);

        savedListRecyclerView.setAdapter(savedListAdapter);

        //    if (adapter.getItemCount() != 0) {

        //        noChatsLayout.setVisibility(View.GONE);
        //        chatsRecyclerView.setVisibility(View.VISIBLE);

        //    }

    }

    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_saved_list_item, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position) {
            MainItemModel model = CountryArrayList.get(holder.getAdapterPosition());
            holder.miniRecyclerView.setVisibility(View.GONE);
            controller.setLocked(holder.flag);

            controller.checkBeenStatus(holder.flag, model.title + model.desc);

            controller.loadFlagOnImage(model, holder.flag);

            holder.title.setText(model.title);

            holder.layout.setOnClickListener(view -> {
                if (holder.arrow.getRotation() == 180) {
                    holder.arrow.setRotation(270);
                } else {
                    holder.arrow.setRotation(180);
                }
                if (holder.miniRecyclerView.getVisibility() == View.VISIBLE) {
                    holder.miniRecyclerView.setVisibility(View.GONE);
                } else {
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray(model.title);
                        initSavedListMiniRecyclerView(model, jsonArray, holder.miniRecyclerView);
                        holder.miniRecyclerView.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            if (CountryArrayList == null)
                return 0;
            return CountryArrayList.size();
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            TextView title;
            ImageView flag, arrow;
            RelativeLayout layout;
            RecyclerView miniRecyclerView;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                title = v.findViewById(R.id.nameTextViewSavedlist);
                flag = v.findViewById(R.id.flagImageviewSavedlist);
                arrow = v.findViewById(R.id.arrowImgSavedList);
                layout = v.findViewById(R.id.parentLayoutSavedList);
                miniRecyclerView = v.findViewById(R.id.savedlistitemminiRecyclerview);

            }
        }

    }

    //---------------MINI RECYCLER VIEW-------------------------

    private RecyclerView savedListMiniRecyclerView;
    private RecyclerViewAdapterMiniList savedListMiniAdapter;

    private void initSavedListMiniRecyclerView(MainItemModel model, JSONArray jsonArray, RecyclerView miniRecyclerView) {

        savedListMiniRecyclerView = miniRecyclerView;
        savedListMiniRecyclerView.addItemDecoration(new DividerItemDecoration(savedListMiniRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        savedListMiniAdapter = new RecyclerViewAdapterMiniList(model.title, jsonArray);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        savedListMiniRecyclerView.setLayoutManager(linearLayoutManager);
        savedListMiniRecyclerView.setHasFixedSize(true);
        savedListMiniRecyclerView.setNestedScrollingEnabled(false);

        savedListMiniRecyclerView.setAdapter(savedListMiniAdapter);

        //    if (adapter.getItemCount() != 0) {

        //        noChatsLayout.setVisibility(View.GONE);
        //        chatsRecyclerView.setVisibility(View.VISIBLE);

        //    }

    }

    private class RecyclerViewAdapterMiniList extends RecyclerView.Adapter
            <RecyclerViewAdapterMiniList.ViewHolderMiniList> {

        public ArrayList<String> miniItemsArrayList = new ArrayList<>();
        String title;

        public RecyclerViewAdapterMiniList(String title, JSONArray jsonArray) {
            this.title = title;
            miniItemsArrayList.clear();

            for (int i = 0; i < Math.min(jsonArray.length(), 30); i++) {
                try {
                    String name = jsonArray.getString(i);
                    miniItemsArrayList.add(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // CHECKING IF CURRENT PATH IS BEEN THEN EXTRACT BEEN EXTRA LIST OTHERWISE WANT TO
            String EXTRA_LIST = Constants.EXTRA_LIST;
            if (CURRENT_PATH.equals(Constants.WANT_TO_ITEMS_PATH))
                EXTRA_LIST = Constants.EXTRA_LIST_WANT;

            // ADDING EXTRA CITIES NAMES INTO LIST WHICH WERE NOT ADDED JUST IN CASE
            ArrayList<String> extraCitiesList = Stash.getArrayList(title + EXTRA_LIST, String.class);
            miniItemsArrayList.addAll(extraCitiesList);

            // REMOVING DUPLICATES
            Set<String> set = new HashSet<>(miniItemsArrayList);
            miniItemsArrayList.clear();
            miniItemsArrayList.addAll(set);

        }

        @NonNull
        @Override
        public ViewHolderMiniList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_saved_list_item_mini, parent, false);
            return new ViewHolderMiniList(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderMiniList holder, int position) {
            controller.setLocked(holder.flag);

            MainItemModel model = new MainItemModel();
            model.title = title;
            model.desc = Constants.NULL;
            controller.loadFlagOnImage(model, holder.flag);

            controller.checkBeenStatus(holder.flag, miniItemsArrayList.get(holder.getAdapterPosition()) + title);

            holder.title.setText(miniItemsArrayList.get(holder.getAdapterPosition()));
        }

        @Override
        public int getItemCount() {
            if (miniItemsArrayList == null)
                return 0;
            return miniItemsArrayList.size();
        }

        public class ViewHolderMiniList extends RecyclerView.ViewHolder {

            TextView title;
            ImageView flag;
            RelativeLayout layout;

            public ViewHolderMiniList(@NonNull View v) {
                super(v);
                title = v.findViewById(R.id.nameTextViewSavedlistMini);
                flag = v.findViewById(R.id.flagImageviewSavedlistMini);
                layout = v.findViewById(R.id.parentLayoutSavedListMini);

            }
        }

    }

    //---------------AIRPORT RECYCLER VIEW---------------

    private RecyclerView airportListRecyclerView;
    private RecyclerViewAdapterairportList airportListAdapter;

    private void initairportListRecyclerView() {

        airportListRecyclerView = b.savedAirportListRecyclerView;
        airportListRecyclerView.addItemDecoration(new DividerItemDecoration(airportListRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        airportListAdapter = new RecyclerViewAdapterairportList();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        airportListRecyclerView.setLayoutManager(linearLayoutManager);
        airportListRecyclerView.setHasFixedSize(true);
        airportListRecyclerView.setNestedScrollingEnabled(false);

        airportListRecyclerView.setAdapter(airportListAdapter);

        //    if (adapter.getItemCount() != 0) {

        //        noChatsLayout.setVisibility(View.GONE);
        //        chatsRecyclerView.setVisibility(View.VISIBLE);

        //    }

    }

    private class RecyclerViewAdapterairportList extends RecyclerView.Adapter
            <RecyclerViewAdapterairportList.ViewHolderairportList> {

        public ArrayList<MainItemModel> airportListArrayList = Stash.getArrayList(Constants.PARAMS_Airports, MainItemModel.class);
        String title;

        public RecyclerViewAdapterairportList() {
        }

        @NonNull
        @Override
        public ViewHolderairportList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_saved_list_item_mini, parent, false);
            return new ViewHolderairportList(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderairportList holder, int position) {
            controller.setLocked(holder.flag);

            String title = airportListArrayList.get(holder.getAdapterPosition()).title + airportListArrayList.get(holder.getAdapterPosition()).desc;

            controller.loadFlagOnImage(airportListArrayList.get(holder.getAdapterPosition()), holder.flag);

            controller.checkBeenStatus(holder.flag, title);

            holder.title.setText(airportListArrayList.get(holder.getAdapterPosition()).title);
        }

        @Override
        public int getItemCount() {
            if (airportListArrayList == null)
                return 0;
            return airportListArrayList.size();
        }

        public class ViewHolderairportList extends RecyclerView.ViewHolder {

            TextView title;
            ImageView flag;
            RelativeLayout layout;

            public ViewHolderairportList(@NonNull View v) {
                super(v);
                title = v.findViewById(R.id.nameTextViewSavedlistMini);
                flag = v.findViewById(R.id.flagImageviewSavedlistMini);
                layout = v.findViewById(R.id.parentLayoutSavedListMini);

            }
        }

    }

}