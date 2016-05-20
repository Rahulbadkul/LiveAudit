
package com.actiknow.liveaudit.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actiknow.liveaudit.R;
import com.actiknow.liveaudit.adapter.AllAtmAdapter;
import com.actiknow.liveaudit.adapter.NavDrawerAdapter;
import com.actiknow.liveaudit.app.AppController;
import com.actiknow.liveaudit.helper.DatabaseHandler;
import com.actiknow.liveaudit.model.Atm;
import com.actiknow.liveaudit.model.Question;
import com.actiknow.liveaudit.utils.AppConfigTags;
import com.actiknow.liveaudit.utils.AppConfigURL;
import com.actiknow.liveaudit.utils.Constants;
import com.actiknow.liveaudit.utils.LoginDetailsPref;
import com.actiknow.liveaudit.utils.NetworkConnection;
import com.actiknow.liveaudit.utils.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    TextView tvNoInternetConnection;
    ProgressBar progressBar;
    ListView listViewAllAtm;
    Button btEnterManually;

    GoogleApiClient client;
    Dialog dialogSplash;
    Dialog dialogEnterManually;
    DatabaseHandler db;
    // Action Bar components
    private List<Atm> atmList = new ArrayList<> ();
    private AllAtmAdapter adapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mDrawerPanel;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        initView ();
        initPref ();
        isLogin ();
        initListener ();
        initData ();
        setUpNavigationDrawer ();
//        initLocationSettings ();

        if (Constants.splash_screen_first_time == 0 && Constants.auditor_id_main != 0)
            showSplashScreen ();

        if (Constants.auditor_id_main != 0) {
            getAtmListFromServer ();
            getQuestionListFromServer ();
            if (db.getResponseCount () > 0)
                uploadStoredResponseToServer ();
            if (db.getRatingCount () > 0)
                uploadStoredRatingToServer ();
        }
        db.closeDB ();
    }

    private void initData () {
        db = new DatabaseHandler (getApplicationContext ());
        Utils.setTypefaceToAllViews (this, tvNoInternetConnection);
        adapter = new AllAtmAdapter (this, atmList);
        listViewAllAtm.setAdapter (adapter);
        client = new GoogleApiClient.Builder (this).addApi (AppIndex.API).build ();
        dialogSplash = new Dialog (this, R.style.full_screen);
    }

    private void initListener () {
        btEnterManually.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                showEnterManuallyDialog ();
            }
        });
    }

    private void isLogin () {
        if (Constants.username == "" || Constants.auditor_id_main == 0 || Constants.auditor_name == "") {
            Intent myIntent = new Intent (this, LoginActivity.class);
            startActivity (myIntent);
        }
        if (Constants.auditor_id_main == 0)
            finish ();
    }

    private void initPref () {
        LoginDetailsPref loginDetailsPref = LoginDetailsPref.getInstance ();
        Constants.auditor_name = loginDetailsPref.getStringPref (MainActivity.this, LoginDetailsPref.AUDITOR_NAME);
        Constants.username = loginDetailsPref.getStringPref (MainActivity.this, LoginDetailsPref.USERNAME);
        Constants.auditor_id_main = loginDetailsPref.getIntPref (MainActivity.this, LoginDetailsPref.AUDITOR_ID);
    }

    private void initView () {
        listViewAllAtm = (ListView) findViewById (R.id.lvAtmList);
        tvNoInternetConnection = (TextView) findViewById (R.id.tvNoIternetConnection);
        progressBar = (ProgressBar) findViewById (R.id.progressbar);
        btEnterManually = (Button) findViewById (R.id.btEnterManually);
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
        if (mDrawerLayout.isDrawerOpen (mDrawerPanel)) {
            mDrawerLayout.closeDrawer (mDrawerPanel);
        } else {
            finish ();
            overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater ().inflate (R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId ()) {
            case R.id.action_logout:
                showLogOutDialog ();
                return true;
        }
        Utils.hideSoftKeyboard (MainActivity.this);
/**
 if (item != null && item.getItemId () == android.R.id.home) {
 if (mDrawerLayout.isDrawerOpen (mDrawerPanel)) {
 } else {
 mDrawerLayout.openDrawer (mDrawerPanel);
 }
 return true;
 }
 */
        return super.onOptionsItemSelected (item);
    }

    private void showLogOutDialog () {
        AlertDialog.Builder alert = new AlertDialog.Builder (MainActivity.this);
        alert.setMessage ("Are you sure you want to LOGOUT");
        alert.setPositiveButton ("YES", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                LoginDetailsPref loginDetailsPref = LoginDetailsPref.getInstance ();
                loginDetailsPref.putIntPref (MainActivity.this, LoginDetailsPref.AUDITOR_ID, 0);
                loginDetailsPref.putStringPref (MainActivity.this, LoginDetailsPref.AUDITOR_NAME, "");
                loginDetailsPref.putStringPref (MainActivity.this, LoginDetailsPref.USERNAME, "");
                Intent intent = new Intent (MainActivity.this, LoginActivity.class);
                Constants.username = "";
                Constants.auditor_name = "";
                Constants.auditor_id_main = 0;
                intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity (intent);
                overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        alert.setNegativeButton ("NO", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                dialog.dismiss ();
            }
        });
        alert.show ();
    }

    private void showSplashScreen () {
        Constants.splash_screen_first_time = 1;
        dialogSplash.setContentView (R.layout.dialog_splash);
        dialogSplash.setCancelable (false);
        dialogSplash.show ();

        final Handler handler = new Handler ();
        handler.postDelayed (new Runnable () {
            @Override
            public void run () {
                if (dialogSplash != null)
                    dialogSplash.dismiss ();
            }
        }, 4000);
    }

    private void setUpNavigationDrawer () {
        Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar1);
        toolbar.showOverflowMenu ();
        setSupportActionBar (toolbar);
        ActionBar actionBar = getSupportActionBar ();
        try {
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled (false);
            actionBar.setHomeButtonEnabled (false);
            actionBar.setTitle (getResources ().getString (R.string.app_name));
            actionBar.setDisplayShowTitleEnabled (false);
        } catch (Exception ignored) {
        }
        ListView mDrawerListView = (ListView) findViewById (R.id.navDrawerList);
        mDrawerPanel = (RelativeLayout) findViewById (R.id.navDrawerPanel);
        mDrawerLayout = (DrawerLayout) findViewById (R.id.drawer_layout);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<> (this, android.R.layout.simple_list_item_1, getResources ().getStringArray (R.array.menulist));
        mDrawerListView.setAdapter (new NavDrawerAdapter (this, getResources ().getStringArray (R.array.menulist)));
        mDrawerListView.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                }
                mDrawerLayout.closeDrawer (mDrawerPanel);
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle (this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened (View drawerView) {
                super.onDrawerOpened (drawerView);
                //getSupportActionBar().setTitle(getString(R.string.drawer_opened));
                invalidateOptionsMenu ();
            }

            public void onDrawerClosed (View view) {
                super.onDrawerClosed (view);
                //getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu ();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled (true);
        mDrawerLayout.setDrawerListener (mDrawerToggle);
    }

    private void getAtmListFromServer () {
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_GETALLATMS, true);
            StringRequest strRequest = new StringRequest (Request.Method.POST, AppConfigURL.URL_GETALLATMS,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            int is_data_received = 0;
                            int json_array_len = 0;
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                is_data_received = 1;
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    JSONArray jsonArray = jsonObj.getJSONArray (AppConfigTags.ATMS);
                                    db.deleteAllAtms ();
                                    json_array_len = jsonArray.length ();
                                    for (int i = 0; i < json_array_len; i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject (i);
                                        Atm atm = new Atm ();
                                        atm.setAtm_id (jsonObject.getInt (AppConfigTags.ATM_ID));
                                        atm.setAtm_unique_id (jsonObject.getString (AppConfigTags.ATM_UNIQUE_ID));
                                        atm.setAtm_agency_id (jsonObject.getInt (AppConfigTags.ATM_AGENCY_ID));
                                        atm.setAtm_last_audit_date (Utils.convertTimeFormat (jsonObject.getString (AppConfigTags.ATM_LAST_AUDIT_DATE)));
                                        atm.setAtm_bank_name (jsonObject.getString (AppConfigTags.ATM_BANK_NAME));
                                        atm.setAtm_address (jsonObject.getString (AppConfigTags.ATM_ADDRESS));
                                        atm.setAtm_city (jsonObject.getString (AppConfigTags.ATM_CITY));
                                        atm.setAtm_pincode (jsonObject.getString (AppConfigTags.ATM_PINCODE));
                                        atmList.add (atm);
                                        db.createAtm (atm);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            adapter.notifyDataSetChanged ();
                            if (is_data_received != 0 && json_array_len != 0) {
                                progressBar.setVisibility (View.GONE);
                                listViewAllAtm.setVisibility (View.VISIBLE);
                                tvNoInternetConnection.setVisibility (View.GONE);
                            } else if (is_data_received != 0 && json_array_len == 0) {
                                tvNoInternetConnection.setVisibility (View.VISIBLE);
                                progressBar.setVisibility (View.GONE);
                                listViewAllAtm.setVisibility (View.GONE);
                            }
                            if (is_data_received == 0) {
                                progressBar.setVisibility (View.GONE);
                                listViewAllAtm.setVisibility (View.GONE);
                                tvNoInternetConnection.setVisibility (View.VISIBLE);
                            }
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            progressBar.setVisibility (View.GONE);
                            listViewAllAtm.setVisibility (View.VISIBLE);
                            getAtmListFromLocalDatabase ();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.AUDITOR_ID, String.valueOf (Constants.auditor_id_main));
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
            };
            AppController.getInstance ().addToRequestQueue (strRequest);
        } else {
            progressBar.setVisibility (View.GONE);
            listViewAllAtm.setVisibility (View.VISIBLE);
            getAtmListFromLocalDatabase ();
            Utils.showOkDialog (MainActivity.this, "Seems like there is no internet connection, the app will continue in Offline mode", false);
        }
    }

    private void getAtmListFromLocalDatabase () {
        Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "Getting all the atm from local database", true);
        atmList.clear ();
        List<Atm> allAtm = db.getAllAtms ();
        for (Atm atm : allAtm)
            atmList.add (atm);
        adapter.notifyDataSetChanged ();
    }

    private void getQuestionListFromServer () {
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_GETALLQUESTIONS, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_GETALLQUESTIONS,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    JSONArray jsonArray = jsonObj.getJSONArray (AppConfigTags.QUESTIONS);
                                    Constants.total_questions = jsonArray.length ();
                                    db.deleteAllQuestion ();
                                    for (int i = 0; i < Constants.total_questions; i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject (i);
                                        Question question = new Question ();
                                        question.setQuestion_id (jsonObject.getInt (AppConfigTags.QUESTION_ID));
                                        question.setQuestion (jsonObject.getString (AppConfigTags.QUESTION));
                                        Constants.questionsList.add (question);
                                        db.createQuestion (question);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            getQuestionListFromLocalDatabase ();
                        }
                    });
            AppController.getInstance ().addToRequestQueue (strRequest1);
        } else {
            getQuestionListFromLocalDatabase ();
        }
    }

    private void getQuestionListFromLocalDatabase () {
        Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "Getting all the questions from local database", true);
        List<Question> allQuestions = db.getAllQuestions ();
        for (Question question : allQuestions)
            Constants.questionsList.add (question);
        Constants.total_questions = Constants.questionsList.size ();
    }

    private void showEnterManuallyDialog () {
        Button btEnterManuallyContinue;
        final EditText etEnterManuallyAtmId;
        final EditText etEnterManuallyAtmLocation;

        dialogEnterManually = new Dialog (MainActivity.this);
        dialogEnterManually.setContentView (R.layout.dialog_enter_manually);
        dialogEnterManually.setCancelable (true);
        btEnterManuallyContinue = (Button) dialogEnterManually.findViewById (R.id.btEnterManuallyContinue);
        etEnterManuallyAtmId = (EditText) dialogEnterManually.findViewById (R.id.etEnterManuallyAtmId);
        etEnterManuallyAtmLocation = (EditText) dialogEnterManually.findViewById (R.id.etEnterManuallyLocation);
        Utils.setTypefaceToAllViews (MainActivity.this, etEnterManuallyAtmId);
        dialogEnterManually.getWindow ().setBackgroundDrawable (new ColorDrawable (android.graphics.Color.TRANSPARENT));
        dialogEnterManually.show ();
        btEnterManuallyContinue.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                if (etEnterManuallyAtmId.getText ().toString ().length () == 0)
                    etEnterManuallyAtmId.setError ("Please enter the ATM ID");
                else {
                    Constants.atm_location_in_manual = etEnterManuallyAtmLocation.getText ().toString ().toUpperCase ();
                    Constants.atm_unique_id = etEnterManuallyAtmId.getText ().toString ().toUpperCase ();
                    Intent intent = new Intent (MainActivity.this, ViewPagerActivity.class);
                    MainActivity.this.startActivity (intent);
                    MainActivity.this.overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
                    dialogEnterManually.dismiss ();
                }
            }
        });
    }

    private void uploadStoredRatingToServer () {
        Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "Getting all the rating from local database", true);
        List<com.actiknow.liveaudit.model.Rating> allRatings = db.getAllRatings ();
        for (com.actiknow.liveaudit.model.Rating ratings : allRatings) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_SUBMITRATING, true);
            final com.actiknow.liveaudit.model.Rating finalRating = ratings;
            if (NetworkConnection.isNetworkAvailable (this)) {
                StringRequest strRequest3 = new StringRequest (Request.Method.POST, AppConfigURL.URL_SUBMITRATING,
                        new com.android.volley.Response.Listener<String> () {
                            @Override
                            public void onResponse (String response) {
                                Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                                if (response != null) {
                                    try {
                                        JSONObject jsonObj = new JSONObject (response);
                                        int status = jsonObj.getInt (AppConfigTags.STATUS);
                                        if (status == 1)
                                            db.deleteRating (finalRating.getRating_id ());
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
                        params.put (AppConfigTags.ATM_UNIQUE_ID, finalRating.getAtm_unique_id ());
                        params.put (AppConfigTags.AUDITOR_ID, String.valueOf (finalRating.getAuditor_id ()));
                        params.put (AppConfigTags.RATING, String.valueOf (finalRating.getRating ()));
                        Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                        return params;
                    }
                };
                AppController.getInstance ().addToRequestQueue (strRequest3);
            } else {
                Utils.showLog (Log.WARN, AppConfigTags.TAG, "If no internet connection", true);
            }
        }
    }

    private void uploadStoredResponseToServer () {
        Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "Getting all the responses from local  database", true);
        List<com.actiknow.liveaudit.model.Response> allResponses = db.getAllResponse ();
        for (final com.actiknow.liveaudit.model.Response responses : allResponses) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_SUBMITRESPONSE, true);
            final com.actiknow.liveaudit.model.Response finalResponse = responses;
            if (NetworkConnection.isNetworkAvailable (this)) {
                StringRequest strRequest2 = new StringRequest (Request.Method.POST, AppConfigURL.URL_SUBMITRESPONSE,
                        new com.android.volley.Response.Listener<String> () {
                            @Override
                            public void onResponse (String response) {
                                Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                                if (response != null) {
                                    try {
                                        JSONObject jsonObj = new JSONObject (response);
                                        int status = jsonObj.getInt (AppConfigTags.STATUS);
                                        if (status == 1)
                                            db.deleteResponse (responses.getResponse_id ());
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
                                Utils.showLog (Log.ERROR, AppConfigTags.TAG, error.toString (), true);
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams () throws AuthFailureError {
                        Map<String, String> params = new Hashtable<String, String> ();
                        params.put (AppConfigTags.ATM_UNIQUE_ID, finalResponse.getResponse_atm_unique_id ());
                        params.put (AppConfigTags.ATM_AGENCY_ID, String.valueOf (finalResponse.getResponse_agency_id ()));
                        params.put (AppConfigTags.AUDITOR_ID, String.valueOf (finalResponse.getResponse_auditor_id ()));
                        params.put (AppConfigTags.QUESTION_ID, String.valueOf (finalResponse.getResponse_question_id ()));
                        params.put (AppConfigTags.SWITCH_FLAG, String.valueOf (finalResponse.getResponse_switch_flag ()));
                        params.put (AppConfigTags.COMMENT, finalResponse.getResponse_comment ());
                        params.put (AppConfigTags.IMAGE1, finalResponse.getResponse_image1 ());
                        params.put (AppConfigTags.IMAGE2, finalResponse.getResponse_image2 ());
                        Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                        return params;
                    }
                };
                AppController.getInstance ().addToRequestQueue (strRequest2);
            } else {
                Utils.showLog (Log.WARN, AppConfigTags.TAG, "If no internet connection", true);
            }
        }
    }
}



