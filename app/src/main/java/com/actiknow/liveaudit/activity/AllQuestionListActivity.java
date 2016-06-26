
package com.actiknow.liveaudit.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.actiknow.liveaudit.R;
import com.actiknow.liveaudit.adapter.AllQuestionsAdapter;
import com.actiknow.liveaudit.helper.DatabaseHandler;
import com.actiknow.liveaudit.model.GeoImage;
import com.actiknow.liveaudit.model.Question;
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
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class AllQuestionListActivity extends AppCompatActivity {

    public static TextView tvRatingNumber;
    public static SeekBar sbRating;
    Button btSubmit;
    ListView lvAllQuestions;
    ProgressDialog pDialog;

    GoogleApiClient client;
    Dialog dialogEnterManually;
    DatabaseHandler db;
    // Action Bar components
    private List<Question> questionList = new ArrayList<> ();
    private AllQuestionsAdapter adapter;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_all_questions_list);
        initView ();
        initListener ();
        initData ();
        db.closeDB ();
    }

    private void initData () {
        db = new DatabaseHandler (getApplicationContext ());
        Utils.setTypefaceToAllViews (this, lvAllQuestions);


        for (int i = 0; i < Constants.questionsList.size (); i++) {
            Question question = Constants.questionsList.get (i);
            Response response = new Response ();
            response.setResponse_auditor_id (Constants.auditor_id_main);
            response.setResponse_agency_id (Constants.atm_agency_id);
            response.setResponse_atm_unique_id (Constants.atm_unique_id);
            response.setResponse_question_id (question.getQuestion_id ());
            response.setResponse_question (question.getQuestion ());
            response.setResponse_switch_flag (0);
            if (Constants.atm_location_in_manual.length () != 0 && i == 0)
                response.setResponse_comment (Constants.atm_location_in_manual);
            else
                response.setResponse_comment ("");
            response.setResponse_image1 ("");
            response.setResponse_image2 ("");
            Constants.responseList.add (i, response);
        }


        adapter = new AllQuestionsAdapter (this, Constants.questionsList);
        lvAllQuestions.setAdapter (adapter);
        client = new GoogleApiClient.Builder (this).addApi (AppIndex.API).build ();
//        dialogSplash = new Dialog (this, R.style.full_screen);
    }

    private void initListener () {
        sbRating.setOnSeekBarChangeListener (new SeekBar.OnSeekBarChangeListener () {
            public void onStopTrackingTouch (SeekBar bar) {
                int value = bar.getProgress (); // the value of the seekBar progress
            }

            public void onStartTrackingTouch (SeekBar bar) {
            }

            public void onProgressChanged (SeekBar bar, int paramInt, boolean paramBoolean) {
                if (paramInt / 10 == 10)
                    tvRatingNumber.setText ("" + paramInt / 10);
                else
                    tvRatingNumber.setText (" " + paramInt / 10);
            }
        });

        btSubmit.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                pDialog = new ProgressDialog (AllQuestionListActivity.this);
                Utils.showProgressDialog (pDialog, null);
//                JSONArray jsonArray = new JSONArray ();

//                try {
                for (int i = 0; i < Constants.total_questions; i++) {
                    final Response response;
                    response = Constants.responseList.get (i);
                    submitResponseToServer (i, response);
//                        JSONObject jsonObject = new JSONObject ();
//                        jsonObject.put (AppConfigTags.ATM_UNIQUE_ID, response.getResponse_atm_unique_id ());
//                        jsonObject.put (AppConfigTags.ATM_AGENCY_ID, String.valueOf (response.getResponse_agency_id ()));
//                        jsonObject.put (AppConfigTags.AUDITOR_ID, String.valueOf (response.getResponse_auditor_id ()));
//                        jsonObject.put (AppConfigTags.QUESTION_ID, String.valueOf (response.getResponse_question_id ()));
//                        jsonObject.put (AppConfigTags.QUESTION, response.getResponse_question ());
//                        jsonObject.put (AppConfigTags.SWITCH_FLAG, String.valueOf (response.getResponse_switch_flag ()));
//                        jsonObject.put (AppConfigTags.COMMENT, response.getResponse_comment ());
//                        jsonObject.put (AppConfigTags.IMAGE1, response.getResponse_image1 ());
//                        jsonObject.put (AppConfigTags.IMAGE2, response.getResponse_image2 ());
//                        jsonArray.put (jsonObject);

//                        Log.d (AppConfigTags.ATM_UNIQUE_ID, response.getResponse_atm_unique_id ());
//                        Log.d (AppConfigTags.ATM_AGENCY_ID, String.valueOf (response.getResponse_agency_id ()));
//                        Log.d (AppConfigTags.AUDITOR_ID, String.valueOf (response.getResponse_auditor_id ()));
//                        Log.d (AppConfigTags.QUESTION_ID, String.valueOf (response.getResponse_question_id ()));
//                        Log.d (AppConfigTags.QUESTION, response.getResponse_question ());
//                        Log.d (AppConfigTags.SWITCH_FLAG, String.valueOf (response.getResponse_switch_flag ()));
//                        Log.d (AppConfigTags.COMMENT, response.getResponse_comment ());
//                        Log.d (AppConfigTags.IMAGE1, response.getResponse_image1 ());
//                        Log.d (AppConfigTags.IMAGE2, response.getResponse_image2 ());
                }
//                } catch (JSONException e) {
//                    e.printStackTrace ();
//                }

//                Log.d ("json array of response", ""+ jsonArray);

                Constants.rating.setAtm_unique_id (Constants.atm_unique_id);
                Constants.rating.setAuditor_id (Constants.auditor_id_main);
                Constants.rating.setRating (sbRating.getProgress () / 10);
                submitRatingToServer (Constants.rating);
                submitGeoImageToServer (Constants.geoImage);
            }
        });
    }

    private void initView () {
        lvAllQuestions = (ListView) findViewById (R.id.lvQuestionList);
        tvRatingNumber = (TextView) findViewById (R.id.tvRatingNumber);
        sbRating = (SeekBar) findViewById (R.id.sbRating);
        btSubmit = (Button) findViewById (R.id.btSubmit);
    }

    @Override
    public void onStart () {
        super.onStart ();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect ();
        Action viewAction = Action.newAction (
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse ("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse ("android-app://com.actiknow.liveaudit/http/host/path")
        );
        AppIndex.AppIndexApi.start (client, viewAction);
    }

    @Override
    public void onStop () {
        super.onStop ();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction (
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse ("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse ("android-app://com.actiknow.liveaudit/http/host/path")
        );
        AppIndex.AppIndexApi.end (client, viewAction);
        client.disconnect ();
    }

    @Override
    public void onBackPressed () {
        finish ();
        overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater ().inflate (R.menu.menu_rest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        return super.onOptionsItemSelected (item);
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult (requestCode, resultCode, data);
        try {

            Bitmap bp = (Bitmap) data.getExtras ().get ("data");
            String image = Utils.bitmapToBase64 (bp);

            for (int i = 0; i < Constants.questionsList.size (); i++) {
                final Response response;
                response = Constants.responseList.get (i);
                if (requestCode == response.getResponse_question_id ())
                    response.setResponse_image1 (image);
            }
        } catch (Exception e) {
        }
    }

    private void submitResponseToServer (int i, Response response) {
        if (NetworkConnection.isNetworkAvailable (AllQuestionListActivity.this)) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_SUBMITRESPONSE, true);
            final Response finalResponse = response;
            final int finalI = i;
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_SUBMITRESPONSE,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                //             if (finalI == 0 && Constants.atm_location_in_manual.length ()!=0)

                                if (finalI == Constants.total_questions - 1) {
                                    pDialog.dismiss ();
                                    Utils.showOkDialog (AllQuestionListActivity.this, "Your responses have been uploaded successfully to the server", true);
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
                                Utils.showOkDialog (AllQuestionListActivity.this, "Seems like there is an issue with the internet connection," +
                                        " your responses have been saved and will be uploaded once you are online", true);
                            }
                            db.createResponse (finalResponse);
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
            Utils.sendRequest (strRequest1);
        } else {
            if (i == Constants.total_questions - 1) {
                pDialog.dismiss ();
                Utils.showOkDialog (AllQuestionListActivity.this, "Seems like there is no internet connection, your responses have been saved" +
                        " and will be uploaded once you are online", true);
            }
            db.createResponse (response);
        }
    }

    private void submitRatingToServer (final Rating rating) {
        if (NetworkConnection.isNetworkAvailable (AllQuestionListActivity.this)) {
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
                            db.createRating (rating);
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
            Utils.sendRequest (strRequest1);
        } else
            db.createRating (rating);
    }

    private void submitGeoImageToServer (final GeoImage geoImage) {
        if (NetworkConnection.isNetworkAvailable (AllQuestionListActivity.this)) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_SUBMITGEOIMAGE, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_SUBMITGEOIMAGE,
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
                            db.createGeoImage (geoImage);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.ATM_UNIQUE_ID, geoImage.getAtm_unique_id ());
                    params.put (AppConfigTags.AUDITOR_ID, String.valueOf (geoImage.getAuditor_id ()));
                    params.put (AppConfigTags.ATM_AGENCY_ID, String.valueOf (geoImage.getAgency_id ()));
                    params.put (AppConfigTags.GEO_IMAGE, geoImage.getGeo_image_string ());
                    params.put (AppConfigTags.LATITUDE, geoImage.getLatitude ());
                    params.put (AppConfigTags.LONGITUDE, geoImage.getLongitude ());
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
            };
            Utils.sendRequest (strRequest1);
        } else
            db.createGeoImage (geoImage);
    }
}



