package com.actiknow.liveaudit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actiknow.liveaudit.R;
import com.actiknow.liveaudit.utils.Constants;
import com.actiknow.liveaudit.utils.Utils;


public class OfflineActivity extends AppCompatActivity {


    EditText etOfflineAtmid;
    Button btOfflineContinue;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_offline);
        initView ();
        initListener ();
        initAdapter ();
    }

    private void initAdapter () {
    }

    private void initView () {
        etOfflineAtmid = (EditText) findViewById (R.id.etOfflineAtmid);
        btOfflineContinue = (Button) findViewById (R.id.btOfflineContinue);
    }

    private void initListener () {
       /*
        etOfflineAtmid.addTextChangedListener (new TextWatcher () {
            @Override
            public void afterTextChanged (Editable mEdit) {
            }

            public void beforeTextChanged (CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged (CharSequence s, int start, int before, int count) {
            }
        });
     */
        btOfflineContinue.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                int status_atmid = Utils.isValidPassword (Constants.password);
                if (status_atmid == 1) {
                    Toast.makeText (OfflineActivity.this, "Invalid Login Credentials", Toast.LENGTH_SHORT).show ();
                }
                if (status_atmid == 0)
                    etOfflineAtmid.setError ("Enter the ATM ID");

            }
        });
    }
}