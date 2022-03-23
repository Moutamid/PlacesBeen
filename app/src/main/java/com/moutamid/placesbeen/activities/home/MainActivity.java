package com.moutamid.placesbeen.activities.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.fxn.stash.Stash;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.databinding.ActivityMainBinding;
import com.moutamid.placesbeen.fragments.charts.ChartsFragment;
import com.moutamid.placesbeen.fragments.home.HomeFragment;
import com.moutamid.placesbeen.fragments.profile.ProfileFragment;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.models.MarkerModel;
import com.moutamid.placesbeen.utils.Constants;
import com.moutamid.placesbeen.utils.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public ActivityMainBinding b;

    private ViewPagerFragmentAdapter viewPagerFragmentAdapter;
    private ViewPager viewPager;

    MainController controller;

    public SupportMapFragment mapFragment;

    ArrayList<MainItemModel> savedArrayListCities = new ArrayList<>();
    ArrayList<MainItemModel> beenArrayListCities = new ArrayList<>();
    ArrayList<MainItemModel> wantToArrayListCities = new ArrayList<>();

    ArrayList<MainItemModel> savedArrayListCountries = new ArrayList<>();
    ArrayList<MainItemModel> beenArrayListCountries = new ArrayList<>();
    ArrayList<MainItemModel> wantToArrayListCountries = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(b.getRoot());

        controller = new MainController(this);

        viewPager = findViewById(R.id.main_view_pager);

        viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());

        MainController.fetchAllPolygonBoundaries();
        controller.fetchAllLatLngsOfCities();

        // Setting up the view Pager
        setupViewPager(viewPager);

        currentDot = b.homeDotBtnNav;
        currentBtn = b.homeBtnNavMain;

        b.homeLayoutMain.setOnClickListener(view -> {
            changeNavTo(b.homeDotBtnNav, b.homeBtnNavMain, R.drawable.ic_selected_home_24);
            viewPager.setVisibility(View.GONE);
        });
        b.chartsLayoutMain.setOnClickListener(view -> {
            viewPager.setVisibility(View.VISIBLE);
            changeNavTo(b.chartsDotBtnNav, b.chartsBtnNavMain, R.drawable.ic_charts_selected_24);

            viewPager.setCurrentItem(0, true);
        });
        b.saveLayoutMain.setOnClickListener(view -> {
            viewPager.setVisibility(View.VISIBLE);
            changeNavTo(b.saveDotBtnNav, b.saveBtnNavMain, R.drawable.ic_selected_map_24);
            viewPager.setCurrentItem(1, true);

        });
        b.profileLayoutMain.setOnClickListener(view -> {
            viewPager.setVisibility(View.VISIBLE);
            changeNavTo(b.profileDotBtnNav, b.profileBtnNavMain, R.drawable.ic_profile_selected_24);
            viewPager.setCurrentItem(2, true);
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapSaveFragment);

        controller.initMaps();

        controller.retrieveDatabaseItems();

        b.citiesBtnForMaps.setOnClickListener(view -> {
            b.searchEtLayoutMain.setVisibility(View.GONE);
            b.searchBtnLayout.setVisibility(View.VISIBLE);

            b.citiesBtnForMaps.setBackgroundColor(getResources().getColor(R.color.yellow));
            b.citiesBtnForMaps.setTextColor(getResources().getColor(R.color.white));

            b.countryBtnForMaps.setBackgroundColor(getResources().getColor(R.color.white));
            b.countryBtnForMaps.setTextColor(getResources().getColor(R.color.yellow));
            b.countryBtnForMaps.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#3700B3")));

            for (int i = 0; i < controller.markers.size(); i++) {
                // IF DESC IS NOT NULL THEN ITS A CITY
                if (!controller.markers.get(i).descNull) {
                    controller.markers.get(i).marker.setVisible(true);
                } else controller.markers.get(i).marker.setVisible(false);
            }
            for (int i = 0; i < controller.polygonModelArrayList.size(); i++) {
                if (!controller.polygonModelArrayList.get(i).descNull) {
                    controller.polygonModelArrayList.get(i).polygon.setVisible(true);
                } else controller.polygonModelArrayList.get(i).polygon.setVisible(false);
            }
        });

        b.countryBtnForMaps.setOnClickListener(view -> {
            b.searchEtLayoutMain.setVisibility(View.GONE);
            b.searchBtnLayout.setVisibility(View.VISIBLE);

            b.countryBtnForMaps.setBackgroundColor(getResources().getColor(R.color.yellow));
            b.countryBtnForMaps.setTextColor(getResources().getColor(R.color.white));

            b.citiesBtnForMaps.setBackgroundColor(getResources().getColor(R.color.white));
            b.citiesBtnForMaps.setTextColor(getResources().getColor(R.color.yellow));
            b.citiesBtnForMaps.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#3700B3")));

            if (controller.markers.size() > 0)
                for (int i = 0; i < controller.markers.size(); i++) {
                    // IF DESC IS NULL THEN ITS A COUNTRY
                    if (controller.markers.get(i).descNull) {
                        controller.markers.get(i).marker.setVisible(true);
                    } else controller.markers.get(i).marker.setVisible(false);
                }

            if (controller.polygonModelArrayList.size() > 0)
                for (int i = 0; i < controller.polygonModelArrayList.size(); i++) {
                    if (controller.polygonModelArrayList.get(i).descNull) {
                        controller.polygonModelArrayList.get(i).polygon.setVisible(true);
                    } else controller.polygonModelArrayList.get(i).polygon.setVisible(false);
                }
        });

        String PARAMS = getIntent().getStringExtra(Constants.PARAMS);
        if (PARAMS != null) {
            if (PARAMS.equals(Constants.PARAMS_CulturalSites)) {
                viewPager.setVisibility(View.VISIBLE);
                changeNavTo(b.saveDotBtnNav, b.saveBtnNavMain, R.drawable.ic_selected_map_24);
                viewPager.setCurrentItem(1, true);
//                HomeFragment fragment = (HomeFragment) viewPagerFragmentAdapter.getItem(1);
//                fragment.triggerCulturalClick();
            }
            if (PARAMS.equals(Constants.PARAMS_Airports)) {
                viewPager.setVisibility(View.VISIBLE);
                changeNavTo(b.saveDotBtnNav, b.saveBtnNavMain, R.drawable.ic_selected_map_24);
                viewPager.setCurrentItem(1, true);
//                HomeFragment fragment = (HomeFragment) viewPagerFragmentAdapter.getItem(1);
//                fragment.triggerAirportClick();
            }
            if (PARAMS.equals(Constants.PARAMS_Continent)) {
                viewPager.setVisibility(View.VISIBLE);
                changeNavTo(b.saveDotBtnNav, b.saveBtnNavMain, R.drawable.ic_selected_map_24);
                viewPager.setCurrentItem(1, true);
            }
            if (PARAMS.equals(Constants.PARAMS_CHARTS)) {
                viewPager.setVisibility(View.VISIBLE);
                changeNavTo(b.chartsDotBtnNav, b.chartsBtnNavMain, R.drawable.ic_charts_selected_24);
                viewPager.setCurrentItem(0, true);
            }
            if (PARAMS.equals(Constants.PARAMS_PROFILE)) {
                viewPager.setVisibility(View.VISIBLE);
                changeNavTo(b.profileDotBtnNav, b.profileBtnNavMain, R.drawable.ic_profile_selected_24);
                viewPager.setCurrentItem(2, true);
            }
        }
        b.backBtnMain.setOnClickListener(view -> {
            finish();
        });

        b.searchBtnMain.setOnClickListener(view -> {
            b.searchBtnLayout.setVisibility(View.GONE);
            b.searchEtLayoutMain.setVisibility(View.VISIBLE);
        });

        b.backBtnSearchEt.setOnClickListener(view -> {
            b.searchEtLayoutMain.setVisibility(View.GONE);
            b.searchBtnLayout.setVisibility(View.VISIBLE);
        });

        b.searchQueryBtnMain.setOnClickListener(view -> {
            if (adapter != null) {
                b.searchProgressBarMain.setVisibility(View.VISIBLE);
                adapter.performFiltering(b.searchEtMain.getText().toString().trim());
//                new PerformFiltering(b.searchEtMain.getText().toString().trim()).execute();
//                adapter.getFilter().filter(b.searchEtMain.getText().toString().trim());
            }
        });

        /*b.searchEtMain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                b.searchProgressBarMain.setVisibility(View.VISIBLE);
                adapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/

        controller.retrieveSearchListItems();

    }

    public ArrayList<MainItemModel> mainItemModelArrayList = new ArrayList<>();
    public ArrayList<MainItemModel> mainItemModelArrayListAll = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;

    public void initRecyclerView() {
        conversationRecyclerView = b.searchRecyclerViewMain;
        conversationRecyclerView.addItemDecoration(new DividerItemDecoration(conversationRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new RecyclerViewAdapterMessages();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);

    }

    /*public class PerformFiltering extends AsyncTask<Void, Void, Void> {
        String key;

        public PerformFiltering(String key) {
            this.key = key;
        }

        @Override
        protected Void doInBackground(Void... voids) {


            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);


        }
    }*/

    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {// implements Filterable {

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_item, parent, false);
            return new ViewHolderRightMessage(view);
        }

        boolean shouldColor = false;

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position) {
            MainItemModel model = mainItemModelArrayList.get(holder.getAdapterPosition());

            holder.title.setText(model.title);

            if (savedList.contains(model.title)) {
                holder.title.setTextColor(getResources().getColor(R.color.yellow));
                holder.saveCB.setChecked(true);
            }

            // IF USER BEEN
            if (Stash.getBoolean(model.title + Constants.BEEN_ITEMS_PATH, false)) {
                holder.title.setTextColor(getResources().getColor(R.color.yellow2));
                holder.beenCB.setChecked(true);
            }
            // IF WANT TO SAVED
            if (Stash.getBoolean(model.title + Constants.WANT_TO_ITEMS_PATH, false)) {
                holder.title.setTextColor(getResources().getColor(R.color.red));
                holder.wantToCB.setChecked(true);
            }

            holder.saveCB.setOnCheckedChangeListener((compoundButton, b1) -> {
                if (b1) {
                    holder.title.setTextColor(getResources().getColor(R.color.yellow));
                } else {
                    holder.title.setTextColor(getResources().getColor(R.color.default_text_color));
                }
                saveUnSaveItem(model, holder.saveCB);
            });

            holder.beenCB.setOnCheckedChangeListener((compoundButton, b1) -> {
                Utils.changeChartsValue(model.title, b1);
                if (b1) {
                    holder.title.setTextColor(getResources().getColor(R.color.yellow2));
                } else {
                    holder.title.setTextColor(getResources().getColor(R.color.default_text_color));
                }
                triggerCheckBox(model, b1, Constants.BEEN_ITEMS_PATH);
            });

            holder.wantToCB.setOnCheckedChangeListener((compoundButton, b1) -> {
                if (b1) {
                    holder.title.setTextColor(getResources().getColor(R.color.red));
                } else {
                    holder.title.setTextColor(getResources().getColor(R.color.default_text_color));
                }
                triggerCheckBox(model, b1, Constants.WANT_TO_ITEMS_PATH);
            });

            holder.parentLayout.setOnClickListener(view -> {
                b.searchEtLayoutMain.setVisibility(View.GONE);
                b.searchBtnLayout.setVisibility(View.VISIBLE);

                addMarkerOnMaps(model);
            });

        }

        ProgressDialog progressDialog;

        private void addMarkerOnMaps(MainItemModel model) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            new Thread(() -> {
                Log.d(TAG, "addMarkerOnMaps: ");
                double lat;
                double lng;

                if (model.lat.equals(Constants.NULL)) {
                    lat = controller.getLat(model.title);
                    lng = controller.LONG;
                } else {
                    lat = Double.parseDouble(model.lat);
                    lng = Double.parseDouble(model.lng);
                }

                LatLng sydney = new LatLng(lat, lng);
//                if (saveFragment.isAdded())
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    mMap.addMarker(new MarkerOptions().position(sydney)
                            .title(model.title));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(6.0f));

                });

            }).start();
        }

        public void triggerCheckBox(MainItemModel mainItemModel, boolean b, String itemsPath) {
            Stash.put(mainItemModel.title + itemsPath, b);

            if (b) {
                Constants.databaseReference()
                        .child(Constants.auth().getUid())
                        .child(itemsPath)
                        .child(mainItemModel.title)
                        .setValue(mainItemModel);
            } else {
                Constants.databaseReference()
                        .child(Constants.auth().getUid())
                        .child(itemsPath)
                        .child(mainItemModel.title)
                        .removeValue();
            }
        }

        ArrayList<String> savedList = Stash.getArrayList(Constants.SAVED_LIST, String.class);

        public void saveUnSaveItem(MainItemModel model, CheckBox checkBox) {
            if (savedList.contains(model.title)) {

                // IF ALREADY SAVED THEN REMOVE
                checkBox.setChecked(false);
                Constants.databaseReference()
                        .child(Constants.auth().getUid())
                        .child(Constants.SAVED_ITEMS_PATH)
                        .child(model.title)
                        .removeValue();

                savedList.remove(model.title);
                Stash.put(Constants.SAVED_LIST, savedList);
            } else {
                // IF NOT SAVED THEN SAVE
                checkBox.setChecked(true);
                Constants.databaseReference()
                        .child(Constants.auth().getUid())
                        .child(Constants.SAVED_ITEMS_PATH)
                        .child(model.title)
                        .setValue(model);

                savedList.add(model.title);
                Stash.put(Constants.SAVED_LIST, savedList);
            }
        }

        @Override
        public int getItemCount() {
            if (mainItemModelArrayList == null)
                return 0;
            return mainItemModelArrayList.size();
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            TextView title;
            CheckBox saveCB, beenCB, wantToCB;
            RelativeLayout parentLayout;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                title = v.findViewById(R.id.nameTextViewSearchItem);
                saveCB = v.findViewById(R.id.saveCheckBoxSearchItem);
                beenCB = v.findViewById(R.id.beenCheckBoxSearchItem);
                wantToCB = v.findViewById(R.id.wantToCheckBoxSearchItem);
                parentLayout = v.findViewById(R.id.parentLayoutSearchItem);

            }
        }

        private void performFiltering(String key) {
            new Thread(() -> {
                if (key.isEmpty()) {
                    mainItemModelArrayList = mainItemModelArrayListAll;
                } else {
                    ArrayList<MainItemModel> filtered = new ArrayList<>();

                    for (MainItemModel model : mainItemModelArrayListAll) {
                        if (model.title.toLowerCase().contains(key.toLowerCase())) {
                            filtered.add(model);
                        }
//                        else if (model.desc.toLowerCase().contains(key.toLowerCase())) {
//                            filtered.add(model);
//                        }
//                            else if (model.getSongYTUrl().toLowerCase().contains(key.toLowerCase())) {
//                                filtered.add(model);
//                            }
                    }

                    mainItemModelArrayList.clear();
                    mainItemModelArrayList = filtered;
                }
                runOnUiThread(() -> {
//                    initRecyclerView();
                    b.searchProgressBarMain.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                });

            }).start();
        }

        /*public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String key = charSequence.toString();
                    if (key.isEmpty()) {
                        mainItemModelArrayList = mainItemModelArrayListAll;
                    } else {
                        ArrayList<MainItemModel> filtered = new ArrayList<>();

                        for (MainItemModel model : mainItemModelArrayListAll) {
                            if (model.title.toLowerCase().contains(key.toLowerCase())) {
                                filtered.add(model);
                            } else if (model.desc.toLowerCase().contains(key.toLowerCase())) {
                                filtered.add(model);
                            }
//                            else if (model.getSongYTUrl().toLowerCase().contains(key.toLowerCase())) {
//                                filtered.add(model);
//                            }
                        }

                        mainItemModelArrayList.clear();
                        mainItemModelArrayList = filtered;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mainItemModelArrayList;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    b.searchProgressBarMain.setVisibility(View.GONE);
                    mainItemModelArrayList = (ArrayList<MainItemModel>) filterResults.values;
                    adapter.notifyDataSetChanged();
                }
            };
        }*/
    }

    boolean IS_HIDDEN = false;

    /*private void showMainLayout() {
        IS_HIDDEN = false;
        b.mainLayout.animate().alpha(1.0f).translationY(0).setDuration(700).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                b.mainLayout.setVisibility(View.VISIBLE);
                b.mapsLayout.animate().alpha(1.0f).translationY(b.mainLayout.getHeight()).setDuration(700).start();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                b.mapsLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();
    }

    private void hideMainLayout() {
        IS_HIDDEN = true;
        b.mainLayout.animate().alpha(0.0f).translationY(-b.mainLayout.getHeight()).setDuration(700).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                b.mapsLayout.setVisibility(View.VISIBLE);
                b.mapsLayout.animate().alpha(1.0f).translationY(0).setDuration(700).start();
//                    b.mapsLayout.animate().alpha(1.0f).translationY(b.mainLayout.getHeight()).start();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                b.mainLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();
    }*/

    public GoogleMap mMap;

    private View currentDot;
    private ImageView currentBtn;

    public void openProfilePage() {
        changeNavTo(b.profileDotBtnNav, b.profileBtnNavMain, R.drawable.ic_profile_selected_24);
        viewPager.setCurrentItem(2, true);
    }

    private void setupViewPager(ViewPager viewPager) {
        // Adding Fragments to Adapter
//        adapter.addFragment(new SaveFragment());
        viewPagerFragmentAdapter.addFragment(new ChartsFragment());
        viewPagerFragmentAdapter.addFragment(new HomeFragment());
        viewPagerFragmentAdapter.addFragment(new ProfileFragment());

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(viewPagerFragmentAdapter);

        Log.d(TAG, "setupViewPager: adapter attached");

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
//                if (position == 0) {
//                    changeNavTo(b.homeDotBtnNav, b.homeBtnNavMain, R.drawable.ic_selected_home_24);
//                } else
                if (position == 0) {
                    ChartsFragment fragment = (ChartsFragment) viewPagerFragmentAdapter.getItem(0);
                    fragment.refreshArcs();
                    changeNavTo(b.chartsDotBtnNav, b.chartsBtnNavMain, R.drawable.ic_charts_selected_24);
                } else if (position == 1) {
                    changeNavTo(b.saveDotBtnNav, b.saveBtnNavMain, R.drawable.ic_selected_map_24);
                } else if (position == 2) {
                    ProfileFragment fragment = (ProfileFragment) viewPagerFragmentAdapter.getItem(2);
                    fragment.refreshData();
                    changeNavTo(b.profileDotBtnNav, b.profileBtnNavMain, R.drawable.ic_profile_selected_24);
                }
            }

            int lastPosition = 0;

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    private void changeNavTo(View dot, ImageView btn, int drawable) {

        currentDot.setVisibility(View.GONE);
        b.homeBtnNavMain.setImageResource(R.drawable.ic_unselected_home_24);
        b.chartsBtnNavMain.setImageResource(R.drawable.ic_charts_unselected_24);
        b.saveBtnNavMain.setImageResource(R.drawable.ic_unselected_map_24);
        b.profileBtnNavMain.setImageResource(R.drawable.ic_profile_24);


        currentBtn = btn;
        currentDot = dot;

        currentBtn.setImageResource(drawable);
        currentDot.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        /*if (IS_HIDDEN) {
            showMainLayout();
            return;
        }*/

        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);

    }

    public static class NonSwipableViewPager extends ViewPager {


        public NonSwipableViewPager(@NonNull Context context) {
            super(context);
        }

        public NonSwipableViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            return false;
        }

        public void setMyScroller() {

            try {
                Class<?> viewpager = ViewPager.class;
                Field scroller = viewpager.getDeclaredField("mScroller");
                scroller.setAccessible(true);
                scroller.set(this, new NonSwipableViewPager.MyScroller(getContext()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public class MyScroller extends Scroller {

            public MyScroller(Context context) {
                super(context, new DecelerateInterpolator());
            }

            @Override
            public void startScroll(int startX, int startY, int dx, int dy, int duration) {
                super.startScroll(startX, startY, dx, dy, 350);
            }
        }
    }

    public static class ViewPagerFragmentAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        public ViewPagerFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

}
