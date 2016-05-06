package com.actiknow.liveaudit.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actiknow.liveaudit.R;
import com.actiknow.liveaudit.app.AppController;
import com.actiknow.liveaudit.utils.AppConfigTags;
import com.actiknow.liveaudit.utils.AppConfigURL;
import com.actiknow.liveaudit.utils.Constants;
import com.actiknow.liveaudit.utils.NetworkConnection;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Actiknow on 09-03-2016.
 */
public class AtmDetailsActivity extends AppCompatActivity {

    Button btCctv;
    Button btAtmMachine;
    Button btAtmAc;
    Button btGuard;
    Button btSubmit;
    CheckBox cbCctv;
    CheckBox cbAtmMachine;
    CheckBox cbAtmAc;
    CheckBox cbGuard;
    TextView tvAtmName;
    TextView tvAtmBank;
    TextView tvAtmLocation;
    ImageView ivAtmImage;

    GoogleApiClient client;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView (R.layout.activity_atm_details);
        initView ();
        initListener ();
        initAdapter ();
        setUpNavigationDrawer ();

        client = new GoogleApiClient.Builder (this).addApi (AppIndex.API).build ();

        if (NetworkConnection.isNetworkAvailable (this)) {
            progressDialog = new ProgressDialog (this);
            progressDialog.setMessage ("Loading...");
            progressDialog.setCancelable (true);
            progressDialog.show ();
            Log.d ("URL", AppConfigURL.URL_GETATMDETAILS);
            StringRequest strRequest = new StringRequest (Request.Method.POST, AppConfigURL.URL_GETATMDETAILS,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {

                            int is_data_received = 0;
                            Log.d ("SERVER RESPONSE", response);
                            if (response != null) {
                                is_data_received = 1;
                                try {
                                    JSONObject jsonObj = new JSONObject (response);

                            } catch (JSONException e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                Log.e (AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER);
                            }

                            if (is_data_received != 0) {
                                progressDialog.dismiss ();
                                if (Constants.atm_cctv_check != 0)
                                    cbCctv.setChecked (true);
                                else
                                    cbCctv.setChecked (false);

                                if (Constants.atm_machine_check != 0)
                                    cbAtmMachine.setChecked (true);
                                else
                                    cbAtmMachine.setChecked (false);

                                if (Constants.atm_ac_check != 0)
                                    cbAtmAc.setChecked (true);
                                else
                                    cbAtmAc.setChecked (false);

                                if (Constants.atm_guard_check != 0)
                                    cbGuard.setChecked (true);
                                else
                                    cbGuard.setChecked (false);

                            }
                            if (is_data_received == 0) {
                                progressDialog.dismiss ();
                                Toast.makeText (AtmDetailsActivity.this, "Unable to retrieve details", Toast.LENGTH_SHORT).show ();
                            }
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Log.d ("TAG", error.toString ());
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams () {
                    Map<String, String> params = new HashMap<String, String> ();
                    params.put (AppConfigTags.ATM_ID, String.valueOf (Constants.atm_id));
                    return params;
                }
            };
            AppController.getInstance ().addToRequestQueue (strRequest);
        }
    }

    private void initView() {
        tvAtmName = (TextView)findViewById (R.id.tvAtmName);
        tvAtmBank = (TextView)findViewById (R.id.tvAtmBank);
        tvAtmLocation = (TextView)findViewById (R.id.tvAtmLocation);
        ivAtmImage = (ImageView)findViewById (R.id.ivAtmImage);
        btCctv = (Button) findViewById (R.id.btCctv);
        btAtmMachine = (Button) findViewById (R.id.btMachine);
        btAtmAc = (Button) findViewById (R.id.btAc);
        btGuard = (Button) findViewById (R.id.btGuard);
        btSubmit = (Button)findViewById (R.id.btSubmit);

        cbCctv = (CheckBox) findViewById (R.id.cbCctv);
        cbAtmMachine = (CheckBox) findViewById (R.id.cbMachine);
        cbAtmAc = (CheckBox) findViewById (R.id.cbAc);
        cbGuard = (CheckBox) findViewById (R.id.cbGuard);

    }

    private void initListener() {

        btCctv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.atm_checklist_item = 1;
                Intent intent = new Intent(AtmDetailsActivity.this, TakePictureActivity.class);
                startActivity (intent);
                overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        btAtmMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.atm_checklist_item = 2;
                Intent intent = new Intent(AtmDetailsActivity.this, TakePictureActivity.class);
                startActivity(intent);
                overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        btAtmAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.atm_checklist_item = 3;
                Intent intent = new Intent(AtmDetailsActivity.this, TakePictureActivity.class);
                startActivity(intent);
                overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        btGuard.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                Constants.atm_checklist_item = 4;
                Intent intent = new Intent (AtmDetailsActivity.this, TakePictureActivity.class);
                startActivity (intent);
                overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        btSubmit.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                if (NetworkConnection.isNetworkAvailable (AtmDetailsActivity.this)) {
                    Log.d ("URL", AppConfigURL.URL_REFRESHRESPONSE);
                    StringRequest strRequest = new StringRequest (Request.Method.POST, AppConfigURL.URL_REFRESHRESPONSE,
                            new Response.Listener<String> () {
                                @Override
                                public void onResponse (String response) {
                                    Log.d ("SERVER RESPONSE", response);
                                    if (response != null) {
                                        try {
                                            JSONObject jsonObj = new JSONObject (response);
                                            int status = jsonObj.getInt (AppConfigTags.STATUS);
                                            switch (status) {
                                                case 1:
                                                    cbCctv.setChecked (false);
                                                    cbAtmMachine.setChecked (false);
                                                    cbAtmAc.setChecked (false);
                                                    cbGuard.setChecked (false);
                                                    Toast.makeText (AtmDetailsActivity.this, "Thank you for submitting your response", Toast.LENGTH_SHORT).show ();
                                                    break;
                                                case 0:
                                                    Toast.makeText (AtmDetailsActivity.this, "Error occurred", Toast.LENGTH_SHORT).show ();
                                                    break;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace ();
                                        }
                                    } else {
                                        Log.e (AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER);
                                    }
                                }
                            },
                            new Response.ErrorListener () {
                                @Override
                                public void onErrorResponse (VolleyError error) {
                                    Log.d ("TAG", error.toString ());
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams () {
                            Map<String, String> params = new HashMap<String, String> ();
                            params.put (AppConfigTags.ATM_ID, String.valueOf (Constants.atm_id));
                            return params;
                        }
                    };
                    AppController.getInstance ().addToRequestQueue (strRequest);
                }
            }
        });
    }

    private void initAdapter() {
    }

    private void setUpNavigationDrawer () {
        Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar1);
        setSupportActionBar (toolbar);
        ActionBar actionBar = getSupportActionBar ();
        try {
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled (true);
            actionBar.setHomeButtonEnabled (true);
            actionBar.setTitle ("ATM Details");
            actionBar.setDisplayShowTitleEnabled (true);
        } catch (Exception ignored) {
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
                Uri.parse ("android-app://actiknow.com.atmncr/http/host/path")
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
                Uri.parse ("android-app://actiknow.com.atmncr/http/host/path")
        );
        AppIndex.AppIndexApi.end (client, viewAction);
        client.disconnect ();
    }

    @Override
    public void onResume(){
        super.onResume ();
        if (NetworkConnection.isNetworkAvailable (this)) {
            Log.d ("URL", AppConfigURL.URL_GETATMDETAILS);
            StringRequest strRequest = new StringRequest (Request.Method.POST, AppConfigURL.URL_GETATMDETAILS,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {

                            int is_data_received = 0;
                            Log.d ("SERVER RESPONSE", response);
                            if (response != null) {
                                is_data_received = 1;
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                } catch (JSONException e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                Log.e (AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER);
                            }

                            if (is_data_received != 0) {
                                if (Constants.atm_cctv_check != 0)
                                    cbCctv.setChecked (true);
                                else
                                    cbCctv.setChecked (false);

                                if (Constants.atm_machine_check != 0)
                                    cbAtmMachine.setChecked (true);
                                else
                                    cbAtmMachine.setChecked (false);

                                if (Constants.atm_ac_check != 0)
                                    cbAtmAc.setChecked (true);
                                else
                                    cbAtmAc.setChecked (false);

                                if (Constants.atm_guard_check != 0)
                                    cbGuard.setChecked (true);
                                else
                                    cbGuard.setChecked (false);

                            }
                            if (is_data_received == 0) {
                                Toast.makeText (AtmDetailsActivity.this, "Unable to retrieve details", Toast.LENGTH_SHORT).show ();
                            }
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Log.d ("TAG", error.toString ());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () {
                    Map<String, String> params = new HashMap<String, String> ();
                    params.put (AppConfigTags.ATM_ID, String.valueOf (Constants.atm_id));
                    return params;
                }
            };
            AppController.getInstance ().addToRequestQueue (strRequest);
        }
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater ().inflate (R.menu.menu_rest, menu);
        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId ()) {
            case android.R.id.home:
                finish ();
                overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
                break;
        }
        return super.onOptionsItemSelected (item);
    }

    @Override
    public void onBackPressed () {
        finish ();
        overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
