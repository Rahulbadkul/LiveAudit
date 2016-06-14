package com.actiknow.liveaudit.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.actiknow.liveaudit.model.Atm;
import com.actiknow.liveaudit.model.GeoImage;
import com.actiknow.liveaudit.model.Question;
import com.actiknow.liveaudit.model.Rating;
import com.actiknow.liveaudit.model.Response;
import com.actiknow.liveaudit.utils.AppConfigTags;
import com.actiknow.liveaudit.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 7;

    // Database Name
    private static final String DATABASE_NAME = "liveAudit";

    // Table Names
    private static final String TABLE_QUESTIONS = "questions";
    private static final String TABLE_ATMS = "atms";
    private static final String TABLE_RESPONSES = "responses";
    private static final String TABLE_RATINGS = "ratings";
    private static final String TABLE_GEO_IMAGE = "geo_image";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // QUESTIONS Table - column names
    private static final String KEY_QUESTION = "question";

    // ATMS Table - column names
    private static final String KEY_ATM_ID = "atm_id";
    private static final String KEY_AGENCY_ID = "agency_id";
    private static final String KEY_LAST_AUDIT_DATE = "last_audit_date";
    private static final String KEY_BANK_NAME = "bank_name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_CITY = "city";
    private static final String KEY_PINCODE = "pincode";

    // RESPONSE Table - column names
    private static final String KEY_AUDITOR_ID = "auditor_id";
    private static final String KEY_QUESTION_ID = "question_id";
    private static final String KEY_SWITCH_FLAG = "switch_flag";
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_IMAGE1 = "image1";
    private static final String KEY_IMAGE2 = "image2";

    // RATING Table - column names
    private static final String KEY_RATING = "rating";

    // RESPONSE_GEO_IMAGE Table - column names
    private static final String KEY_GEO_IMAGE = "geo_image";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    // Question table Create Statements
    private static final String CREATE_TABLE_QUESTIONS = "CREATE TABLE "
            + TABLE_QUESTIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY," +KEY_QUESTION
            + " TEXT," + KEY_CREATED_AT + " DATETIME" + ")";

    // ATM table create statement
    private static final String CREATE_TABLE_ATMS = "CREATE TABLE " + TABLE_ATMS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_AGENCY_ID + " INTEGER," + KEY_ATM_ID + " TEXT,"
            + KEY_LAST_AUDIT_DATE + " TEXT," + KEY_BANK_NAME + " TEXT," + KEY_ADDRESS + " TEXT," + KEY_CITY + " TEXT,"
            + KEY_PINCODE + " TEXT," + KEY_CREATED_AT + " DATETIME" + ")";

    // Response table create statement
    private static final String CREATE_TABLE_RESPONSE = "CREATE TABLE " + TABLE_RESPONSES
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ATM_ID + " TEXT," + KEY_AGENCY_ID + " INTEGER,"
            + KEY_AUDITOR_ID + " INTEGER," + KEY_QUESTION_ID + " INTEGER," + KEY_SWITCH_FLAG + " INTEGER,"
            + KEY_COMMENT + " TEXT," + KEY_IMAGE1 + " TEXT," + KEY_IMAGE2 + " TEXT," + KEY_CREATED_AT + " DATETIME" + ")";

    // Rating table create statement
    private static final String CREATE_TABLE_RATING = "CREATE TABLE " + TABLE_RATINGS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ATM_ID + " TEXT," + KEY_AUDITOR_ID + " INTEGER,"
            + KEY_RATING + " INTEGER," + KEY_CREATED_AT + " DATETIME" + ")";

    // Response_geo_image table create statement
    private static final String CREATE_TABLE_GEO_IMAGE = "CREATE TABLE " + TABLE_GEO_IMAGE
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ATM_ID + " TEXT," + KEY_AGENCY_ID + " INTEGER,"
            + KEY_AUDITOR_ID + " INTEGER," + KEY_GEO_IMAGE + " TEXT," + KEY_LATITUDE + " TEXT," + KEY_LONGITUDE + " TEXT," + KEY_CREATED_AT + " DATETIME" + ")";

    public DatabaseHandler (Context context) {
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL (CREATE_TABLE_QUESTIONS);
        db.execSQL (CREATE_TABLE_ATMS);
        db.execSQL (CREATE_TABLE_RESPONSE);
        db.execSQL (CREATE_TABLE_RATING);
        db.execSQL (CREATE_TABLE_GEO_IMAGE);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_ATMS);
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_RESPONSES);
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_RATINGS);
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_GEO_IMAGE);
        onCreate (db);
    }

    // ------------------------ "questions" table methods ----------------//

    public long createQuestion (Question question) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Creating Question", false);
        ContentValues values = new ContentValues ();
        values.put (KEY_ID, question.getQuestion_id ());
        values.put (KEY_QUESTION, question.getQuestion ());
        values.put (KEY_CREATED_AT, getDateTime ());
        long question_id = db.insert (TABLE_QUESTIONS, null, values);
        return question_id;
    }

    public Question getQuestion (long question_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT  * FROM " + TABLE_QUESTIONS + " WHERE " + KEY_ID + " = " + question_id;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get Question where ID = " + question_id, false);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        Question question = new Question ();
        question.setQuestion_id (c.getInt (c.getColumnIndex (KEY_ID)));
        question.setQuestion ((c.getString (c.getColumnIndex (KEY_QUESTION))));
        return question;
    }

    public List<Question> getAllQuestions () {
        List<Question> questions = new ArrayList<Question> ();
        String selectQuery = "SELECT  * FROM " + TABLE_QUESTIONS;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get all questions", false);
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor c = db.rawQuery (selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst ()) {
            do {
                Question question = new Question ();
                question.setQuestion_id (c.getInt ((c.getColumnIndex (KEY_ID))));
                question.setQuestion ((c.getString (c.getColumnIndex (KEY_QUESTION))));
                questions.add (question);
            } while (c.moveToNext ());
        }
        return questions;
    }

    public int getQuestionCount () {
        String countQuery = "SELECT  * FROM " + TABLE_QUESTIONS;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get total questions count : " + count, false);
        return count;
    }

    public int updateQuestion (Question question) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Update questions", false);
        ContentValues values = new ContentValues ();
        values.put (KEY_QUESTION, question.getQuestion ());
        // updating row
        return db.update (TABLE_QUESTIONS, values, KEY_ID + " = ?", new String[] {String.valueOf (question.getQuestion_id ())});
    }

    public void deleteQuestion (long question_id) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete question where ID = " + question_id, false);
        db.delete (TABLE_QUESTIONS, KEY_ID + " = ?",
                new String[] {String.valueOf (question_id)});
    }

    public void deleteAllQuestion () {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete all questions", false);
        db.execSQL ("delete from " + TABLE_QUESTIONS);
    }


    // ------------------------ "atms" table methods ----------------//

    public long createAtm (Atm atm) {
        SQLiteDatabase db = this.getWritableDatabase ();
        ContentValues values = new ContentValues ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Creating Atm", false);
        values.put (KEY_ID, atm.getAtm_id ());
        values.put (KEY_AGENCY_ID, atm.getAtm_agency_id ());
        values.put (KEY_ATM_ID, atm.getAtm_unique_id ());
        values.put (KEY_LAST_AUDIT_DATE, atm.getAtm_last_audit_date ());
        values.put (KEY_BANK_NAME, atm.getAtm_bank_name ());
        values.put (KEY_ADDRESS, atm.getAtm_address ());
        values.put (KEY_CITY, atm.getAtm_city ());
        values.put (KEY_PINCODE, atm.getAtm_pincode ());
        values.put (KEY_CREATED_AT, getDateTime ());
        long atm_id = db.insert (TABLE_ATMS, null, values);
        return atm_id;
    }

    public Atm getAtm (long atm_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT  * FROM " + TABLE_ATMS + " WHERE " + KEY_ID + " = " + atm_id;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get Atm where ID = " + atm_id, false);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        Atm atm = new Atm ();
        atm.setAtm_id (c.getInt (c.getColumnIndex (KEY_ID)));
        atm.setAtm_agency_id (c.getInt (c.getColumnIndex (KEY_AGENCY_ID)));
        atm.setAtm_unique_id (c.getString (c.getColumnIndex (KEY_ATM_ID)));
        atm.setAtm_last_audit_date (c.getString (c.getColumnIndex (KEY_LAST_AUDIT_DATE)));
        atm.setAtm_bank_name (c.getString (c.getColumnIndex (KEY_BANK_NAME)));
        atm.setAtm_address (c.getString (c.getColumnIndex (KEY_ADDRESS)));
        atm.setAtm_city (c.getString (c.getColumnIndex (KEY_CITY)));
        atm.setAtm_pincode (c.getString (c.getColumnIndex (KEY_PINCODE)));
        return atm;
    }

    public List<Atm> getAllAtms () {
        List<Atm> atms = new ArrayList<Atm> ();
        String selectQuery = "SELECT  * FROM " + TABLE_ATMS;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get all atm", false);
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor c = db.rawQuery (selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst ()) {
            do {
                Atm atm = new Atm ();
                atm.setAtm_id (c.getInt (c.getColumnIndex (KEY_ID)));
                atm.setAtm_agency_id (c.getInt (c.getColumnIndex (KEY_AGENCY_ID)));
                atm.setAtm_unique_id (c.getString (c.getColumnIndex (KEY_ATM_ID)));
                atm.setAtm_last_audit_date (c.getString (c.getColumnIndex (KEY_LAST_AUDIT_DATE)));
                atm.setAtm_bank_name (c.getString (c.getColumnIndex (KEY_BANK_NAME)));
                atm.setAtm_address (c.getString (c.getColumnIndex (KEY_ADDRESS)));
                atm.setAtm_city (c.getString (c.getColumnIndex (KEY_CITY)));
                atm.setAtm_pincode (c.getString (c.getColumnIndex (KEY_PINCODE)));
                atms.add (atm);
            } while (c.moveToNext ());
        }
        return atms;
    }

    public int getAtmCount () {
        String countQuery = "SELECT  * FROM " + TABLE_ATMS;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get total atm count : " + count, false);
        return count;
    }

    public int updateAtm (Atm atm) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Update atm", false);
        ContentValues values = new ContentValues ();
        values.put (KEY_ATM_ID, atm.getAtm_unique_id ());
        values.put (KEY_AGENCY_ID, atm.getAtm_agency_id ());
        values.put (KEY_LAST_AUDIT_DATE, atm.getAtm_last_audit_date ());
        values.put (KEY_BANK_NAME, atm.getAtm_bank_name ());
        values.put (KEY_ADDRESS, atm.getAtm_address ());
        values.put (KEY_CITY, atm.getAtm_city ());
        values.put (KEY_PINCODE, atm.getAtm_pincode ());
        // updating row
        return db.update (TABLE_ATMS, values, KEY_ID + " = ?", new String[] {String.valueOf (atm.getAtm_id ())});
    }

    public void deleteAtm (long atm_id) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete atm where ID = " + atm_id, false);
        db.delete (TABLE_ATMS, KEY_ID + " = ?", new String[] {String.valueOf (atm_id)});
    }

    public void deleteAllAtms () {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete all atm", false);
        db.execSQL ("delete from " + TABLE_ATMS);
    }


    // ------------------------ "Response" table methods ----------------//

    public long createResponse (Response response) {
        SQLiteDatabase db = this.getWritableDatabase ();
        ContentValues values = new ContentValues ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Response inserted successfully in the database", false);
        values.put (KEY_ATM_ID, response.getResponse_atm_unique_id ());
        values.put (KEY_AGENCY_ID, response.getResponse_agency_id ());
        values.put (KEY_AUDITOR_ID, response.getResponse_auditor_id ());
        values.put (KEY_QUESTION_ID, response.getResponse_question_id ());
        values.put (KEY_SWITCH_FLAG, response.getResponse_switch_flag ());
        values.put (KEY_COMMENT, response.getResponse_comment ());
        values.put (KEY_IMAGE1, response.getResponse_image1 ());
        values.put (KEY_IMAGE2, response.getResponse_image2 ());
        values.put (KEY_CREATED_AT, getDateTime ());
        long response_id = db.insert (TABLE_RESPONSES, null, values);
        return response_id;
    }

    public Response getResponse (long response_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT  * FROM " + TABLE_RESPONSES + " WHERE " + KEY_ID + " = " + response_id;
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        Response response = new Response ();
        response.setResponse_id (c.getInt (c.getColumnIndex (KEY_ID)));
        response.setResponse_agency_id (c.getInt (c.getColumnIndex (KEY_AGENCY_ID)));
        response.setResponse_atm_unique_id (c.getString (c.getColumnIndex (KEY_ATM_ID)));
        response.setResponse_auditor_id (c.getInt (c.getColumnIndex (KEY_AUDITOR_ID)));
        response.setResponse_question_id (c.getInt (c.getColumnIndex (KEY_QUESTION_ID)));
        response.setResponse_switch_flag (c.getInt (c.getColumnIndex (KEY_SWITCH_FLAG)));
        response.setResponse_comment (c.getString (c.getColumnIndex (KEY_COMMENT)));
        response.setResponse_image1 (c.getString (c.getColumnIndex (KEY_IMAGE1)));
        response.setResponse_image2 (c.getString (c.getColumnIndex (KEY_IMAGE2)));
        return response;
    }

    public List<Response> getAllResponse () {
        List<Response> responses = new ArrayList<Response> ();
        String selectQuery = "SELECT  * FROM " + TABLE_RESPONSES;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor c = db.rawQuery (selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst ()) {
            do {
                Response response = new Response ();
                response.setResponse_id (c.getInt (c.getColumnIndex (KEY_ID)));
                response.setResponse_agency_id (c.getInt (c.getColumnIndex (KEY_AGENCY_ID)));
                response.setResponse_atm_unique_id (c.getString (c.getColumnIndex (KEY_ATM_ID)));
                response.setResponse_auditor_id (c.getInt (c.getColumnIndex (KEY_AUDITOR_ID)));
                response.setResponse_question_id (c.getInt (c.getColumnIndex (KEY_QUESTION_ID)));
                response.setResponse_switch_flag (c.getInt (c.getColumnIndex (KEY_SWITCH_FLAG)));
                response.setResponse_comment (c.getString (c.getColumnIndex (KEY_COMMENT)));
                response.setResponse_image1 (c.getString (c.getColumnIndex (KEY_IMAGE1)));
                response.setResponse_image2 (c.getString (c.getColumnIndex (KEY_IMAGE2)));
                responses.add (response);
            } while (c.moveToNext ());
        }
        return responses;
    }

    public int getResponseCount () {
        String countQuery = "SELECT  * FROM " + TABLE_RESPONSES;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        return count;
    }

    public int updateResponse (Response response) {
        SQLiteDatabase db = this.getWritableDatabase ();
        ContentValues values = new ContentValues ();
        values.put (KEY_ATM_ID, response.getResponse_atm_unique_id ());
        values.put (KEY_AGENCY_ID, response.getResponse_agency_id ());
        values.put (KEY_AUDITOR_ID, response.getResponse_auditor_id ());
        values.put (KEY_QUESTION_ID, response.getResponse_question_id ());
        values.put (KEY_SWITCH_FLAG, response.getResponse_switch_flag ());
        values.put (KEY_COMMENT, response.getResponse_comment ());
        values.put (KEY_IMAGE1, response.getResponse_image1 ());
        values.put (KEY_IMAGE2, response.getResponse_image2 ());
        // updating row
        return db.update (TABLE_RESPONSES, values, KEY_ID + " = ?",
                new String[] {String.valueOf (response.getResponse_id ())});
    }

    public void deleteResponse (long response_id) {
        SQLiteDatabase db = this.getWritableDatabase ();
        db.delete (TABLE_RESPONSES, KEY_ID + " = ?",
                new String[] {String.valueOf (response_id)});
    }

    public void deleteAllResponses () {
        SQLiteDatabase db = this.getWritableDatabase ();
        db.execSQL ("delete from " + TABLE_RESPONSES);
    }


    // ------------------------ "rating" table methods ----------------//

    public long createRating (Rating rating) {
        SQLiteDatabase db = this.getWritableDatabase ();
        ContentValues values = new ContentValues ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Rating inserted successfully in the database", false);
        values.put (KEY_ATM_ID, rating.getAtm_unique_id ());
        values.put (KEY_AUDITOR_ID, rating.getAuditor_id ());
        values.put (KEY_RATING, rating.getRating ());
        values.put (KEY_CREATED_AT, getDateTime ());
        long rating_id = db.insert (TABLE_RATINGS, null, values);
        return rating_id;
    }

    public Rating getRating (long rating_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT  * FROM " + TABLE_RATINGS + " WHERE " + KEY_ID + " = " + rating_id;
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        Rating rating = new Rating ();
        rating.setRating_id (c.getInt (c.getColumnIndex (KEY_ID)));
        rating.setAuditor_id (c.getInt (c.getColumnIndex (KEY_AUDITOR_ID)));
        rating.setRating (c.getInt (c.getColumnIndex (KEY_RATING)));
        rating.setAtm_unique_id ((c.getString (c.getColumnIndex (KEY_ATM_ID))));
        return rating;
    }

    public List<Rating> getAllRatings () {
        List<Rating> ratings = new ArrayList<Rating> ();
        String selectQuery = "SELECT  * FROM " + TABLE_RATINGS;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor c = db.rawQuery (selectQuery, null);
        if (c.moveToFirst ()) {
            do {
                Rating rating = new Rating ();
                rating.setRating_id (c.getInt (c.getColumnIndex (KEY_ID)));
                rating.setAuditor_id (c.getInt (c.getColumnIndex (KEY_AUDITOR_ID)));
                rating.setRating (c.getInt (c.getColumnIndex (KEY_RATING)));
                rating.setAtm_unique_id ((c.getString (c.getColumnIndex (KEY_ATM_ID))));
                ratings.add (rating);
            } while (c.moveToNext ());
        }
        return ratings;
    }

    public int getRatingCount () {
        String countQuery = "SELECT  * FROM " + TABLE_RATINGS;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        return count;
    }

    public int updateRating (Rating rating) {
        SQLiteDatabase db = this.getWritableDatabase ();
        ContentValues values = new ContentValues ();
        values.put (KEY_AUDITOR_ID, rating.getAuditor_id ());
        values.put (KEY_ATM_ID, rating.getAtm_unique_id ());
        values.put (KEY_RATING, rating.getRating ());
        // updating row
        return db.update (TABLE_RATINGS, values, KEY_ID + " = ?",
                new String[] {String.valueOf (rating.getRating_id ())});
    }

    public void deleteRating (long rating_id) {
        SQLiteDatabase db = this.getWritableDatabase ();
        db.delete (TABLE_RATINGS, KEY_ID + " = ?",
                new String[] {String.valueOf (rating_id)});
    }

    public void deleteAllRating () {
        SQLiteDatabase db = this.getWritableDatabase ();
        db.execSQL ("delete from " + TABLE_RATINGS);
    }

    // ------------------------ "response geo image" table methods ----------------//

    public long createGeoImage (GeoImage geoImage) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Creating GeoImage", false);
        ContentValues values = new ContentValues ();
        values.put (KEY_AUDITOR_ID, geoImage.getAuditor_id ());
        values.put (KEY_AGENCY_ID, geoImage.getAgency_id ());
        values.put (KEY_ATM_ID, geoImage.getAtm_unique_id ());
        values.put (KEY_GEO_IMAGE, geoImage.getGeo_image_string ());
        values.put (KEY_LATITUDE, geoImage.getLatitude ());
        values.put (KEY_LONGITUDE, geoImage.getLongitude ());
        values.put (KEY_CREATED_AT, getDateTime ());
        long geo_image_id = db.insert (TABLE_GEO_IMAGE, null, values);
        return geo_image_id;
    }

    public GeoImage getGeoImage (long geo_image_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT  * FROM " + TABLE_GEO_IMAGE + " WHERE " + KEY_ID + " = " + geo_image_id;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get Geo Image where ID = " + geo_image_id, false);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        GeoImage geoImage = new GeoImage ();
        geoImage.setAuditor_id (c.getInt (c.getColumnIndex (KEY_AUDITOR_ID)));
        geoImage.setAtm_unique_id (c.getString (c.getColumnIndex (KEY_ATM_ID)));
        geoImage.setAgency_id (c.getInt (c.getColumnIndex (KEY_AGENCY_ID)));
        geoImage.setGeo_image_string (c.getString (c.getColumnIndex (KEY_GEO_IMAGE)));
        geoImage.setLatitude (c.getString (c.getColumnIndex (KEY_LATITUDE)));
        geoImage.setLongitude (c.getString (c.getColumnIndex (KEY_LONGITUDE)));
        return geoImage;
    }

    public List<GeoImage> getAllGeoImages () {
        List<GeoImage> geoImages = new ArrayList<GeoImage> ();
        String selectQuery = "SELECT  * FROM " + TABLE_GEO_IMAGE;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get all Geo Images", false);
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor c = db.rawQuery (selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst ()) {
            do {
                GeoImage geoImage = new GeoImage ();
                geoImage.setAuditor_id (c.getInt (c.getColumnIndex (KEY_AUDITOR_ID)));
                geoImage.setAtm_unique_id (c.getString (c.getColumnIndex (KEY_ATM_ID)));
                geoImage.setAgency_id (c.getInt (c.getColumnIndex (KEY_AGENCY_ID)));
                geoImage.setGeo_image_string (c.getString (c.getColumnIndex (KEY_GEO_IMAGE)));
                geoImage.setLatitude (c.getString (c.getColumnIndex (KEY_LATITUDE)));
                geoImage.setLongitude (c.getString (c.getColumnIndex (KEY_LONGITUDE)));
                geoImages.add (geoImage);
            } while (c.moveToNext ());
        }
        return geoImages;
    }

    public int getGeoImageCount () {
        String countQuery = "SELECT  * FROM " + TABLE_GEO_IMAGE;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get total geo images count : " + count, false);
        return count;
    }

    public int updateGeoImage (GeoImage geoImage) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Update geo image", false);
        ContentValues values = new ContentValues ();
        values.put (KEY_AUDITOR_ID, geoImage.getAuditor_id ());
        values.put (KEY_AGENCY_ID, geoImage.getAgency_id ());
        values.put (KEY_ATM_ID, geoImage.getAtm_unique_id ());
        values.put (KEY_GEO_IMAGE, geoImage.getGeo_image_string ());
        values.put (KEY_LATITUDE, geoImage.getLatitude ());
        values.put (KEY_LONGITUDE, geoImage.getLongitude ());

        // updating row
        return db.update (TABLE_GEO_IMAGE, values, KEY_ID + " = ?", new String[] {String.valueOf (geoImage.getResponse_geo_image_id ())});
    }

    public void deleteGeoImage (String geo_image_string) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete geo image where geo image string = " + geo_image_string, false);
        db.delete (TABLE_GEO_IMAGE, KEY_GEO_IMAGE + " = ?",
                new String[] {geo_image_string});
    }

    public void deleteAllGeoImages () {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete all geo images", false);
        db.execSQL ("delete from " + TABLE_GEO_IMAGE);
    }


    public void closeDB () {
        SQLiteDatabase db = this.getReadableDatabase ();
        if (db != null && db.isOpen ())
            db.close ();
    }

    private String getDateTime () {
        SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss", Locale.getDefault ());
        Date date = new Date ();
        return dateFormat.format (date);
    }
}