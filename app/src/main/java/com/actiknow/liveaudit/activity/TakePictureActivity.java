package com.actiknow.liveaudit.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.actiknow.liveaudit.R;
import com.actiknow.liveaudit.app.AppController;
import com.actiknow.liveaudit.service.AppLocationService;
import com.actiknow.liveaudit.utils.AppConfigTags;
import com.actiknow.liveaudit.utils.AppConfigURL;
import com.actiknow.liveaudit.utils.Constants;
import com.actiknow.liveaudit.utils.NetworkConnection;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by rahul jain on 09-03-2016.
 */
public class TakePictureActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Button btSubmit;
    EditText etComments;
    ImageView ivCameraImage;

    LocationManager locationManager;
    String provider;

    GoogleApiClient googleApiClient;
    private Context context;

    ProgressDialog progressDialog;

    AppLocationService appLocationService;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_camera);
        initView ();
        initListener ();
        initLocationSettings ();
        setUpNavigationDrawer ();

        appLocationService = new AppLocationService (TakePictureActivity.this);
        Location gpsLocation = appLocationService.getLocation (LocationManager.GPS_PROVIDER);

        if (gpsLocation != null) {
            Constants.resp_lat = gpsLocation.getLatitude ();
            Constants.resp_lng = gpsLocation.getLongitude ();
            Log.d ("TAG", "gpsLocation != null");
        }
        else
            Log.d ("TAG", "gpsLocation == null");
    }

    private void initLocationSettings () {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder (this).addApi (LocationServices.API).addConnectionCallbacks (this)
                    .addOnConnectionFailedListener (TakePictureActivity.this).build ();
            googleApiClient.connect ();
            LocationRequest locationRequest = LocationRequest.create ();
            locationRequest.setPriority (LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval (30 * 1000);
            locationRequest.setFastestInterval (5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder ().addLocationRequest (locationRequest);

            builder.setAlwaysShow (true);
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings (googleApiClient, builder.build ());
            result.setResultCallback (new ResultCallback<LocationSettingsResult> () {
                @Override
                public void onResult (LocationSettingsResult result) {
                    final Status status = result.getStatus ();
                    final LocationSettingsStates state = result.getLocationSettingsStates ();
                    switch (status.getStatusCode ()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult (TakePictureActivity.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    private void setUpNavigationDrawer () {
        Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar1);
        setSupportActionBar (toolbar);
        ActionBar actionBar = getSupportActionBar ();
        try {
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled (true);
            actionBar.setHomeButtonEnabled (true);
            actionBar.setTitle ("Capture image");
            actionBar.setDisplayShowTitleEnabled (true);
        } catch (Exception ignored) {
        }
    }

    private void initView() {
        ivCameraImage = (ImageView) findViewById(R.id.ivCameraImage);
        etComments = (EditText) findViewById (R.id.etComments);
        btSubmit = (Button) findViewById (R.id.btSubmit);
    }

    private void initListener() {
        ivCameraImage.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {


                final int CAMERA_ACTIVITY = 100;
                Intent mIntent = null;
                if (isPackageExists ("com.google.android.camera")) {
                    mIntent = new Intent ();
                    mIntent.setPackage ("com.google.android.camera");
                    mIntent.setAction (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                } else {
                    PackageManager packageManager = getPackageManager ();
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

                if (mIntent.resolveActivity (getPackageManager ()) != null) {
                    startActivityForResult (mIntent, CAMERA_ACTIVITY);
                }

//                startActivityForResult (mIntent, CAMERA_ACTIVITY);



//                Intent intent = new Intent (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult (intent, 0);
            }
        });

        btSubmit.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                if (NetworkConnection.isNetworkAvailable (TakePictureActivity.this)) {
                    progressDialog = new ProgressDialog (TakePictureActivity.this);
                    progressDialog.setMessage ("Loading...");
                    progressDialog.setCancelable (true);
                    progressDialog.show ();
                    Log.d ("URL", AppConfigURL.URL_SUBMITRESPONSE);
                    StringRequest strRequest = new StringRequest (Request.Method.POST, AppConfigURL.URL_SUBMITRESPONSE,
                            new Response.Listener<String> () {
                                @Override
                                public void onResponse (String response) {
                                    int is_data_received = 0;
                                    Log.d ("SERVER RESPONSE", response);
                                    if (response != null) {
                                        is_data_received = 1;
                                        try {
                                            JSONObject jsonObj = new JSONObject (response);
                                            int status = jsonObj.getInt (AppConfigTags.STATUS);
                                            switch (status){
                                                case 1  :
                                                    finish ();
                                                    break;
                                                case 0 :
                                                    Toast.makeText (TakePictureActivity.this, "Error occurred", Toast.LENGTH_SHORT).show ();
                                                break;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace ();
                                        }
                                    } else {
                                        Log.e (AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER);
                                    }

                                    if (is_data_received != 0) {
                                        progressDialog.dismiss ();
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
                            params.put (AppConfigTags.USER_ID, String.valueOf (Constants.user_id_main));
                            params.put (AppConfigTags.CHECKLIST_ITEM, String.valueOf (Constants.atm_checklist_item));
                            params.put (AppConfigTags.IMAGE_NAME, "image_name");
                            params.put (AppConfigTags.COMMENTS, etComments.getText ().toString ());
                            params.put (AppConfigTags.RESP_LAT, String.valueOf (Constants.resp_lat));
                            params.put (AppConfigTags.RESP_LNG, String.valueOf (Constants.resp_lng));
                            return params;
                        }
                    };
                    AppController.getInstance ().addToRequestQueue (strRequest);
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult (requestCode, resultCode, data);
        try{
            Bitmap bp = (Bitmap) data.getExtras ().get ("data");
            ivCameraImage.setImageBitmap (bp);
        }catch (Exception e){

        }
    }

    public boolean isPackageExists (String targetPackage) {
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getPackageManager ();
        packages = pm.getInstalledApplications (0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals (targetPackage)) return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ();
        Log.d ("on destroy", "in ondestroy function");
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

    @Override
    public void onLocationChanged (Location location) {
        // Setting Current Longitude
        // tvLongitude.setText("Longitude:" + location.getLongitude());
        // tvLatitude.setText("Latitude:" + location.getLatitude());

    }

    @Override
    public void onStatusChanged (String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled (String s) {

    }

    @Override
    public void onProviderDisabled (String s) {

    }

    @Override
    public void onConnected (Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended (int i) {

    }

    @Override
    public void onConnectionFailed (ConnectionResult connectionResult) {

    }

}