package com.actiknow.liveaudit.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.actiknow.liveaudit.R;
import com.actiknow.liveaudit.adapter.MyPagerAdapter;
import com.actiknow.liveaudit.adapter.SmartFragmentStatePagerAdapter;
import com.actiknow.liveaudit.fragment.BaseFragment;
import com.actiknow.liveaudit.model.Response;
import com.actiknow.liveaudit.utils.AppConfigTags;
import com.actiknow.liveaudit.utils.Constants;
import com.actiknow.liveaudit.utils.CustomViewPager;
import com.actiknow.liveaudit.utils.Utils;


public class ViewPagerActivity extends AppCompatActivity {

    public static boolean flag = false;
    static CustomViewPager vpPager;

    private SmartFragmentStatePagerAdapter adapterViewPager;

    public static void nextPage () {
        vpPager.setCurrentItem (vpPager.getCurrentItem () + 1);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_pager);
        initView ();
        initListener ();
        initData ();
    }

    private void initData () {
        Constants.count = 0;
        Constants.responseList.clear ();
        adapterViewPager = new MyPagerAdapter (getSupportFragmentManager ());
        vpPager.setAdapter (adapterViewPager);
        vpPager.setClipToPadding (false);
        vpPager.setPageMargin (10);
        vpPager.setOffscreenPageLimit (1);
    }

    private void initView () {
        vpPager = (CustomViewPager) findViewById (R.id.vpPager);
    }

    private void initListener () {
        vpPager.addOnPageChangeListener (new ViewPager.OnPageChangeListener () {
            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected (int position) {
                if (position == Constants.total_questions) {
                    BaseFragment fragment = (BaseFragment) adapterViewPager.getRegisteredFragment (position);
                    int count = 0;
                    for (int i = 0; i < Constants.total_questions - 1; i++) {
                        Response response;
                        response = Constants.responseList.get (i);
                        count = count + response.getResponse_switch_flag ();
                    }
                    int rating = ((count + 1) * 100) / Constants.total_questions;
                    Utils.showLog (Log.DEBUG, AppConfigTags.RATING, "" + rating);
                    fragment.tvRatingNumber.setText (String.valueOf (rating / 10));
                    fragment.sbRating.setProgress (rating);
                }
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged (int state) {
                // Code goes here
            }
        });

        final View touchView = findViewById (R.id.vpPager);
        touchView.setOnTouchListener (new View.OnTouchListener () {
            @Override
            public boolean onTouch (View v, MotionEvent event) {
                return false;
            }
        });
    }

    @Override
    public void onBackPressed () {
        if (vpPager.getCurrentItem () > 0) {
            vpPager.setCurrentItem (vpPager.getCurrentItem () - 1);
        } else {
            finish ();
            overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}
