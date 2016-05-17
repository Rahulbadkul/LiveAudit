package com.actiknow.liveaudit.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actiknow.liveaudit.R;
import com.actiknow.liveaudit.adapter.MyPagerAdapter;
import com.actiknow.liveaudit.adapter.SmartFragmentStatePagerAdapter;
import com.actiknow.liveaudit.fragment.BaseFragment;
import com.actiknow.liveaudit.model.Response;
import com.actiknow.liveaudit.utils.Constants;
import com.actiknow.liveaudit.utils.CustomViewPager;

import java.util.ArrayList;
import java.util.List;


public class ViewPagerActivity extends AppCompatActivity {

    public static boolean flag = false;

    //    ViewPager vpPager;
    static CustomViewPager vpPager;

    //   InkPageIndicator inkPageIndicator;
    List<Response> responses = new ArrayList<Response> ();
    private SmartFragmentStatePagerAdapter adapterViewPager;
    private Button btNext;

    public static void nextPage () {
        vpPager.setCurrentItem (vpPager.getCurrentItem () + 1);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_pager);
        initView ();
        initListener ();
        initAdapter ();

        Constants.count = 0;
        Constants.responseList.clear ();

        adapterViewPager = new MyPagerAdapter (getSupportFragmentManager ());
        vpPager.setAdapter (adapterViewPager);


        // based on the current position you can then cast the page to the correct
        // class and call the method:
//        if (ViewPager.getCurrentItem () == 0 && page != null) {
//            ((FragmentClass1) page).updateList ("new item");
//        }

//        inkPageIndicator.setViewPager (vpPager);

        vpPager.setClipToPadding (false);
        vpPager.setPageMargin (10);
        //      vpPager.setOffscreenPageLimit (Constants.questionsList.size () - 1);
        vpPager.setOffscreenPageLimit (1);

    }

    private void initAdapter () {
    }

    private void initView () {
        vpPager = (CustomViewPager) findViewById (R.id.vpPager);
        //       inkPageIndicator = (InkPageIndicator) findViewById (R.id.indicator);
        btNext = (Button) findViewById (R.id.btNext);
    }

    private void initListener () {


        // Attach the page change listener inside the activity
        vpPager.addOnPageChangeListener (new ViewPager.OnPageChangeListener () {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected (int position) {
                //     Toast.makeText (ViewPagerActivity.this,
                //             "Selected page position: " + position, Toast.LENGTH_SHORT).show ();


                if (position == Constants.total_questions) {
//                    BaseFragment.refresh ();
                    Log.e ("karman.singh asd", "asd in onpageselected");
                    BaseFragment fragment = (BaseFragment) adapterViewPager.getRegisteredFragment (position);


                    int count = 0;

//                    for (int i=0; i<Constants.total_questions; i++){
//                        Response response;
//                        response = Constants.responseList.get (i);
//                        count = count +response.getResponse_switch_flag ();
//                    }

                    Log.d ("count in local", "asd" + count);
                    Log.e ("count", "asd" + Constants.count);
                    Log.e ("total question", "asd" + Constants.total_questions);

                    int rating = (Constants.count * 100) / Constants.total_questions;

                    Log.e ("rating", "asd" + rating);

//                    fragment.tvRatingNumber.setText (String.valueOf (rating));
//                    fragment.sbRating.setProgress (rating);

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

        btNext.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                //               onNextPressed ();




                vpPager.setCurrentItem (vpPager.getCurrentItem () + 1);



                if (vpPager.getCurrentItem () == Constants.questionsList.size ()) {
                    Toast.makeText (ViewPagerActivity.this, "All clear", Toast.LENGTH_SHORT).show ();

//                    adapterViewPager.notifyDataSetChanged ();

                }
                if (vpPager.getCurrentItem () == Constants.questionsList.size ())
                    btNext.setText ("SUBMIT");
                else
                    btNext.setText ("NEXT");
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
        if ((vpPager.getCurrentItem ()) == (Constants.questionsList.size () + 1))
            btNext.setText ("SUBMIT");
        else
            btNext.setText ("NEXT");

        if (vpPager.getCurrentItem () > 0) {
            vpPager.setCurrentItem (vpPager.getCurrentItem () - 1);
        } else {
            finish ();
            overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}
