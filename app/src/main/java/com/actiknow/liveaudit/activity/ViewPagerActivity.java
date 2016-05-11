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
import com.actiknow.liveaudit.app.AppController;
import com.actiknow.liveaudit.model.Response;
import com.actiknow.liveaudit.utils.AppConfigTags;
import com.actiknow.liveaudit.utils.AppConfigURL;
import com.actiknow.liveaudit.utils.Constants;
import com.actiknow.liveaudit.utils.CustomViewPager;
import com.actiknow.liveaudit.utils.NetworkConnection;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class ViewPagerActivity extends AppCompatActivity {

    public static boolean flag = false;

    //    ViewPager vpPager;
    CustomViewPager vpPager;

    //   InkPageIndicator inkPageIndicator;
    List<Response> responses = new ArrayList<Response> ();
    private SmartFragmentStatePagerAdapter adapterViewPager;
    private Button btNext;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_pager);
        initView ();
        initListener ();
        initAdapter ();

        Constants.count = 0;
        Constants.final_rating = 0;

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


                if (NetworkConnection.isNetworkAvailable (ViewPagerActivity.this)) {
                    Log.d ("URL", AppConfigURL.URL_SUBMITRESPONSE);
                    StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_SUBMITRESPONSE,
                            new com.android.volley.Response.Listener<String> () {
                                @Override
                                public void onResponse (String response) {
                                    Log.d ("SERVER RESPONSE", response);
                                    if (response != null) {
                                        try {
                                            JSONObject jsonObj = new JSONObject (response);
                                        } catch (JSONException e) {
                                            e.printStackTrace ();
                                        }
                                    } else {
                                        Log.e (AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER);
                                    }
                                }
                            },
                            new com.android.volley.Response.ErrorListener () {
                                @Override
                                public void onErrorResponse (VolleyError error) {
                                    Log.d ("TAG", error.toString ());
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams () throws AuthFailureError {
                            //Converting Bitmap to String
//                            String image = getStringImage (bitmap);

                            //Getting Image Name
//                            String name = editTextName.getText ().toString ().trim ();

                            //Creating parameters
                            Map<String, String> params = new Hashtable<String, String> ();

                            //Adding parameters
                            params.put (AppConfigTags.ATM_UNIQUE_ID, "1");
                            params.put (AppConfigTags.AUDITOR_ID, "2");
                            params.put (AppConfigTags.QUESTION_ID, "1");
                            params.put (AppConfigTags.SWITCH_FLAG, "1");
                            params.put (AppConfigTags.COMMENT, "comment");
                            params.put (AppConfigTags.IMAGE1, "image1.png");
                            params.put (AppConfigTags.IMAGE2, "image2.png");
                            //returning parameters
                            return params;
                        }
                    };
                    AppController.getInstance ().addToRequestQueue (strRequest1);
                } else {
                    Log.d ("Response", "Response to be done in no internet connection");
                }


                //      if(BaseFragment.flag)
                vpPager.setCurrentItem (vpPager.getCurrentItem () + 1);
                //       else {
                //          Toast.makeText (ViewPagerActivity.this, "Please fill the comment", Toast.LENGTH_SHORT).show ();
                //           Log.e ("karman", "please fill the comment");
                //       }

                //vpPager.getAdapter ().notifyDataSetChanged ();


                //              BaseFragment.addResponse();


                Constants.final_rating = ((Constants.count * 100) / Constants.questionsList.size ());

                Log.e ("rating :", "a " + Constants.final_rating);


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
