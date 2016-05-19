package com.actiknow.liveaudit.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.actiknow.liveaudit.helper.DatabaseHandler;
import com.actiknow.liveaudit.model.Rating;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class BaseFragment extends android.support.v4.app.Fragment {
    private static final String IMAGE_RESOURCE = "image-resource";
    //    public static boolean flag = false;
    public static boolean isLast = false;
    public static TextView tvRatingNumber;
    public static SeekBar sbRating;
    final int CAMERA_ACTIVITY_1 = 1;
    final int CAMERA_ACTIVITY_2 = 2;
    RelativeLayout rlRequirements;
    RelativeLayout rlRating;
    Bitmap bp1 = null;
    Bitmap bp2 = null;
    Bitmap bp1temp;
    Bitmap bp2temp;
    Response response;
    DatabaseHandler db;
    JSONArray jsonArray;
    ProgressDialog pDialog;
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
        db = new DatabaseHandler (getActivity ());
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

                    View view = getActivity ().getCurrentFocus ();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity ().getSystemService (Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow (view.getWindowToken (), 0);
                    }

                    if (switchYesNo.isChecked ()) {
                        switch_flag = 1;
                        Constants.count++;
                    } else {
                        switch_flag = 0;
                        Constants.count--;
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
                            response.setResponse_agency_id (Constants.atm_agency_id);
                            response.setResponse_atm_unique_id (Constants.atm_unique_id);
                            response.setResponse_question_id (question_id);
                            response.setResponse_question (question);
                            response.setResponse_switch_flag (switch_flag);
                            if (page == 0)
                                response.setResponse_comment (etComments.getText ().toString () + " " + Constants.atm_location_in_manual);
                            else
                                response.setResponse_comment (etComments.getText ().toString ());
                            response.setResponse_image1 (image1);
                            response.setResponse_image2 (image2);
                            Constants.responseList.add (page, response);
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
                        Log.e ("Agency ID :", "" + Constants.atm_agency_id);
                        Log.e ("ATM Unique ID :", "" + Constants.atm_unique_id);
                        Log.e ("question id:", "" + question_id);
                        Log.e ("question :", "" + question);
                        Log.e ("Switch Flag :", "" + switch_flag);
                        Log.e ("Comment :", etComments.getText ().toString ());
                        Log.e ("Image1 :", image1);
                        Log.e ("Image2 :", image2);

                        Response response = new Response ();
                        response.setResponse_auditor_id (Constants.auditor_id_main);
                        response.setResponse_agency_id (Constants.atm_agency_id);
                        response.setResponse_atm_unique_id (Constants.atm_unique_id);
                        response.setResponse_question_id (question_id);
                        response.setResponse_question (question);
                        response.setResponse_switch_flag (switch_flag);
                        if (page == 0)
                            response.setResponse_comment (etComments.getText ().toString () + " " + Constants.atm_location_in_manual);
                        else
                            response.setResponse_comment (etComments.getText ().toString ());
                        response.setResponse_image1 (image1);
                        response.setResponse_image2 (image2);
                        Constants.responseList.add (page, response);
                    }
                }
            });

        } else {
            view = inflater.inflate (R.layout.fragment_last, container, false);
            tvRatingNumber = (TextView) view.findViewById (R.id.tvRatingNumber);
            sbRating = (SeekBar) view.findViewById (R.id.sbRating);
            Button btSubmit = (Button) view.findViewById (R.id.btSubmitInFragment);

            Typeface tf = SetTypeFace.getTypeface (getActivity ());
            SetTypeFace.applyTypeface (SetTypeFace.getParentView (tvRatingNumber), tf);

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
                    pDialog = new ProgressDialog (getActivity ());
                    pDialog.setMessage ("Please Wait...");
                    pDialog.setCancelable (true);
                    pDialog.show ();
/*
                    try {
                        jsonArray = new JSONArray (AppConfigTags.RESPONSES);
                    } catch (JSONException e) {
                        e.printStackTrace ();
                    }
*/
                    for (int i = 0; i < Constants.total_questions; i++) {
                        final Response response;
                        response = Constants.responseList.get (i);

/*
                        JSONObject jsonObject = new JSONObject ();

                        try {
                            jsonObject.put (AppConfigTags.ATM_UNIQUE_ID, response.getResponse_atm_unique_id ());
                            jsonObject.put (AppConfigTags.ATM_AGENCY_ID, String.valueOf (response.getResponse_agency_id ()));
                            jsonObject.put (AppConfigTags.AUDITOR_ID, String.valueOf (response.getResponse_auditor_id ()));
                            jsonObject.put (AppConfigTags.QUESTION_ID, String.valueOf (response.getResponse_question_id ()));
                            jsonObject.put (AppConfigTags.QUESTION, response.getResponse_question ());
                            jsonObject.put (AppConfigTags.SWITCH_FLAG, String.valueOf (response.getResponse_switch_flag ()));
                            jsonObject.put (AppConfigTags.COMMENT, response.getResponse_comment ());
                            jsonObject.put (AppConfigTags.IMAGE1, response.getResponse_image1 ());
                            jsonObject.put (AppConfigTags.IMAGE2, response.getResponse_image2 ());
                            jsonArray.put (jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace ();
                        }

                        */

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
                            final int finalI = i;
                            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_SUBMITRESPONSE,
                                    new com.android.volley.Response.Listener<String> () {
                                        @Override
                                        public void onResponse (String response) {
                                            Log.d ("SERVER RESPONSE", response);
                                            if (response != null) {
                                                try {
                                                    if (finalI == Constants.total_questions - 1) {
                                                        pDialog.dismiss ();
                                                        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ());
                                                        builder.setMessage ("Your responses have been uploaded successfully to the server")
                                                                .setCancelable (false)
                                                                .setPositiveButton ("OK", new DialogInterface.OnClickListener () {
                                                                    public void onClick (DialogInterface dialog, int id) {
                                                                        dialog.dismiss ();
                                                                        getActivity ().finish ();
                                                                        getActivity ().overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
                                                                    }
                                                                });
                                                        AlertDialog alert = builder.create ();
                                                        alert.show ();

                                                    }

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
                                            if (finalI == Constants.total_questions - 1) {
                                                pDialog.dismiss ();
                                                AlertDialog.Builder builder2 = new AlertDialog.Builder (getActivity ());
                                                builder2.setMessage ("Seems like there is an issue with the internet connection, your responses have been saved " +
                                                        "and will be uploaded once you are online")
                                                        .setCancelable (false)
                                                        .setPositiveButton ("OK", new DialogInterface.OnClickListener () {
                                                            public void onClick (DialogInterface dialog, int id) {
                                                                dialog.dismiss ();
                                                                getActivity ().finish ();
                                                                getActivity ().overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
                                                            }
                                                        });
                                                AlertDialog alert2 = builder2.create ();
                                                alert2.show ();
                                                db.createResponse (response);
                                            }
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams () throws AuthFailureError {
                                    //Creating parameters
                                    Map<String, String> params = new Hashtable<String, String> ();
                                    //Adding parameters
                                    params.put (AppConfigTags.ATM_UNIQUE_ID, finalResponse.getResponse_atm_unique_id ());
                                    params.put (AppConfigTags.ATM_AGENCY_ID, String.valueOf (finalResponse.getResponse_agency_id ()));
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
                            if (i == Constants.total_questions - 1) {
                                pDialog.dismiss ();
                                AlertDialog.Builder builder3 = new AlertDialog.Builder (getActivity ());
                                builder3.setMessage ("Seems like there is no internet connection, your responses have been saved " +
                                        "and will be uploaded once you are online")
                                        .setCancelable (false)
                                        .setPositiveButton ("OK", new DialogInterface.OnClickListener () {
                                            public void onClick (DialogInterface dialog, int id) {
                                                dialog.dismiss ();
                                                getActivity ().finish ();
                                                getActivity ().overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
                                            }
                                        });
                                AlertDialog alert3 = builder3.create ();
                                alert3.show ();
                            }
                            db.createResponse (response);
                        }
                    }

                    final Rating rating = new Rating ();
                    rating.setAtm_unique_id (Constants.atm_unique_id);
                    rating.setAuditor_id (Constants.auditor_id_main);
                    rating.setRating (sbRating.getProgress () / 10);

                    if (NetworkConnection.isNetworkAvailable (getActivity ())) {
                        Log.d ("URL", AppConfigURL.URL_SUBMITRATING);
                        StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_SUBMITRATING,
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
                                //Creating parameters
                                Map<String, String> params = new Hashtable<String, String> ();
                                //Adding parameters
                                params.put (AppConfigTags.ATM_UNIQUE_ID, rating.getAtm_unique_id ());
                                params.put (AppConfigTags.AUDITOR_ID, String.valueOf (rating.getAuditor_id ()));
                                params.put (AppConfigTags.RATING, String.valueOf (rating.getRating ()));
                                //returning parameters
                                Log.d ("Param sent to the server", "" + params);
                                return params;
                            }
                        };
                        AppController.getInstance ().addToRequestQueue (strRequest1);
                    } else {
                        Log.d ("Response", "Response to be done in no internet connection");
                        db.createRating (rating);
                    }


//                    Log.d ("Jsonstring : ", jsonArray.toString ());
                }
            });
        }

        db.closeDB ();
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

    @Override
    public void onResume () {
        super.onResume ();
        Log.e ("karman", "asd");
//        refresh ();

    }
}