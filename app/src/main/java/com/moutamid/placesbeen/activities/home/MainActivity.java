package com.moutamid.placesbeen.activities.home;

import static com.bumptech.glide.Glide.with;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.databinding.ActivityMainBinding;
import com.moutamid.placesbeen.fragments.charts.ChartsFragment;
import com.moutamid.placesbeen.fragments.home.HomeFragment;
import com.moutamid.placesbeen.fragments.profile.ProfileFragment;
import com.moutamid.placesbeen.fragments.save.SaveFragment;
import com.moutamid.placesbeen.onboard.fragments.FragmentOnBoardingThree;
import com.moutamid.placesbeen.onboard.fragments.FragmentOnBoardingTwo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public ActivityMainBinding b;

    private ViewPagerFragmentAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(b.getRoot());

        viewPager = findViewById(R.id.main_view_pager);

        adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());

        MainController.fetchAllPolygonBoundaries();

        // Setting up the view Pager
        setupViewPager(viewPager);

        currentDot = b.homeDotBtnNav;
        currentBtn = b.homeBtnNavMain;

        b.homeLayoutMain.setOnClickListener(view -> {
            changeNavTo(b.homeDotBtnNav, b.homeBtnNavMain, R.drawable.ic_selected_home_24);

            viewPager.setCurrentItem(0, true);
        });
        b.chartsLayoutMain.setOnClickListener(view -> {
            changeNavTo(b.chartsDotBtnNav, b.chartsBtnNavMain, R.drawable.ic_charts_selected_24);

            viewPager.setCurrentItem(1, true);
        });
        b.saveLayoutMain.setOnClickListener(view -> {
            changeNavTo(b.saveDotBtnNav, b.saveBtnNavMain, R.drawable.ic_save_24);
            viewPager.setCurrentItem(2, true);

        });
        b.profileLayoutMain.setOnClickListener(view -> {
            changeNavTo(b.profileDotBtnNav, b.profileBtnNavMain, R.drawable.ic_profile_selected_24);
            viewPager.setCurrentItem(3, true);

        });

    }

    private View currentDot;
    private ImageView currentBtn;

    public void openProfilePage() {
        changeNavTo(b.profileDotBtnNav, b.profileBtnNavMain, R.drawable.ic_profile_selected_24);
        viewPager.setCurrentItem(3, true);
    }

    private void setupViewPager(ViewPager viewPager) {
        // Adding Fragments to Adapter
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new ChartsFragment());
        adapter.addFragment(new SaveFragment());
        adapter.addFragment(new ProfileFragment());

        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);

        Log.d(TAG, "setupViewPager: adapter attached");

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    changeNavTo(b.homeDotBtnNav, b.homeBtnNavMain, R.drawable.ic_selected_home_24);
                } else if (position == 1) {
                    ChartsFragment fragment = (ChartsFragment) adapter.getItem(1);
                    fragment.refreshArcs();
                    changeNavTo(b.chartsDotBtnNav, b.chartsBtnNavMain, R.drawable.ic_charts_selected_24);
                } else if (position == 2) {
                    changeNavTo(b.saveDotBtnNav, b.saveBtnNavMain, R.drawable.ic_save_24);
                } else if (position == 3) {
                    ProfileFragment fragment = (ProfileFragment) adapter.getItem(3);
                    fragment.refreshData();
                    changeNavTo(b.profileDotBtnNav, b.profileBtnNavMain, R.drawable.ic_profile_selected_24);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    private void changeNavTo(View dot, ImageView btn, int drawable) {

        currentDot.setVisibility(View.GONE);
        b.homeBtnNavMain.setImageResource(R.drawable.ic_unselected_home_24);
        b.chartsBtnNavMain.setImageResource(R.drawable.ic_charts_unselected_24);
        b.saveBtnNavMain.setImageResource(R.drawable.ic_unsave_24);
        b.profileBtnNavMain.setImageResource(R.drawable.ic_profile_24);


        currentBtn = btn;
        currentDot = dot;

        currentBtn.setImageResource(drawable);
        currentDot.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {

        if (viewPager.getCurrentItem() == 0) super.onBackPressed();

        else viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);

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
