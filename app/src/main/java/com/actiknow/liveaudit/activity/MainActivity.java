
package com.actiknow.liveaudit.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import com.actiknow.liveaudit.helper.DatabaseHandler;
import com.actiknow.liveaudit.model.Atm;
import com.actiknow.liveaudit.model.Question;
import com.actiknow.liveaudit.service.LocationService;
import com.actiknow.liveaudit.utils.AppConfigTags;
import com.actiknow.liveaudit.utils.AppConfigURL;
import com.actiknow.liveaudit.utils.Constants;
import com.actiknow.liveaudit.utils.GPSTracker;
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

    public static int GEO_IMAGE_REQUEST_CODE = 1;
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
        getLatLong ();
        initService ();
        setUpNavigationDrawer ();
//        initLocationSettings ();

        if (Constants.splash_screen_first_time == 0 && Constants.auditor_id_main != 0)
            showSplashScreen ();

        if (Constants.auditor_id_main != 0) {
            getAtmListFromServer ();
            getQuestionListFromServer ();
            if (db.getAuditorLocationCount () > 0)
                uploadStoredAuditorLocationToServer ();
            if (db.getReportCount () > 0)
                uploadStoredReportsToServer ();
        }
        db.closeDB ();
    }

    private void initData () {
        db = new DatabaseHandler (getApplicationContext ());
        Utils.setTypefaceToAllViews (this, tvNoInternetConnection);
        adapter = new AllAtmAdapter (this, atmList);
        //    Constants.questionsList.clear ();
        listViewAllAtm.setAdapter (adapter);
        client = new GoogleApiClient.Builder (this).addApi (AppIndex.API).build ();
        dialogSplash = new Dialog (this, R.style.full_screen);
    }

    @Override
    protected void onResume () {

        super.onResume ();
        if (Constants.auditor_id_main != 0) {
            getAtmListFromServer ();
            getQuestionListFromServer ();
        }
        //     recreate ();
        //    Constants.questionsList.clear ();
        //    if (Constants.auditor_id_main != 0) {
        //        getQuestionListFromServer ();
        //    }
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
        Constants.auditor_agency_id = loginDetailsPref.getIntPref (MainActivity.this, LoginDetailsPref.AUDITOR_AGENCY_ID);
    }

    private void initService () {
        if (Constants.auditor_id_main != 0) {
            Intent mServiceIntent = new Intent (this, LocationService.class);
            startService (mServiceIntent);
        }
    }

    private void initView () {
        listViewAllAtm = (ListView) findViewById (R.id.lvAtmList);
        tvNoInternetConnection = (TextView) findViewById (R.id.tvNoIternetConnection);
        progressBar = (ProgressBar) findViewById (R.id.progressbar);
        btEnterManually = (Button) findViewById (R.id.btEnterManually);
    }

    private void getLatLong () {
        GPSTracker gps = new GPSTracker (MainActivity.this);
        if (gps.canGetLocation ()) {
            Constants.latitude = gps.getLatitude ();
            Constants.longitude = gps.getLongitude ();
            Log.e ("current lat long", "" + Constants.latitude + " " + Constants.longitude);
        } else {
            gps.showSettingsAlert ();
        }
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
                loginDetailsPref.putIntPref (MainActivity.this, LoginDetailsPref.AUDITOR_AGENCY_ID, 0);
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
                            atmList.clear ();
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
            Utils.sendRequest (strRequest);

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
                            db.deleteAllQuestion ();
                            Constants.questionsList.clear ();
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    JSONArray jsonArray = jsonObj.getJSONArray (AppConfigTags.QUESTIONS);
                                    for (int i = 0; i < jsonArray.length (); i++) {
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
            Utils.sendRequest (strRequest1);
        } else {
            getQuestionListFromLocalDatabase ();
        }
    }

    private void getQuestionListFromLocalDatabase () {
        Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "Getting all the questions from local database", true);
        Constants.questionsList.clear ();
        List<Question> allQuestions = db.getAllQuestions ();
        for (Question question : allQuestions)
            Constants.questionsList.add (question);
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
                    dialogEnterManually.dismiss ();

                    AlertDialog.Builder builder = new AlertDialog.Builder (MainActivity.this);
                    builder.setMessage ("Please take an image of the ATM Machine\nNote : This image will be Geotagged")
                            .setCancelable (false)
                            .setPositiveButton ("OK", new DialogInterface.OnClickListener () {
                                public void onClick (DialogInterface dialog, int id) {
                                    dialog.dismiss ();
                                    Constants.atm_location_in_manual = etEnterManuallyAtmLocation.getText ().toString ().toUpperCase ();
//                                    Constants.atm_unique_id = etEnterManuallyAtmId.getText ().toString ().toUpperCase ();

                                    Constants.report.setAtm_id (0);
                                    Constants.report.setAuditor_id (Constants.auditor_id_main);
                                    Constants.report.setAgency_id (Constants.auditor_agency_id);
                                    Constants.report.setAtm_unique_id (etEnterManuallyAtmId.getText ().toString ().toUpperCase ());
                                    Constants.report.setLatitude (String.valueOf (Constants.latitude));
                                    Constants.report.setLongitude (String.valueOf (Constants.longitude));

                                    Intent mIntent = null;
                                    if (Utils.isPackageExists (MainActivity.this, "com.google.android.camera")) {
                                        mIntent = new Intent ();
                                        mIntent.setPackage ("com.google.android.camera");
                                        mIntent.setAction (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                    } else {
                                        PackageManager packageManager = getPackageManager ();
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
                                    if (mIntent.resolveActivity (getPackageManager ()) != null)
                                        startActivityForResult (mIntent, MainActivity.GEO_IMAGE_REQUEST_CODE);
                                }
                            })
                            .setNegativeButton ("CANCEL", new DialogInterface.OnClickListener () {
                                public void onClick (DialogInterface dialog, int id) {
                                    dialog.dismiss ();
                                }
                            });
                    AlertDialog alert = builder.create ();
                    alert.show ();
                }
            }
        });
    }

    private void uploadStoredReportsToServer () {
        Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "Getting all the reports from local database", true);
        List<com.actiknow.liveaudit.model.Report> allReports = db.getAllReports ();
        for (com.actiknow.liveaudit.model.Report report : allReports) {
            final com.actiknow.liveaudit.model.Report finalReport = report;
            if (NetworkConnection.isNetworkAvailable (MainActivity.this)) {
                Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_SUBMITREPORT, true);
                StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_SUBMITREPORT,
                        new com.android.volley.Response.Listener<String> () {
                            @Override
                            public void onResponse (String response) {
                                Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                                if (response != null) {
                                    try {
                                        JSONObject jsonObj = new JSONObject (response);
                                        switch (jsonObj.getInt (AppConfigTags.STATUS)) {
                                            case 0:
                                                break;
                                            case 1:
                                                db.deleteReport (finalReport.getGeo_image_string ());
                                                break;
                                        }
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
                        params.put (AppConfigTags.ATM_ID, String.valueOf (finalReport.getAtm_id ()));
                        params.put (AppConfigTags.ATM_UNIQUE_ID, finalReport.getAtm_unique_id ());
                        params.put (AppConfigTags.ATM_AGENCY_ID, String.valueOf (finalReport.getAgency_id ()));
                        params.put (AppConfigTags.AUDITOR_ID, String.valueOf (finalReport.getAuditor_id ()));
                        params.put (AppConfigTags.ISSUES, finalReport.getIssues_json_array ());
                        params.put (AppConfigTags.GEO_IMAGE, finalReport.getGeo_image_string ());
                        params.put (AppConfigTags.LATITUDE, finalReport.getLatitude ());
                        params.put (AppConfigTags.LONGITUDE, finalReport.getLongitude ());
                        params.put (AppConfigTags.RATING, String.valueOf (finalReport.getRating ()));
                        params.put (AppConfigTags.SIGN_IMAGE, finalReport.getSignature_image_string ());

                        Log.e (AppConfigTags.ATM_ID, String.valueOf (finalReport.getAtm_id ()));
                        Log.e (AppConfigTags.ATM_UNIQUE_ID, finalReport.getAtm_unique_id ());
                        Log.e (AppConfigTags.ATM_AGENCY_ID, String.valueOf (finalReport.getAgency_id ()));
                        Log.e (AppConfigTags.AUDITOR_ID, String.valueOf (finalReport.getAuditor_id ()));
//                        Log.e (AppConfigTags.GEO_IMAGE, finalReport.getGeo_image_string ());
                        Log.e (AppConfigTags.LATITUDE, finalReport.getLatitude ());
                        Log.e (AppConfigTags.LONGITUDE, finalReport.getLongitude ());
                        Log.e (AppConfigTags.RATING, String.valueOf (finalReport.getRating ()));
//                        Log.e (AppConfigTags.SIGN_IMAGE, finalReport.getSignature_image_string ());

//                        Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                        return params;
                    }
                };
                Utils.sendRequest (strRequest1);
            }
        }
    }

    private void uploadStoredAuditorLocationToServer () {
        Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "Getting all the auditor_location from local database", true);
        List<com.actiknow.liveaudit.model.AuditorLocation> allAuditorLocations = db.getAllAuditorLocation ();
        for (com.actiknow.liveaudit.model.AuditorLocation auditorLocation : allAuditorLocations) {
            final com.actiknow.liveaudit.model.AuditorLocation finalAuditorLocation = auditorLocation;
            if (NetworkConnection.isNetworkAvailable (this)) {
                Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_SUBMITAUDITORLOCATION, true);
                StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_SUBMITAUDITORLOCATION,
                        new com.android.volley.Response.Listener<String> () {
                            @Override
                            public void onResponse (String response) {
                                Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                                if (response != null) {
                                    try {
                                        JSONObject jsonObj = new JSONObject (response);
                                        int status = jsonObj.getInt (AppConfigTags.STATUS);
                                        switch (status) {
                                            case 0://error
                                                break;
                                            case 1://success
                                                db.deleteAuditorLocation (finalAuditorLocation.getTime ());
                                                break;
                                        }
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
                        params.put (AppConfigTags.AUDITOR_ID, String.valueOf (finalAuditorLocation.getAuditor_id ()));
                        params.put (AppConfigTags.LATITUDE, finalAuditorLocation.getLatitude ());
                        params.put (AppConfigTags.LONGITUDE, finalAuditorLocation.getLongitude ());
                        params.put ("time", finalAuditorLocation.getTime ());
                        Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                        return params;
                    }
                };
                Utils.sendRequest (strRequest1);
            } else {
                Utils.showLog (Log.WARN, AppConfigTags.TAG, "If no internet connection", true);
            }
        }
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult (requestCode, resultCode, data);
        if (requestCode == GEO_IMAGE_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    Bitmap bp = (Bitmap) data.getExtras ().get ("data");
                    String image = Utils.bitmapToBase64 (bp);
                    Constants.report.setGeo_image_string (image);

                    Intent intent = new Intent (MainActivity.this, AllQuestionListActivity.class);
                    startActivity (intent);
                    overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case RESULT_CANCELED:
//                    Utils.showToast (MainActivity.this, "Please take an image");
                    break;
                default:
                    break;
            }
        }
    }
}



