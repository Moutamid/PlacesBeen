package com.moutamid.placesbeen.fragments.save;

import static android.view.LayoutInflater.from;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.databinding.FragmentHomeBinding;
import com.moutamid.placesbeen.databinding.FragmentSaveBinding;

import java.util.ArrayList;

public class SaveFragment extends Fragment {
    public FragmentSaveBinding b;

    SaveController controller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = FragmentSaveBinding.inflate(inflater, container, false);

        controller = new SaveController(this);

        b.optionSaved.setOnClickListener(view -> {
            controller.changeDotTo(b.dotSaved, b.textViewSaved);
        });

        b.optionBeen.setOnClickListener(view -> {
            controller.changeDotTo(b.dotBeen, b.textViewBeen);
        });

        b.optionWantTo.setOnClickListener(view -> {
            controller.changeDotTo(b.dotWantTo, b.textViewWantTo);
        });

        b.savedRecyclerView.showShimmerAdapter();

        initRecyclerView();

        return b.getRoot();

    }

    private ArrayList<String> tasksArrayList = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;

    private void initRecyclerView() {

        conversationRecyclerView = b.savedRecyclerView;
        //conversationRecyclerView.addItemDecoration(new DividerItemDecoration(conversationRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new RecyclerViewAdapterMessages();
        //        LinearLayoutManager layoutManagerUserFriends = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        //    int numberOfColumns = 3;
        //int mNoOfColumns = calculateNoOfColumns(getApplicationContext(), 50);
        //  recyclerView.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        //linearLayoutManager.setReverseLayout(true);
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

    /*public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }*/

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

            //            holder.title.setText("");

        }

        @Override
        public int getItemCount() {
            //            if (tasksArrayList == null)
            return 10;
            //            return tasksArrayList.size();
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            //            TextView title;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                //                title = v.findViewById(R.id.titleTextview);

            }
        }

    }

}
