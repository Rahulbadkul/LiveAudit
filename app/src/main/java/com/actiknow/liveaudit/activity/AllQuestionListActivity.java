
package com.actiknow.liveaudit.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.actiknow.liveaudit.R;
import com.actiknow.liveaudit.adapter.AllQuestionsAdapter;
import com.actiknow.liveaudit.helper.DatabaseHandler;
import com.actiknow.liveaudit.model.Question;
import com.actiknow.liveaudit.model.Report;
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
import com.kyanogen.signatureview.SignatureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;


public class AllQuestionListActivity extends AppCompatActivity {

    public static TextView tvRatingNumber;
    public static SeekBar sbRating;
    Button btSubmit;
    ListView lvAllQuestions;
    ProgressDialog pDialog;

    GoogleApiClient client;
    Dialog dialogSign;
    DatabaseHandler db;
    // Action Bar components
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
            response.setQuestion_id (question.getQuestion_id ());
            response.setQuestion (question.getQuestion ());
            response.setSwitch_flag (0);
            if (Constants.atm_location_in_manual.length () != 0 && i == 0)
                response.setComment (Constants.atm_location_in_manual);
            else
                response.setComment ("");
            response.setImage1 ("");
            response.setImage2 ("");
            Constants.responseList.add (i, response);
        }

        //      sbRating.setEnabled (false);

        adapter = new AllQuestionsAdapter (this, Constants.questionsList);
        lvAllQuestions.setAdapter (adapter);
        client = new GoogleApiClient.Builder (this).addApi (AppIndex.API).build ();
//        dialogSplash = new Dialog (this, R.style.full_screen);
    }

    private void initListener () {

        sbRating.setOnTouchListener (new View.OnTouchListener () {
            @Override
            public boolean onTouch (View view, MotionEvent motionEvent) {
                return true;
            }
        });

/*
        sbRating.setOnSeekBarChangeListener (new SeekBar.OnSeekBarChangeListener () {
            public void onStopTrackingTouch (SeekBar bar) {
                int value = bar.getProgress (); // the value of the seekBar progress
            }

            public void onStartTrackingTouch (SeekBar bar) {
            }

            public void onProgressChanged (SeekBar bar, int paramInt, boolean paramBoolean) {
//                if (paramInt / 10 == 10)
//                    tvRatingNumber.setText ("" + paramInt / 10);
//                else
//                    tvRatingNumber.setText (" " + paramInt / 10);

            }
        });
*/
        btSubmit.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                JSONArray jsonArray = new JSONArray ();
                try {
                    for (int i = 0; i < Constants.questionsList.size (); i++) {
                        final Response response;
                        response = Constants.responseList.get (i);
                        JSONObject jsonObject = new JSONObject ();
                        jsonObject.put (AppConfigTags.QUESTION_ID, String.valueOf (response.getQuestion_id ()));
                        jsonObject.put (AppConfigTags.QUESTION, response.getQuestion ());
                        jsonObject.put (AppConfigTags.SWITCH_FLAG, String.valueOf (response.getSwitch_flag ()));
                        jsonObject.put (AppConfigTags.COMMENT, response.getComment ());
                        jsonObject.put (AppConfigTags.IMAGE1, response.getImage1 ());
                        jsonObject.put (AppConfigTags.IMAGE2, response.getImage2 ());
                        jsonArray.put (jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace ();
                }
                Constants.report.setIssues_json_array (String.valueOf (jsonArray));
                Constants.report.setRating (sbRating.getProgress ());
                showSignatureDialog ();
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
//            switch (resultCode) {
//                case RESULT_OK:
            File f = new File (Environment.getExternalStorageDirectory () + File.separator + "img.jpg");
            Bitmap bp = null;
            if (f.exists ()) {
                bp = Utils.compressBitmap (BitmapFactory.decodeFile (f.getAbsolutePath ()), AllQuestionListActivity.this);
            }

//                    Bitmap bp = (Bitmap) data.getExtras ().get ("data");
            String image = Utils.bitmapToBase64 (bp);

            for (int i = 0; i < Constants.questionsList.size (); i++) {
                final Response response;
                response = Constants.responseList.get (i);
                if (requestCode == response.getQuestion_id ())
                    response.setImage1 (image);
            }
//                    break;
//                case RESULT_CANCELED:
//                    Utils.showToast (MainActivity.this, "Please take an image");
//                    break;
//                default:
//                    break;
//            }
        } catch (Exception e) {
        }
    }

    private void showSignatureDialog () {
        Button btSignCanel;
        Button btSignClear;
        Button btSignSave;
        final SignatureView signatureView;

        dialogSign = new Dialog (AllQuestionListActivity.this);
        dialogSign.setContentView (R.layout.dialog_signature);
        dialogSign.setCancelable (false);
        btSignCanel = (Button) dialogSign.findViewById (R.id.btSignCancel);
        btSignClear = (Button) dialogSign.findViewById (R.id.btSignClear);
        btSignSave = (Button) dialogSign.findViewById (R.id.btSignSave);
        signatureView = (SignatureView) dialogSign.findViewById (R.id.signSignatureView);

        Utils.setTypefaceToAllViews (AllQuestionListActivity.this, btSignCanel);
//        dialogSign.requestWindowFeature (Window.FEATURE_NO_TITLE);
        dialogSign.getWindow ().setBackgroundDrawable (new ColorDrawable (android.graphics.Color.TRANSPARENT));
        dialogSign.show ();
        btSignCanel.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                dialogSign.dismiss ();
            }
        });
        btSignClear.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                signatureView.clearCanvas ();
            }
        });
        btSignSave.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                dialogSign.dismiss ();
//                pDialog = new ProgressDialog (AllQuestionListActivity.this);
//                Utils.showProgressDialog (pDialog, null);
                Bitmap bp = signatureView.getSignatureBitmap ();
                Constants.report.setSignature_image_string (Utils.bitmapToBase64 (bp));
                submitReportToServer (Constants.report);
            }
        });
    }

    private void submitReportToServer (final Report report) {
        if (NetworkConnection.isNetworkAvailable (AllQuestionListActivity.this)) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_SUBMITREPORT, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_SUBMITREPORT,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
//                                pDialog.dismiss ();
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    switch (jsonObj.getInt (AppConfigTags.STATUS)) {
                                        case 0:
                                            db.createReport (report);
//                                           pDialog.dismiss ();
//                                            Utils.showOkDialog (AllQuestionListActivity.this, "Some error occurred, Please try again after some time", true);
                                            Utils.showLog (Log.INFO, "RESPONSE LOG", "Some error occurred your responses have been saved offline and will be uploaded later", true);
                                            break;
                                        case 1:
//                                            pDialog.dismiss ();
//                                            Utils.showOkDialog (AllQuestionListActivity.this, "Your responses have been uploaded successfully to the server", true);
                                            Utils.showLog (Log.INFO, "RESPONSE LOG", "Your responses have been uploaded successfully to the server", true);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    db.createReport (report);
                                    e.printStackTrace ();
                                }
                            } else {
                                db.createReport (report);
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
//                            pDialog.dismiss ();
//                            Utils.showOkDialog (AllQuestionListActivity.this, "Seems like there is an issue with the internet connection," +
//                                    " your responses have been saved and will be uploaded once you are online", true);
                            db.createReport (report);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.ATM_ID, String.valueOf (report.getAtm_id ()));
                    params.put (AppConfigTags.ATM_UNIQUE_ID, report.getAtm_unique_id ());
                    params.put (AppConfigTags.ATM_AGENCY_ID, String.valueOf (report.getAgency_id ()));
                    params.put (AppConfigTags.AUDITOR_ID, String.valueOf (report.getAuditor_id ()));
                    params.put (AppConfigTags.ISSUES, report.getIssues_json_array ());
                    params.put (AppConfigTags.GEO_IMAGE, report.getGeo_image_string ());
                    params.put (AppConfigTags.LATITUDE, report.getLatitude ());
                    params.put (AppConfigTags.LONGITUDE, report.getLongitude ());
                    params.put (AppConfigTags.RATING, String.valueOf (report.getRating ()));
                    params.put (AppConfigTags.SIGN_IMAGE, report.getSignature_image_string ());

                    Log.e (AppConfigTags.ATM_ID, String.valueOf (report.getAtm_id ()));
                    Log.e (AppConfigTags.ATM_UNIQUE_ID, report.getAtm_unique_id ());
                    Log.e (AppConfigTags.ATM_AGENCY_ID, String.valueOf (report.getAgency_id ()));
                    Log.e (AppConfigTags.AUDITOR_ID, String.valueOf (report.getAuditor_id ()));
//                        Log.e (AppConfigTags.GEO_IMAGE, finalReport.getGeo_image_string ());
                    Log.e (AppConfigTags.LATITUDE, report.getLatitude ());
                    Log.e (AppConfigTags.LONGITUDE, report.getLongitude ());
                    Log.e (AppConfigTags.RATING, String.valueOf (report.getRating ()));
//                        Log.e (AppConfigTags.SIGN_IMAGE, finalReport.getSignature_image_string ());


//                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
            };
            Utils.sendRequest (strRequest1, 60);
            Utils.showOkDialog (AllQuestionListActivity.this, "Your responses have been saved" +
                    " and will be uploaded in the background", true);

        } else {
//            pDialog.dismiss ();
            Utils.showOkDialog (AllQuestionListActivity.this, "Seems like there is no internet connection, your responses have been saved" +
                    " and will be uploaded once you are online", true);
            db.createReport (report);
        }
    }
}



