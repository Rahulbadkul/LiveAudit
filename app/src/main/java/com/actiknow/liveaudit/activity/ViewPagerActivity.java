package com.actiknow.liveaudit.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.actiknow.liveaudit.R;
import com.actiknow.liveaudit.adapter.MyPagerAdapter;
import com.actiknow.liveaudit.adapter.SmartFragmentStatePagerAdapter;
import com.actiknow.liveaudit.utils.Constants;
import com.actiknow.liveaudit.utils.CustomViewPager;
import com.pixelcan.inkpageindicator.InkPageIndicator;



public class ViewPagerActivity extends AppCompatActivity {


    //    ViewPager vpPager;
    CustomViewPager vpPager;

    InkPageIndicator inkPageIndicator;
    // ...
    private SmartFragmentStatePagerAdapter adapterViewPager;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_pager);
        initView ();
        initListener ();
        initAdapter ();

        adapterViewPager = new MyPagerAdapter (getSupportFragmentManager ());
        vpPager.setAdapter (adapterViewPager);

//        inkPageIndicator.setViewPager (vpPager);

        vpPager.setClipToPadding (false);
        vpPager.setPageMargin (10);
        vpPager.setOffscreenPageLimit (Constants.questionsList.size () - 1);
    }

    private void initAdapter () {
    }

    private void initView () {
        vpPager = (CustomViewPager) findViewById (R.id.vpPager);
        inkPageIndicator = (InkPageIndicator) findViewById (R.id.indicator);
    }

    private void initListener () {
        // Attach the page change listener inside the activity
        vpPager.addOnPageChangeListener (new ViewPager.OnPageChangeListener () {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected (int position) {
                //     Toast.makeText (ViewPagerActivity.this,
                //             "Selected page position: " + position, Toast.LENGTH_SHORT).show ();
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

        /*
        vpPager.setOnTouchListener (new View.OnTouchListener () {

            @Override
            public boolean onTouch (View v, MotionEvent event) {
                if(BaseFragment.flag)
                    Toast.makeText (ViewPagerActivity.this, "Flag is true", Toast.LENGTH_SHORT).show ();
                else
                    Toast.makeText (ViewPagerActivity.this, "Flag is false", Toast.LENGTH_SHORT).show ();

                return true;

            }
        });
        */

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
