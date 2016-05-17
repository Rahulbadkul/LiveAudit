package com.actiknow.liveaudit.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actiknow.liveaudit.R;
import com.actiknow.liveaudit.app.AppController;
import com.actiknow.liveaudit.utils.AppConfigTags;
import com.actiknow.liveaudit.utils.AppConfigURL;
import com.actiknow.liveaudit.utils.Constants;
import com.actiknow.liveaudit.utils.LoginDetailsPref;
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
import java.util.Map;


public class LoginActivity extends AppCompatActivity {


    TextView tvForgetPassword;
    TextView tvForgetUsername;
    EditText etUsername;
    EditText etPassword;

    Button btLogin;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);
        initView ();
        initListener ();
        initAdapter ();
    }

    private void initAdapter() {
    }

    private void initView(){
        tvForgetPassword = (TextView) findViewById (R.id.tvForgetPassword);
        etUsername = (EditText) findViewById (R.id.etUsername);
        etPassword = (EditText) findViewById (R.id.etPassword);
        btLogin = (Button) findViewById (R.id.btLogin);

        Typeface tf = SetTypeFace.getTypeface (this);
        SetTypeFace.applyTypeface (SetTypeFace.getParentView (tvForgetPassword), tf);
    }

    private void initListener(){
        tvForgetPassword.setOnTouchListener (new View.OnTouchListener () {
            @Override
            public boolean onTouch (View v, MotionEvent event) {
                if (event.getAction () == MotionEvent.ACTION_DOWN) {
                    tvForgetPassword.setTextColor (getResources ().getColor (R.color.colorPrimary));
                } else if (event.getAction () == MotionEvent.ACTION_UP) {
                    tvForgetPassword.setTextColor (getResources ().getColor (R.color.text_color_white));
                }
                return true;
            }
        });
        btLogin.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                int status_username = Utils.isValidEmail (etUsername.getText ().toString ());
                int status_password = Utils.isValidPassword (etPassword.getText ().toString ());
                if (status_username == 1 && status_password == 1 ) {


                    if (NetworkConnection.isNetworkAvailable (LoginActivity.this)) {
                        Log.d ("URL", AppConfigURL.URL_LOGIN);
                        progressDialog = new ProgressDialog (LoginActivity.this);
                        progressDialog.setMessage ("Please Wait...");
                        progressDialog.setCancelable (true);
                        progressDialog.show ();

                        StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_LOGIN,
                                new com.android.volley.Response.Listener<String> () {
                                    @Override
                                    public void onResponse (String response) {
                                        Log.d ("SERVER RESPONSE", response);
                                        if (response != null) {
                                            try {
                                                progressDialog.dismiss ();

                                                JSONObject jsonObj = new JSONObject (response);
                                                int status = jsonObj.getInt (AppConfigTags.STATUS);
                                                if (status == 1) {
                                                    Constants.auditor_id_main = jsonObj.getInt (AppConfigTags.AUDITOR_ID);
                                                    Constants.username = jsonObj.getString (AppConfigTags.AUDITOR_EMAIL);
                                                    Constants.auditor_name = jsonObj.getString (AppConfigTags.AUDITOR_NAME);

                                                    LoginDetailsPref loginDetailsPref = LoginDetailsPref.getInstance ();
                                                    loginDetailsPref.putStringPref (LoginActivity.this, LoginDetailsPref.AUDITOR_NAME, Constants.auditor_name);
                                                    loginDetailsPref.putStringPref (LoginActivity.this, LoginDetailsPref.USERNAME, Constants.username);
                                                    loginDetailsPref.putIntPref (LoginActivity.this, LoginDetailsPref.AUDITOR_ID, Constants.auditor_id_main);
                                                    Intent intent = new Intent (LoginActivity.this, MainActivity.class);
                                                    intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity (intent);

                                                } else {
                                                    progressDialog.dismiss ();
//                                                    final Snackbar snackbar = Snackbar
//                                                            .make (coordinatorLayout, "INVALID LOGIN CREDENTIALS ", Snackbar.LENGTH_LONG)
//                                                            .setAction ("DISMISS", new View.OnClickListener () {
//                                                                @Override
//                                                                public void onClick (View view) {
//                                                                }
//                                                            });
//                                                    snackbar.show ();
                                                    Toast.makeText (LoginActivity.this, "Invalid Login credentials", Toast.LENGTH_SHORT).show ();
                                                }


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
                                params.put (AppConfigTags.EMAIL, etUsername.getText ().toString ());
                                params.put (AppConfigTags.PASSWORD, etPassword.getText ().toString ());
                                //returning parameters

                                Log.d ("Param sent to the server", "" + params);
                                return params;
                            }
                        };
                        AppController.getInstance ().addToRequestQueue (strRequest1);
                    } else {
                        progressDialog.dismiss ();
                        Log.d ("Response", "Response to be done in no internet connection");
                        Toast.makeText (LoginActivity.this, "Please check your network connection", Toast.LENGTH_SHORT).show ();
                    }

/*
                    if (Constants.username.equalsIgnoreCase ("admin@gmail.com") && Constants.password.equalsIgnoreCase ("123456")) {

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Login Credentials", Toast.LENGTH_SHORT).show();
                        //             final Snackbar snackbar = Snackbar
                        //                     .make (coordinatorLayout, "INVALID LOGIN CREDENTIALS ", Snackbar.LENGTH_LONG)
                        //                     .setAction ("DISMISS", new View.OnClickListener () {
                        //                         @Override
                        //                         public void onClick (View view) {
                        //                         }
                        //                     });

                        //             snackbar.show ();
                        //              Toast.makeText (LoginActivity.this, "Login credentials are not correct", Toast.LENGTH_SHORT).show ();
                    }
*/


                }
                if (status_username == 0)
                    etUsername.setError ("Enter a Username");
                if (status_username == 2)
                    etUsername.setError ("Enter correct Username");
                if (status_password == 0)
                    etPassword.setError ("Enter the password");
            }
        });
    }
}