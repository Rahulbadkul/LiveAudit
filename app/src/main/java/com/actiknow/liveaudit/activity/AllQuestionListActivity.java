
package com.actiknow.liveaudit.activity;

import android.app.Dialog;
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
import com.actiknow.liveaudit.model.Question;
import com.actiknow.liveaudit.model.Rating;
import com.actiknow.liveaudit.model.Response;
import com.actiknow.liveaudit.utils.AppConfigTags;
import com.actiknow.liveaudit.utils.Constants;
import com.actiknow.liveaudit.utils.Utils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;


public class AllQuestionListActivity extends AppCompatActivity {

    TextView tvRatingNumber;
    SeekBar sbRating;
    Button btSubmit;
    ListView lvAllQuestions;

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
                for (int i = 0; i < Constants.total_questions; i++) {
                    final Response response;
                    response = Constants.responseList.get (i);
                    Log.d (AppConfigTags.ATM_UNIQUE_ID, response.getResponse_atm_unique_id ());
                    Log.d (AppConfigTags.ATM_AGENCY_ID, String.valueOf (response.getResponse_agency_id ()));
                    Log.d (AppConfigTags.AUDITOR_ID, String.valueOf (response.getResponse_auditor_id ()));
                    Log.d (AppConfigTags.QUESTION_ID, String.valueOf (response.getResponse_question_id ()));
                    Log.d (AppConfigTags.QUESTION, response.getResponse_question ());
                    Log.d (AppConfigTags.SWITCH_FLAG, String.valueOf (response.getResponse_switch_flag ()));
                    Log.d (AppConfigTags.COMMENT, response.getResponse_comment ());
                    Log.d (AppConfigTags.IMAGE1, response.getResponse_image1 ());
                    Log.d (AppConfigTags.IMAGE2, response.getResponse_image2 ());
                }

                final Rating rating = new Rating ();
                rating.setAtm_unique_id (Constants.atm_unique_id);
                rating.setAuditor_id (Constants.auditor_id_main);
                rating.setRating (sbRating.getProgress () / 10);
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
}



