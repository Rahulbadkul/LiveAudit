package com.actiknow.liveaudit.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    public static boolean isLast = false;
    public static TextView tvRatingNumber;
    public static SeekBar sbRating;
    final int CAMERA_ACTIVITY_1 = 1;
    final int CAMERA_ACTIVITY_2 = 2;
    Bitmap bp1 = null;
    Bitmap bp2 = null;
    Bitmap bp1temp;
    Bitmap bp2temp;
    DatabaseHandler db;
    ProgressDialog pDialog;
    // Store instance variables
    private String question;
    private int question_id, page, switch_flag;
    private Switch switchYesNo;
    private EditText etComments;
    private TextView tvQuestion;
    private ImageView ivImage1, ivImage2;
    private Button btNext, btSubmit;

    public static BaseFragment newInstance (int page) {
        BaseFragment fragmentFirst = new BaseFragment ();
        Bundle args = new Bundle ();
        args.putInt (AppConfigTags.PAGE_NUMBER, page);
        isLast = true;
        fragmentFirst.setArguments (args);
        return fragmentFirst;
    }

    public static BaseFragment newInstance (int page, String question, int question_id) {
        BaseFragment fragmentFirst = new BaseFragment ();
        Bundle args = new Bundle ();
        isLast = false;
        args.putInt (AppConfigTags.PAGE_NUMBER, page);
        args.putString (AppConfigTags.QUESTION, question);
        args.putInt (AppConfigTags.QUESTION_ID, question_id);
        fragmentFirst.setArguments (args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        page = getArguments ().getInt (AppConfigTags.PAGE_NUMBER, 0);
        if (! isLast) {
            question = getArguments ().getString (AppConfigTags.QUESTION);
            question_id = getArguments ().getInt (AppConfigTags.QUESTION_ID);
        }
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        db = new DatabaseHandler (getActivity ());
        if (! isLast) {
            view = inflater.inflate (R.layout.fragment_first, container, false);
            initView (view);
            initListener ();
            tvQuestion.setText (question);
            Utils.setTypefaceToAllViews (getActivity (), tvQuestion);
            if (savedInstanceState != null) {
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

        } else {
            view = inflater.inflate (R.layout.fragment_last, container, false);
            initView2 (view);
            initListener2 ();
            Utils.setTypefaceToAllViews (getActivity (), tvRatingNumber);
        }
        db.closeDB ();
        return view;
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
        outState.putParcelable ("BitmapImage1", bp1);
        outState.putParcelable ("BitmapImage2", bp2);
        super.onSaveInstanceState (outState);
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView ();

    }

    private void initView (View view) {
        switchYesNo = (Switch) view.findViewById (R.id.switchYesNo);
        etComments = (EditText) view.findViewById (R.id.etComments);
        btNext = (Button) view.findViewById (R.id.btNextInFragment);
        ivImage1 = (ImageView) view.findViewById (R.id.ivImage1);
        ivImage2 = (ImageView) view.findViewById (R.id.ivImage2);
        tvQuestion = (TextView) view.findViewById (R.id.tvQuestion);

    }

    private void initListener () {
        ivImage1.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                Intent mIntent = null;
                if (Utils.isPackageExists (getActivity (), "com.google.android.camera")) {
                    mIntent = new Intent ();
                    mIntent.setPackage ("com.google.android.camera");
                    mIntent.setAction (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                } else {
                    PackageManager packageManager = getActivity ().getPackageManager ();
                    String defaultCameraPackage = null;
                    List<ApplicationInfo> list = packageManager.getInstalledApplications (PackageManager.GET_UNINSTALLED_PACKAGES);
                    for (int n = 0; n < list.size (); n++) {
                        if ((list.get (n).flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                            Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "Installed Applications  : " + list.get (n).loadLabel (packageManager).toString (), false);
                            Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "package name  : " + list.get (n).packageName, false);
                            if (list.get (n).loadLabel (packageManager).toString ().equalsIgnoreCase ("Camera")) {
                                defaultCameraPackage = list.get (n).packageName;
                                break;
                            }
                        }
                    }
                    mIntent = new Intent ();
                    mIntent.setPackage (defaultCameraPackage);
                    mIntent.setAction (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                }
                if (mIntent.resolveActivity (getActivity ().getPackageManager ()) != null)
                    startActivityForResult (mIntent, CAMERA_ACTIVITY_1);
            }
        });

        ivImage2.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                Intent mIntent = null;
                if (Utils.isPackageExists (getActivity (), "com.google.android.camera")) {
                    mIntent = new Intent ();
                    mIntent.setPackage ("com.google.android.camera");
                    mIntent.setAction (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                } else {
                    PackageManager packageManager = getActivity ().getPackageManager ();
                    String defaultCameraPackage = null;
                    List<ApplicationInfo> list = packageManager.getInstalledApplications (PackageManager.GET_UNINSTALLED_PACKAGES);
                    for (int n = 0; n < list.size (); n++) {
                        if ((list.get (n).flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                            Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "Installed Applications  : " + list.get (n).loadLabel (packageManager).toString (), false);
                            Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "package name  : " + list.get (n).packageName, false);
                            if (list.get (n).loadLabel (packageManager).toString ().equalsIgnoreCase ("Camera")) {
                                defaultCameraPackage = list.get (n).packageName;
                                break;
                            }
                        }
                    }
                    mIntent = new Intent ();
                    mIntent.setPackage (defaultCameraPackage);
                    mIntent.setAction (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                }
                if (mIntent.resolveActivity (getActivity ().getPackageManager ()) != null)
                    startActivityForResult (mIntent, CAMERA_ACTIVITY_2);
            }
        });

        btNext.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                Utils.hideSoftKeyboard (getActivity ());
                if (switchYesNo.isChecked ())
                    switch_flag = 1;
                else
                    switch_flag = 0;

                if (! switchYesNo.isChecked ()) {
                    if (etComments.getText ().toString ().length () != 0) {
                        etComments.setError (null);
                        ViewPagerActivity.nextPage ();
                        final String image1 = Utils.bitmapToBase64 (bp1);
                        final String image2 = Utils.bitmapToBase64 (bp2);

                        Utils.showLog (Log.DEBUG, AppConfigTags.PAGE_NUMBER, "" + page, false);
                        Utils.showLog (Log.DEBUG, AppConfigTags.AUDITOR_ID, "" + Constants.auditor_id_main, false);
                        Utils.showLog (Log.DEBUG, AppConfigTags.ATM_AGENCY_ID, "" + Constants.atm_agency_id, false);
                        Utils.showLog (Log.DEBUG, AppConfigTags.ATM_UNIQUE_ID, "" + Constants.atm_unique_id, false);
                        Utils.showLog (Log.DEBUG, AppConfigTags.QUESTION_ID, "" + question_id, false);
                        Utils.showLog (Log.DEBUG, AppConfigTags.QUESTION, "" + question, false);
                        Utils.showLog (Log.DEBUG, AppConfigTags.SWITCH_FLAG, "" + switch_flag, false);
                        Utils.showLog (Log.DEBUG, AppConfigTags.COMMENT, etComments.getText ().toString (), false);
                        Utils.showLog (Log.DEBUG, AppConfigTags.IMAGE1, image1, false);
                        Utils.showLog (Log.DEBUG, AppConfigTags.IMAGE2, image2, false);

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
                    } else
                        Utils.showErrorInEditText (etComments, "Please fill in the comments");
                } else {
                    etComments.setError (null);
                    ViewPagerActivity.nextPage ();
                    final String image1 = Utils.bitmapToBase64 (bp1);
                    final String image2 = Utils.bitmapToBase64 (bp2);

                    Utils.showLog (Log.DEBUG, AppConfigTags.PAGE_NUMBER, "" + page, false);
                    Utils.showLog (Log.DEBUG, AppConfigTags.AUDITOR_ID, "" + Constants.auditor_id_main, false);
                    Utils.showLog (Log.DEBUG, AppConfigTags.ATM_AGENCY_ID, "" + Constants.atm_agency_id, false);
                    Utils.showLog (Log.DEBUG, AppConfigTags.ATM_UNIQUE_ID, "" + Constants.atm_unique_id, false);
                    Utils.showLog (Log.DEBUG, AppConfigTags.QUESTION_ID, "" + question_id, false);
                    Utils.showLog (Log.DEBUG, AppConfigTags.QUESTION, "" + question, false);
                    Utils.showLog (Log.DEBUG, AppConfigTags.SWITCH_FLAG, "" + switch_flag, false);
                    Utils.showLog (Log.DEBUG, AppConfigTags.COMMENT, etComments.getText ().toString (), false);
                    Utils.showLog (Log.DEBUG, AppConfigTags.IMAGE1, image1, false);
                    Utils.showLog (Log.DEBUG, AppConfigTags.IMAGE2, image2, false);


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
    }

    private void initView2 (View view) {
        tvRatingNumber = (TextView) view.findViewById (R.id.tvRatingNumber);
        sbRating = (SeekBar) view.findViewById (R.id.sbRating);
        btSubmit = (Button) view.findViewById (R.id.btSubmitInFragment);
    }

    private void initListener2 () {
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
                Utils.showProgressDialog (pDialog, null);
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
                    submitResponseToServer (i, response);
/**
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
                }
                final Rating rating = new Rating ();
                rating.setAtm_unique_id (Constants.atm_unique_id);
                rating.setAuditor_id (Constants.auditor_id_main);
                rating.setRating (sbRating.getProgress () / 10);

                submitRatingToServer (rating);
//                    Log.d ("Jsonstring : ", jsonArray.toString ());
            }
        });

    }

    private void submitResponseToServer (int i, Response response) {
        if (NetworkConnection.isNetworkAvailable (getActivity ())) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_SUBMITRESPONSE, true);
            final Response finalResponse = response;
            final int finalI = i;
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_SUBMITRESPONSE,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                if (finalI == Constants.total_questions - 1) {
                                    pDialog.dismiss ();
                                    Utils.showOkDialog (getActivity (), "Your responses have been uploaded successfully to the server", true);
                                }
                            } else
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            if (finalI == Constants.total_questions - 1) {
                                pDialog.dismiss ();
                                Utils.showOkDialog (getActivity (), "Seems like there is an issue with the internet connection," +
                                        " your responses have been saved and will be uploaded once you are online", true);
                                db.createResponse (finalResponse);
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.ATM_UNIQUE_ID, finalResponse.getResponse_atm_unique_id ());
                    params.put (AppConfigTags.ATM_AGENCY_ID, String.valueOf (finalResponse.getResponse_agency_id ()));
                    params.put (AppConfigTags.AUDITOR_ID, String.valueOf (finalResponse.getResponse_auditor_id ()));
                    params.put (AppConfigTags.QUESTION_ID, String.valueOf (finalResponse.getResponse_question_id ()));
                    params.put (AppConfigTags.QUESTION, finalResponse.getResponse_question ());
                    params.put (AppConfigTags.SWITCH_FLAG, String.valueOf (finalResponse.getResponse_switch_flag ()));
                    params.put (AppConfigTags.COMMENT, finalResponse.getResponse_comment ());
                    params.put (AppConfigTags.IMAGE1, finalResponse.getResponse_image1 ());
                    params.put (AppConfigTags.IMAGE2, finalResponse.getResponse_image2 ());
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
            };
            AppController.getInstance ().addToRequestQueue (strRequest1);
        } else {
            if (i == Constants.total_questions - 1) {
                pDialog.dismiss ();
                Utils.showOkDialog (getActivity (), "Seems like there is no internet connection, your responses have been saved" +
                        " and will be uploaded once you are online", true);
            }
            db.createResponse (response);
        }
    }

    private void submitRatingToServer (final Rating rating) {
        if (NetworkConnection.isNetworkAvailable (getActivity ())) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_SUBMITRATING, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_SUBMITRATING,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                } catch (JSONException e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.ATM_UNIQUE_ID, rating.getAtm_unique_id ());
                    params.put (AppConfigTags.AUDITOR_ID, String.valueOf (rating.getAuditor_id ()));
                    params.put (AppConfigTags.RATING, String.valueOf (rating.getRating ()));
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
            };
            AppController.getInstance ().addToRequestQueue (strRequest1);
        } else
            db.createRating (rating);
    }
}