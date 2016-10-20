
package com.actiknow.liveaudit.activity;

import android.Manifest;
import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actiknow.liveaudit.R;
import com.actiknow.liveaudit.adapter.AllAtmAdapter;
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
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static int GEO_IMAGE_REQUEST_CODE = 1;
    public static int PERMISSION_REQUEST_CODE = 11;
    ArrayList<Atm> tempArrayList = new ArrayList<Atm> ();
    TextView tvNoInternetConnection;
    ProgressBar progressBar;
    RecyclerView recyclerViewAllAtm;
    GoogleApiClient client;
    Dialog dialogSplash;
    Dialog dialogEnterManually;
    DatabaseHandler db;
    EditText etSearch;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText etEnterManuallyAtmId;
    EditText etEnterManuallyAtmLocation;
    //    private ActionBarDrawerToggle mDrawerToggle;
//    private DrawerLayout mDrawerLayout;
//    private RelativeLayout mDrawerPanel;
    RelativeLayout rlToolbarLogo;
    Bundle savedInstanceState;
    Toolbar toolbar;
    // Action Bar components
    private List<Atm> atmList = new ArrayList<> ();
    private AllAtmAdapter adapter;
    private AccountHeader headerResult;
    private Drawer result;
    private FloatingActionButton fabEnterManually;
    private FloatingActionMenu fabMenu;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        this.savedInstanceState = savedInstanceState;
        initView ();
        initPref ();
        isLogin ();
        initListener ();
        initData ();
        initDrawer ();
        getLatLong ();
        initService ();
        setUpNavigationDrawer ();
        checkPermissions ();

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

    private void initDrawer () {
        DrawerImageLoader.init (new AbstractDrawerImageLoader () {
            @Override
            public void set (ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with (imageView.getContext ()).load (uri).placeholder (placeholder).into (imageView);
            }

            @Override
            public void cancel (ImageView imageView) {
                Glide.clear (imageView);
            }

            @Override
            public Drawable placeholder (Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name ().equals (tag)) {
                    return DrawerUIUtils.getPlaceHolder (ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name ().equals (tag)) {
                    return new IconicsDrawable (ctx).iconText (" ").backgroundColorRes (com.mikepenz.materialdrawer.R.color.primary).sizeDp (56);
                } else if ("customUrlItem".equals (tag)) {
                    return new IconicsDrawable (ctx).iconText (" ").backgroundColorRes (R.color.md_white_1000).sizeDp (56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder (ctx, tag);
            }
        });
        headerResult = new AccountHeaderBuilder ()
                .withActivity (this)
                .withCompactStyle (true)
                .addProfiles (new ProfileDrawerItem ()
                        .withName (Constants.auditor_name)
                        .withEmail (Constants.username)
//                        .withIcon ("http://i.istockimg.com/file_thumbview_approve/64330137/3/stock-photo-64330137-a-icon-of-a-businessman-avatar-or-profile-pic.jpg"))
                        .withIcon (R.drawable.ic_profile_default))
                .withProfileImagesClickable (false)
                .withPaddingBelowHeader (false)
                .withSelectionListEnabledForSingleProfile (false)
                .withHeaderBackground (R.drawable.header)
                .withSavedInstance (savedInstanceState)
                .build ();

        result = new DrawerBuilder ()
                .withActivity (this)
                .withAccountHeader (headerResult)
//                .withToolbar (toolbar)
//                .withItemAnimator (new AlphaCrossFadeAnimator ())
                .addDrawerItems (
                        new PrimaryDrawerItem ().withName (R.string.drawer_item_home).withIcon (FontAwesome.Icon.faw_home).withIdentifier (1),
                        new DividerDrawerItem (),
                        new SecondaryDrawerItem ().withName (R.string.drawer_item_settings).withEnabled (false).withSelectable (false).withIdentifier (2),
                        new SecondaryDrawerItem ().withName (R.string.drawer_item_help_and_feedback).withEnabled (false).withSelectable (false).withIdentifier (3),
                        new SecondaryDrawerItem ().withName (R.string.drawer_item_about_liveaudit).withEnabled (false).withSelectable (false).withIdentifier (4),
                        new SecondaryDrawerItem ().withName (R.string.drawer_item_logout).withSelectable (false).withIdentifier (5)
                )
                .withSavedInstance (savedInstanceState)
                .withOnDrawerItemClickListener (new Drawer.OnDrawerItemClickListener () {
                    @Override
                    public boolean onItemClick (View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch ((int) drawerItem.getIdentifier ()) {
                            case 5:
                                showLogOutDialog ();
                                Utils.showLog (Log.ERROR, "position ", "" + position, true);
                                break;
                        }
                        return false;
                    }
                })
                .build ();
//        result.getActionBarDrawerToggle ().setDrawerIndicatorEnabled (false);


    }

    private void initData () {
        db = new DatabaseHandler (getApplicationContext ());
        new DrawerBuilder ().withActivity (this).build ();

        Utils.setTypefaceToAllViews (this, tvNoInternetConnection);
        adapter = new AllAtmAdapter (this, atmList);
        //    Constants.questionsList.clear ();

        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter (adapter);
        alphaAdapter.setDuration (700);
        recyclerViewAllAtm.setAdapter (alphaAdapter);
        recyclerViewAllAtm.setHasFixedSize (true);
        recyclerViewAllAtm.setLayoutManager (new LinearLayoutManager (this));
        recyclerViewAllAtm.setItemAnimator (new DefaultItemAnimator ());

        client = new GoogleApiClient.Builder (this).addApi (AppIndex.API).build ();
        dialogSplash = new Dialog (this, R.style.full_screen);
        swipeRefreshLayout.setRefreshing (false);
        fabMenu.setClosedOnTouchOutside (true);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState (outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState (outState);
        super.onSaveInstanceState (outState);
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
        fabEnterManually.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                fabMenu.close (true);
                showEnterManuallyDialog ();

            }
        });
        etSearch.addTextChangedListener (new TextWatcher () {
            @Override
            public void onTextChanged (CharSequence cs, int arg1, int arg2, int arg3) {
                int textlength = cs.length ();
                tempArrayList.clear ();

                for (Atm atm : atmList) {
                    if (textlength <= String.valueOf (atm.getAtm_unique_id ()).length ()) {
                        if (String.valueOf (atm.getAtm_unique_id ()).toLowerCase ().contains (cs.toString ().toLowerCase ())) {
                            tempArrayList.add (atm);
                        }
                    }
                }
                adapter = new AllAtmAdapter (MainActivity.this, tempArrayList);

                AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter (adapter);
                alphaAdapter.setDuration (700);
                alphaAdapter.setFirstOnly (true);
                alphaAdapter.setInterpolator (new OvershootInterpolator ());
//                SlideInLeftAnimationAdapter slideAdapter = new SlideInLeftAnimationAdapter (alphaAdapter);
//                slideAdapter.setDuration (500);
//                slideAdapter.setFirstOnly (true);
                recyclerViewAllAtm.setAdapter (alphaAdapter);
            }

            @Override
            public void beforeTextChanged (CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged (Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

//        adapter.SetOnItemClickListener (new AllAtmAdapter.OnItemClickListener () {
//
//            @Override
//            public void onItemClick (View v, int position) {
//                // do something with position
//            }
//        });


        rlToolbarLogo.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                if (result != null && ! result.isDrawerOpen ()) {
                    result.openDrawer ();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener (new SwipeRefreshLayout.OnRefreshListener () {
            @Override
            public void onRefresh () {
                swipeRefreshLayout.setRefreshing (false);
                recyclerViewAllAtm.setVisibility (View.GONE);
                progressBar.setVisibility (View.VISIBLE);
                getAtmListFromServer ();
                etSearch.setText ("");
                etSearch.setError (null);
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
        recyclerViewAllAtm = (RecyclerView) findViewById (R.id.rvAtmList);
        tvNoInternetConnection = (TextView) findViewById (R.id.tvNoIternetConnection);
        progressBar = (ProgressBar) findViewById (R.id.progressbar);
        etSearch = (EditText) findViewById (R.id.etAtmSearch);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById (R.id.swipe_refresh_layout);
        fabEnterManually = (FloatingActionButton) findViewById (R.id.fabEnterManually);
        fabMenu = (FloatingActionMenu) findViewById (R.id.fabMenu);
        rlToolbarLogo = (RelativeLayout) findViewById (R.id.toolbar_logo);
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
        if (result != null && result.isDrawerOpen ()) {
            result.closeDrawer ();
        } else {
            finish ();
            overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater ().inflate (R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService (Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem (R.id.action_search).getActionView ();
        if (null != searchView) {
            searchView.setSearchableInfo (searchManager.getSearchableInfo (getComponentName ()));
//            searchView.setIconifiedByDefault (false);
        }

//        final int searchBarId = searchView.getContext ().getResources ().getIdentifier ("android:id/search_bar", null, null);
//        LinearLayout searchBar = (LinearLayout) searchView.findViewById (searchBarId);

        EditText et = (EditText) searchView.findViewById (R.id.search_src_text);
//        et.getBackground ().setColorFilter (R.color.text_color_grey_dark,null);
//        et.setBackgroundColor (getResources ().getColor (R.color.text_color_grey_light)); // ← If you just want a color
//        et.setBackground (getResources ().getDrawable (R.drawable.layout_search_edittext));

        LinearLayout searchBar = (LinearLayout) searchView.findViewById (R.id.search_bar);
        searchBar.setLayoutTransition (new LayoutTransition ());

        searchView.setQueryHint ("Search ATM ID");
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener () {
            public boolean onQueryTextChange (String newText) {
                etSearch.setText (newText);
                return true;
            }

            public boolean onQueryTextSubmit (String query) {
                //Here u can get the value "query" which is entered in the search box.
                return true;
            }
        };
        searchView.setOnQueryTextListener (queryTextListener);

        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId ()) {
            case R.id.action_search:
//                if (etSearch.isShown ()) {
//                    etSearch.setVisibility (View.GONE);
//                    final Handler handler = new Handler ();
//                    handler.postDelayed (new Runnable () {
//                        @Override
//                        public void run () {
//                            etSearch.setText ("");
//                        }
//                    }, 1000);
//                } else {
//                    etSearch.setVisibility (View.VISIBLE);
//                }
                break;
        }
        Utils.hideSoftKeyboard (MainActivity.this);
        return super.onOptionsItemSelected (item);
    }

    private void showLogOutDialog () {
        TextView tvMessage;
        MaterialDialog dialog = new MaterialDialog.Builder (this)
                .customView (R.layout.dialog_basic, true)
                .positiveText (R.string.dialog_logout_positive)
                .negativeText (R.string.dialog_logout_negative)
                .onPositive (new MaterialDialog.SingleButtonCallback () {
                    @Override
                    public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss ();
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
                }).build ();

        tvMessage = (TextView) dialog.getCustomView ().findViewById (R.id.tvMessage);
        tvMessage.setText (R.string.dialog_logout_content);
        Utils.setTypefaceToAllViews (MainActivity.this, tvMessage);
        dialog.show ();
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
        toolbar = (Toolbar) findViewById (R.id.toolbar1);
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
                                recyclerViewAllAtm.setVisibility (View.VISIBLE);
                                tvNoInternetConnection.setVisibility (View.GONE);
                            } else if (is_data_received != 0 && json_array_len == 0) {
                                tvNoInternetConnection.setVisibility (View.VISIBLE);
                                progressBar.setVisibility (View.GONE);
                                recyclerViewAllAtm.setVisibility (View.GONE);
                            }
                            if (is_data_received == 0) {
                                progressBar.setVisibility (View.GONE);
                                recyclerViewAllAtm.setVisibility (View.GONE);
                                tvNoInternetConnection.setVisibility (View.VISIBLE);
                            }

                            swipeRefreshLayout.setRefreshing (false);
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            progressBar.setVisibility (View.GONE);
                            recyclerViewAllAtm.setVisibility (View.VISIBLE);
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
            Utils.sendRequest (strRequest, 30);

        } else {
            progressBar.setVisibility (View.GONE);
            recyclerViewAllAtm.setVisibility (View.VISIBLE);
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
        swipeRefreshLayout.setRefreshing (false);
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
            Utils.sendRequest (strRequest1, 30);
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
        final View positiveAction;
        MaterialDialog dialog = new MaterialDialog.Builder (this)
                .title (R.string.dialog_enter_manually_title)
                .customView (R.layout.dialog_enter_manually, true)
                .positiveText (R.string.dialog_enter_manually_positive)
                .negativeText (R.string.dialog_enter_manually_negative)
                .onPositive (new MaterialDialog.SingleButtonCallback () {
                    @Override
                    public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss ();
                        Constants.atm_location_in_manual = etEnterManuallyAtmLocation.getText ().toString ().toUpperCase ();

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
                }).build ();

        positiveAction = dialog.getActionButton (DialogAction.POSITIVE);
        etEnterManuallyAtmId = (EditText) dialog.getCustomView ().findViewById (R.id.etEnterManuallyAtmId);
        etEnterManuallyAtmLocation = (EditText) dialog.getCustomView ().findViewById (R.id.etEnterManuallyLocation);
        etEnterManuallyAtmId.addTextChangedListener (new TextWatcher () {
            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled (s.toString ().trim ().length () > 0);
            }

            @Override
            public void afterTextChanged (Editable s) {
            }
        });
        Utils.setTypefaceToAllViews (MainActivity.this, etEnterManuallyAtmId);
        int widgetColor = ThemeSingleton.get ().widgetColor;
        MDTintHelper.setTint (etEnterManuallyAtmId,
                widgetColor == 0 ? ContextCompat.getColor (this, R.color.accent) : widgetColor);
        MDTintHelper.setTint (etEnterManuallyAtmLocation,
                widgetColor == 0 ? ContextCompat.getColor (this, R.color.accent) : widgetColor);
        dialog.show ();
        positiveAction.setEnabled (false); // disabled by default
    }

    private void uploadStoredReportsToServer () {
        Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "Getting all the reports from local database", true);
        List<com.actiknow.liveaudit.model.Report> allReports = db.getAllReports ();
        for (com.actiknow.liveaudit.model.Report report : allReports) {
            final com.actiknow.liveaudit.model.Report finalReport = report;
            if (NetworkConnection.isNetworkAvailable (MainActivity.this)) {
                Utils.showLog (Log.INFO, "offline " + AppConfigTags.URL, AppConfigURL.URL_SUBMITREPORT, true);
                StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_SUBMITREPORT,
                        new com.android.volley.Response.Listener<String> () {
                            @Override
                            public void onResponse (String response) {
                                Utils.showLog (Log.INFO, "offline " + AppConfigTags.SERVER_RESPONSE, response, true);
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
                Utils.sendRequest (strRequest1, 60);
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
                Utils.sendRequest (strRequest1, 30);
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
                    File f = new File (Environment.getExternalStorageDirectory () + File.separator + "img.jpg");

                    Bitmap bp = null;
                    if (f.exists ()) {
                        bp = Utils.compressBitmap (BitmapFactory.decodeFile (f.getAbsolutePath ()), MainActivity.this);
                    }
//                    Bitmap bp = (Bitmap) data.getExtras ().get ("data");
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

    public void checkPermissions () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission (Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission (Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission (Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission (Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission (WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions (new String[] {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.INTERNET, Manifest.permission.RECEIVE_BOOT_COMPLETED, WRITE_EXTERNAL_STORAGE},
                        MainActivity.PERMISSION_REQUEST_CODE);
            }
/*
            if (checkSelfPermission (Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions (new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MainActivity.PERMISSION_REQUEST_CODE);
            }
            if (checkSelfPermission (Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions (new String[] {Manifest.permission.INTERNET}, MainActivity.PERMISSION_REQUEST_CODE);
            }
            if (checkSelfPermission (Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions (new String[] {Manifest.permission.RECEIVE_BOOT_COMPLETED,}, MainActivity.PERMISSION_REQUEST_CODE);
            }
            if (checkSelfPermission (Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions (new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.PERMISSION_REQUEST_CODE);
            }
  */

        }
    }

    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    boolean showRationale = shouldShowRequestPermissionRationale (permission);
                    if (! showRationale) {
                        Utils.showToast (this, "");
                        AlertDialog.Builder builder = new AlertDialog.Builder (MainActivity.this);
                        builder.setMessage ("Permission are required please enable them on the App Setting page")
                                .setCancelable (false)
                                .setPositiveButton ("OK", new DialogInterface.OnClickListener () {
                                    public void onClick (DialogInterface dialog, int id) {
                                        dialog.dismiss ();
                                        Intent intent = new Intent (Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts ("package", getPackageName (), null));
                                        startActivity (intent);
                                    }
                                });
                        AlertDialog alert = builder.create ();
                        alert.show ();
                        // user denied flagging NEVER ASK AGAIN
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting
                    } else if (Manifest.permission.CAMERA.equals (permission)) {
//                        Utils.showToast (this, "Camera Permission is required");
//                        showRationale (permission, R.string.permission_denied_contacts);
                        // user denied WITHOUT never ask again
                        // this is a good place to explain the user
                        // why you need the permission and ask if he want
                        // to accept it (the rationale)
                    } else if (Manifest.permission.ACCESS_FINE_LOCATION.equals (permission)) {
//                        Utils.showToast (this, "Location Permission is required");
//                        showRationale (permission, R.string.permission_denied_contacts);
                        // user denied WITHOUT never ask again
                        // this is a good place to explain the user
                        // why you need the permission and ask if he want
                        // to accept it (the rationale)
                    } else if (WRITE_EXTERNAL_STORAGE.equals (permission)) {
//                        Utils.showToast (this, "Write Permission is required");
//                        showRationale (permission, R.string.permission_denied_contacts);
                        // user denied WITHOUT never ask again
                        // this is a good place to explain the user
                        // why you need the permission and ask if he want
                        // to accept it (the rationale)
                    }
                }
            }


            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    @Override
    public void onRefresh () {
    }
}



