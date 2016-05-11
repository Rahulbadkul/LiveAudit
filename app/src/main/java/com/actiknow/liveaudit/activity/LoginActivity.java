package com.actiknow.liveaudit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actiknow.liveaudit.R;
import com.actiknow.liveaudit.utils.Constants;
import com.actiknow.liveaudit.utils.LoginDetailsPref;
import com.actiknow.liveaudit.utils.Utils;


public class LoginActivity extends AppCompatActivity {


    TextView tvForgetPassword;
    TextView tvForgetUsername;
    EditText etUsername;
    EditText etPassword;

    Button btLogin;

//hello

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
        etUsername.addTextChangedListener (new TextWatcher() {
            @Override
            public void afterTextChanged (Editable mEdit) {
                Constants.username = mEdit.toString ();
            }
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged (CharSequence s, int start, int before, int count) {
            }
        });
        etPassword.addTextChangedListener (new TextWatcher() {
            @Override
            public void afterTextChanged (Editable mEdit) {
                Constants.password = mEdit.toString ();
            }

            public void beforeTextChanged (CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged (CharSequence s, int start, int before, int count) {
            }
        });
        btLogin.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                int status_username = Utils.isValidEmail(Constants.username);
                int status_password = Utils.isValidPassword (Constants.password);
                if (status_username == 1 && status_password == 1 ) {
                    if (Constants.username.equalsIgnoreCase ("admin@gmail.com") && Constants.password.equalsIgnoreCase ("123456")) {
                        LoginDetailsPref loginDetailsPref = LoginDetailsPref.getInstance ();
                        loginDetailsPref.putStringPref (LoginActivity.this, LoginDetailsPref.USERNAME, Constants.username);
                        loginDetailsPref.putStringPref (LoginActivity.this, LoginDetailsPref.PASSWORD, Constants.password);
                        loginDetailsPref.putIntPref (LoginActivity.this, LoginDetailsPref.AUDITOR_ID, Constants.auditor_id_main);

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