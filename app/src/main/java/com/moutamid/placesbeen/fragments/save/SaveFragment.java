package com.moutamid.placesbeen.fragments.save;

import static android.view.LayoutInflater.from;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fxn.stash.Stash;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.activities.place.PlaceItemActivity;
import com.moutamid.placesbeen.databinding.FragmentSaveBinding;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;
import com.moutamid.placesbeen.utils.Utils;

import java.util.ArrayList;
import java.util.Random;

public class SaveFragment extends Fragment {
    public FragmentSaveBinding b;

    SaveController controller;

    public ArrayList<MainItemModel> currentItemsList = new ArrayList<>();

    ArrayList<MainItemModel> savedArrayList = new ArrayList<>();
    ArrayList<MainItemModel> beenArrayList = new ArrayList<>();
    ArrayList<MainItemModel> wantToArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = FragmentSaveBinding.inflate(inflater, container, false);

        controller = new SaveController(this);

        b.optionSaved.setOnClickListener(view -> {
            controller.changeDotTo(b.dotSaved, b.textViewSaved);
            controller.ITEMS_PATH = Constants.SAVED_ITEMS_PATH;
            b.savedRecyclerView.showShimmerAdapter();
            currentItemsList = savedArrayList;
            initRecyclerView();
        });

        b.optionBeen.setOnClickListener(view -> {
            controller.changeDotTo(b.dotBeen, b.textViewBeen);
            controller.ITEMS_PATH = Constants.BEEN_ITEMS_PATH;
            b.savedRecyclerView.showShimmerAdapter();
            currentItemsList = beenArrayList;
            initRecyclerView();
        });

        b.optionWantTo.setOnClickListener(view -> {
            controller.changeDotTo(b.dotWantTo, b.textViewWantTo);
            controller.ITEMS_PATH = Constants.WANT_TO_ITEMS_PATH;
            b.savedRecyclerView.showShimmerAdapter();
            currentItemsList = wantToArrayList;
            initRecyclerView();
        });

        b.savedRecyclerView.showShimmerAdapter();

        controller.retrieveDatabaseItems();

        return b.getRoot();

    }

    private RecyclerView conversationRecyclerView;
    public RecyclerViewAdapterMessages adapter;

    public boolean isFirstTime = true;

    public void initRecyclerView() {

        if (isFirstTime) {
            currentItemsList = savedArrayList;
            isFirstTime = false;
        }

        conversationRecyclerView = b.savedRecyclerView;
        adapter = new RecyclerViewAdapterMessages();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);

        b.savedRecyclerView.hideShimmerAdapter();

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
            View view = from(parent.getContext()).inflate(R.layout.layout_item_saved, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position) {
            MainItemModel model = currentItemsList.get(holder.getAdapterPosition());

//            try {
            holder.title.setText(model.title);
            holder.desc.setText(model.desc.replaceAll("\\R+", " "));

            int nmbr = new Random().nextInt(2);
            nmbr += 4;
            holder.rating.setText(nmbr + "");

            Utils.loadImage(requireActivity(), holder.imageView, model.title, model.desc, false, true);

            holder.parentLayout.setOnClickListener(view -> {
                Stash.put(Constants.CURRENT_MODEL_CLASS, model);
                startActivity(new Intent(requireContext(), PlaceItemActivity.class));
            });
            /*} catch (Exception e) {
                Log.e("TAG", "onBindViewHolder: ERROR: " + e.getMessage());
            }*/
        }

        @Override
        public int getItemCount() {
            if (currentItemsList == null)
                return 0;
            return currentItemsList.size();
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            TextView title, desc, rating;
            ImageView imageView;
            LinearLayout parentLayout;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                title = v.findViewById(R.id.name_item_saved_fragment);
                desc = v.findViewById(R.id.desc_item_saved_fragment);
                rating = v.findViewById(R.id.rating_text_item_saved_fragment);
                imageView = v.findViewById(R.id.image_item_saved_fragment);
                parentLayout = v.findViewById(R.id.parent_layout_item_saved_fragment);

            }
        }

    }

}
