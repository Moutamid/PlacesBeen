package com.moutamid.placesbeen.onboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.activities.RegistrationActivity;
import com.moutamid.placesbeen.onboard.fragments.FragmentOnBoardingOne;
import com.moutamid.placesbeen.onboard.fragments.FragmentOnBoardingThree;
import com.moutamid.placesbeen.onboard.fragments.FragmentOnBoardingTwo;
import com.moutamid.placesbeen.utils.Utils;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity {
    private static final String TAG = "OnBoardingActivity";

    private ViewPagerFragmentAdapter adapter;
    private ViewPager viewPager;

//    LottieAnimationView forwardBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.changeStatusBarColor(this, R.color.yellow);
        setContentView(R.layout.activity_on_boarding);
        viewPager = findViewById(R.id.onBoarding_walkThrough_view_pager);
//        forwardBtn = findViewById(R.id.forwardAnimationBtn);
//        forwardBtn.setMinAndMaxProgress(0, 1.0f);

        adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());

        // Setting up the view Pager
        setupViewPager(viewPager);

        findViewById(R.id.startBtnOnBoard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OnBoardingActivity.this, RegistrationActivity.class));
                finish();
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
//        Utils.changeStatusBarColor(OnBoardingActivity.this, R.color.whitesmoke);
        // Adding Fragments to Adapter
        adapter.addFragment(new FragmentOnBoardingOne());
        adapter.addFragment(new FragmentOnBoardingTwo());
        adapter.addFragment(new FragmentOnBoardingThree());

//        forwardBtn.addAnimatorListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//                forwardBtn.setProgress(0.5f);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                forwardBtn.pauseAnimation();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//
//            }
//        });
//        forwardBtn.setProgress(0.2f);

        // Setting Adapter To ViewPager
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);

        Log.d(TAG, "setupViewPager: adapter attached");

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
//                    forwardBtn.setProgress(0.2f);
                    findViewById(R.id.startBtnOnBoard).setVisibility(View.GONE);
                } else if (position == 1) {
//                    forwardBtn.setProgress(0.4f);
                    findViewById(R.id.startBtnOnBoard).setVisibility(View.GONE);
                } else if (position == 2) {
//                    forwardBtn.resumeAnimation();
//                    YoYo.with(Techniques.FadeIn)
//                            .delay(100)
//                            .duration(700)
//                            .onStart(new YoYo.AnimatorCallback() {
//                                @Override
//                                public void call(Animator animator) {
                    findViewById(R.id.startBtnOnBoard).setVisibility(View.VISIBLE);
//                                }
//                            })
//                            .playOn(findViewById(R.id.startBtnOnBoard));
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        SpringDotsIndicator springDotsIndicator = (SpringDotsIndicator) findViewById(R.id.dotsIndicatorOnBoard);
        springDotsIndicator.setViewPager(viewPager);

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
                scroller.set(this, new MyScroller(getContext()));
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