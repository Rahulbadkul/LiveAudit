package com.actiknow.liveaudit.fragment;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.actiknow.liveaudit.R;
import com.actiknow.liveaudit.activity.ViewPagerActivity;
import com.actiknow.liveaudit.app.AppController;
import com.actiknow.liveaudit.model.Response;
import com.actiknow.liveaudit.utils.AppConfigTags;
import com.actiknow.liveaudit.utils.AppConfigURL;
import com.actiknow.liveaudit.utils.Constants;
import com.actiknow.liveaudit.utils.NetworkConnection;
import com.actiknow.liveaudit.utils.SetTypeFace;
import com.actiknow.liveaudit.utils.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class BaseFragment extends android.support.v4.app.Fragment {
    private static final String IMAGE_RESOURCE = "image-resource";
    //    public static boolean flag = false;
    public static boolean isLast = false;
    final int CAMERA_ACTIVITY_1 = 1;
    final int CAMERA_ACTIVITY_2 = 2;
    RelativeLayout rlRequirements;
    RelativeLayout rlRating;
    Bitmap bp1 = null;
    Bitmap bp2 = null;
    Bitmap bp1temp;
    Bitmap bp2temp;
    Response response;
    // Store instance variables
    private String question;
    private int question_id;
    private int page;
    private int switch_flag;
    private Switch switchYesNo;
    private EditText etComments;
    private ImageView ivImage1;
    private ImageView ivImage2;
    private Button btNext;
    private int image;

    public static BaseFragment newInstance (int page) {
        BaseFragment fragmentFirst = new BaseFragment ();
        Bundle args = new Bundle ();
        args.putInt ("page_number", page);
        isLast = true;
        fragmentFirst.setArguments (args);
        return fragmentFirst;
    }

    public static BaseFragment newInstance (int page, String question, int question_id) {
        BaseFragment fragmentFirst = new BaseFragment ();
        Bundle args = new Bundle ();
        isLast = false;
        args.putInt ("page_number", page);
        args.putString ("question", question);
        args.putInt ("question_id", question_id);
        fragmentFirst.setArguments (args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        page = getArguments ().getInt ("page_number", 0);
        if (! isLast) {
            question = getArguments ().getString ("question");
            question_id = getArguments ().getInt ("question_id");
        }
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        if (! isLast) {
            view = inflater.inflate (R.layout.fragment_first, container, false);
            final TextView tvQuestion = (TextView) view.findViewById (R.id.tvQuestion);
            switchYesNo = (Switch) view.findViewById (R.id.switchYesNo);
            etComments = (EditText) view.findViewById (R.id.etComments);

            btNext = (Button) view.findViewById (R.id.btNextInFragment);

            tvQuestion.setText (question);

            ivImage1 = (ImageView) view.findViewById (R.id.ivImage1);
            ivImage2 = (ImageView) view.findViewById (R.id.ivImage2);


            Typeface tf = SetTypeFace.getTypeface (getActivity ());
            SetTypeFace.applyTypeface (SetTypeFace.getParentView (tvQuestion), tf);

            if (savedInstanceState == null) {

            } else {
                // if there is a bundle, use the saved image resource (if one is there
                bp1temp = savedInstanceState.getParcelable ("BitmapImage1");
                bp2temp = savedInstanceState.getParcelable ("BitmapImage2");

                if (bp1temp != null)
                    ivImage1.setImageBitmap (bp1temp);
                else
                    ivImage1.setImageResource (R.drawable.ic_camera_placeholder);

                if (bp2temp != null)
                    ivImage2.setImageBitmap (bp2temp);
                else
                    ivImage2.setImageResource (R.drawable.ic_camera_placeholder);
            }

            ivImage1.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    Intent mIntent = null;
                    if (isPackageExists ("com.google.android.camera")) {
                        mIntent = new Intent ();
                        mIntent.setPackage ("com.google.android.camera");
                        mIntent.setAction (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    } else {
                        PackageManager packageManager = getActivity ().getPackageManager ();
                        String defaultCameraPackage = null;
                        List<ApplicationInfo> list = packageManager.getInstalledApplications (PackageManager.GET_UNINSTALLED_PACKAGES);
                        for (int n = 0; n < list.size (); n++) {
                            if ((list.get (n).flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                                Log.d ("TAG", "Installed Applications  : " + list.get (n).loadLabel (packageManager).toString ());
                                Log.d ("TAG", "package name  : " + list.get (n).packageName);
                                if (list.get (n).loadLabel (packageManager).toString ().equalsIgnoreCase ("Camera")) {
                                    defaultCameraPackage = list.get (n).packageName;
                                    break;
                                }
                            }
                        }

                        mIntent = new Intent ();
                        mIntent.setPackage (defaultCameraPackage);
                        mIntent.setAction (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

//                    mIntent = new Intent (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }

                    if (mIntent.resolveActivity (getActivity ().getPackageManager ()) != null) {
                        startActivityForResult (mIntent, CAMERA_ACTIVITY_1);
                    }

                }
            });


            ivImage2.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    Intent mIntent = null;
                    if (isPackageExists ("com.google.android.camera")) {
                        mIntent = new Intent ();
                        mIntent.setPackage ("com.google.android.camera");
                        mIntent.setAction (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    } else {
                        PackageManager packageManager = getActivity ().getPackageManager ();
                        String defaultCameraPackage = null;
                        List<ApplicationInfo> list = packageManager.getInstalledApplications (PackageManager.GET_UNINSTALLED_PACKAGES);
                        for (int n = 0; n < list.size (); n++) {
                            if ((list.get (n).flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                                Log.d ("TAG", "Installed Applications  : " + list.get (n).loadLabel (packageManager).toString ());
                                Log.d ("TAG", "package name  : " + list.get (n).packageName);
                                if (list.get (n).loadLabel (packageManager).toString ().equalsIgnoreCase ("Camera")) {
                                    defaultCameraPackage = list.get (n).packageName;
                                    break;
                                }
                            }
                        }

                        mIntent = new Intent ();
                        mIntent.setPackage (defaultCameraPackage);
                        mIntent.setAction (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    mIntent = new Intent (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }

                    if (mIntent.resolveActivity (getActivity ().getPackageManager ()) != null) {
                        startActivityForResult (mIntent, CAMERA_ACTIVITY_2);
                    }

                }
            });

/*
            switchYesNo.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener () {
                @Override
                public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
                    if(switchYesNo.isChecked ())
                        Constants.count++;
                    else
                        Constants.count--;
                }
            });

*/
            btNext.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    if (switchYesNo.isChecked ()) {
                        switch_flag = 1;
                    } else {
                        switch_flag = 0;
                    }

                    if (switch_flag == 0) {
                        if (etComments.getText ().toString ().length () != 0) {
                            etComments.setError (null);
                            ViewPagerActivity.nextPage ();

                            final String image1 = Utils.bitmapToBase64 (bp1);
                            final String image2 = Utils.bitmapToBase64 (bp2);

                            Log.e ("fragment number :", "" + page);
                            Log.e ("Auditor ID :", "" + Constants.auditor_id_main);
                            Log.e ("ATM Unique ID :", "" + Constants.atm_unique_id);
                            Log.e ("question id:", "" + question_id);
                            Log.e ("question :", "" + question);
                            Log.e ("Switch Flag :", "" + switch_flag);
                            Log.e ("Comment :", etComments.getText ().toString ());
                            Log.e ("Image1 :", image1);
                            Log.e ("Image2 :", image2);

                            Response response = new Response ();
                            response.setResponse_auditor_id (Constants.auditor_id_main);
                            response.setResponse_atm_unique_id (Constants.atm_unique_id);
                            response.setResponse_question_id (question_id);
                            response.setResponse_question (question);
                            response.setResponse_switch_flag (switch_flag);
                            response.setResponse_comment (etComments.getText ().toString ());
                            response.setResponse_image1 (image1);
                            response.setResponse_image2 (image2);
                            Constants.responseList.add (page, response);

                   /*

                        if (NetworkConnection.isNetworkAvailable (getActivity ())) {
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

                                    //Creating parameters
                                    Map<String, String> params = new Hashtable<String, String> ();

                                    //Adding parameters
                                    params.put (AppConfigTags.ATM_UNIQUE_ID, Constants.atm_unique_id);
                                    params.put (AppConfigTags.AUDITOR_ID, String.valueOf (Constants.auditor_id_main));
                                    params.put (AppConfigTags.QUESTION_ID, String.valueOf (question_id));
                                    params.put (AppConfigTags.SWITCH_FLAG, String.valueOf (switch_flag));
                                    params.put (AppConfigTags.COMMENT, etComments.getText ().toString ());
                                    params.put (AppConfigTags.IMAGE1, image1);
                                    params.put (AppConfigTags.IMAGE2, image2);
                                    //returning parameters
                                    Log.d ("Param sent to the server", "" + params);
                                    return params;
                                }
                            };
                            AppController.getInstance ().addToRequestQueue (strRequest1);
                        } else {
                            Log.d ("Response", "Response to be done in no internet connection");
                        }

                        */
                        } else {
                            etComments.setError ("Please fill in the comments");
                        }

                    } else {
                        etComments.setError (null);
                        ViewPagerActivity.nextPage ();
                        final String image1 = Utils.bitmapToBase64 (bp1);
                        final String image2 = Utils.bitmapToBase64 (bp2);

                        Log.e ("fragment number :", "" + page);
                        Log.e ("Auditor ID :", "" + Constants.auditor_id_main);
                        Log.e ("ATM Unique ID :", "" + Constants.atm_unique_id);
                        Log.e ("question id:", "" + question_id);
                        Log.e ("question :", "" + question);
                        Log.e ("Switch Flag :", "" + switch_flag);
                        Log.e ("Comment :", etComments.getText ().toString ());
                        Log.e ("Image1 :", image1);
                        Log.e ("Image2 :", image2);

                        Response response = new Response ();
                        response.setResponse_auditor_id (Constants.auditor_id_main);
                        response.setResponse_atm_unique_id (Constants.atm_unique_id);
                        response.setResponse_question_id (question_id);
                        response.setResponse_question (question);
                        response.setResponse_switch_flag (switch_flag);
                        response.setResponse_comment (etComments.getText ().toString ());
                        response.setResponse_image1 (image1);
                        response.setResponse_image2 (image2);
                        Constants.responseList.add (page, response);
                    }
                }
            });





//        Log.d ("question ", "" + question);
//        Log.d ("question_id ", "" + question_id);

        } else {
            view = inflater.inflate (R.layout.fragment_last, container, false);
            final TextView tvRatingNumber = (TextView) view.findViewById (R.id.tvRatingNumber);
            SeekBar sbRating = (SeekBar) view.findViewById (R.id.sbRating);
            Button btSubmit = (Button) view.findViewById (R.id.btSubmitInFragment);

            Typeface tf = SetTypeFace.getTypeface (getActivity ());
            SetTypeFace.applyTypeface (SetTypeFace.getParentView (tvRatingNumber), tf);

            tvRatingNumber.setText ("" + (int) Constants.final_rating / 10);

            sbRating.setProgress ((int) Constants.final_rating);


            tvRatingNumber.setText ((Constants.count / Constants.total_questions) * 100);

            sbRating.setProgress ((Constants.count / Constants.total_questions) * 100);

            sbRating.setOnSeekBarChangeListener (new SeekBar.OnSeekBarChangeListener () {

                public void onStopTrackingTouch (SeekBar bar) {
                    int value = bar.getProgress (); // the value of the seekBar progress
                }

                public void onStartTrackingTouch (SeekBar bar) {
                }

                public void onProgressChanged (SeekBar bar, int paramInt, boolean paramBoolean) {
                    tvRatingNumber.setText ("" + paramInt / 10);
                }
            });


            btSubmit.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View v) {

                    Log.e ("Karman", "in the submit button");

                    for (int i = 0; i < Constants.total_questions; i++) {
                        Response response;
                        response = Constants.responseList.get (i);

//                        Log.e ("fragment number :", "" + i);
//                        Log.e ("Auditor ID :", "" + response.getResponse_auditor_id ());
//                        Log.e ("ATM Unique ID :", response.getResponse_atm_unique_id ());
//                        Log.e ("question id:", "" + response.getResponse_question_id ());
//                        Log.e ("question :", response.getResponse_question ());
//                        Log.e ("Switch Flag :", "" + response.getResponse_switch_flag ());
//                        Log.e ("Comment :", response.getResponse_comment ());
//                        Log.e ("Image1 :", response.getResponse_image1 ());
//                        Log.e ("Image2 :", response.getResponse_image2 ());

                        if (NetworkConnection.isNetworkAvailable (getActivity ())) {
                            Log.d ("URL", AppConfigURL.URL_SUBMITRESPONSE);
                            final Response finalResponse = response;
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

                                    //Creating parameters
                                    Map<String, String> params = new Hashtable<String, String> ();

                                    //Adding parameters
                                    params.put (AppConfigTags.ATM_UNIQUE_ID, finalResponse.getResponse_atm_unique_id ());
                                    params.put (AppConfigTags.AUDITOR_ID, String.valueOf (finalResponse.getResponse_auditor_id ()));
                                    params.put (AppConfigTags.QUESTION_ID, String.valueOf (finalResponse.getResponse_question_id ()));
                                    params.put (AppConfigTags.QUESTION, finalResponse.getResponse_question ());
                                    params.put (AppConfigTags.SWITCH_FLAG, String.valueOf (finalResponse.getResponse_switch_flag ()));
                                    params.put (AppConfigTags.COMMENT, finalResponse.getResponse_comment ());
                                    params.put (AppConfigTags.IMAGE1, finalResponse.getResponse_image1 ());
                                    params.put (AppConfigTags.IMAGE2, finalResponse.getResponse_image2 ());
                                    //returning parameters
                                    Log.d ("Param sent to the server", "" + params);
                                    return params;
                                }
                            };
                            AppController.getInstance ().addToRequestQueue (strRequest1);
                        } else {
                            Log.d ("Response", "Response to be done in no internet connection");
                        }


                    }
                }
            });


        }
        return view;
    }

    public boolean isPackageExists (String targetPackage) {
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getActivity ().getPackageManager ();
        packages = pm.getInstalledApplications (0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals (targetPackage)) return true;
        }
        return false;
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult (requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case CAMERA_ACTIVITY_1:
                    bp1 = (Bitmap) data.getExtras ().get ("data");
                    ivImage1.setImageBitmap (bp1);
                    break;
                case CAMERA_ACTIVITY_2:
                    bp2 = (Bitmap) data.getExtras ().get ("data");
                    ivImage2.setImageBitmap (bp2);
                    break;
            }

        } catch (Exception e) {
        }
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        outState.putInt (IMAGE_RESOURCE, image);
        outState.putParcelable ("BitmapImage1", bp1);
        outState.putParcelable ("BitmapImage2", bp2);
        super.onSaveInstanceState (outState);
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView ();

    }
}