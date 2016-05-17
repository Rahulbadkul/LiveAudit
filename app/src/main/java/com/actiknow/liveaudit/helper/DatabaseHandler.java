package com.actiknow.liveaudit.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.actiknow.liveaudit.model.Atms;
import com.actiknow.liveaudit.model.Questions;
import com.actiknow.liveaudit.model.Rating;
import com.actiknow.liveaudit.model.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = DatabaseHandler.class.getName ();

    // Database Version
    private static final int DATABASE_VERSION = 6;

    // Database Name
    private static final String DATABASE_NAME = "liveAudit";

    // Table Names
    private static final String TABLE_QUESTIONS = "questions";
    private static final String TABLE_ATMS = "atms";
    private static final String TABLE_RESPONSES = "responses";
    private static final String TABLE_RATINGS = "ratings";
//    private static final String TABLE_TODO_TAG = "todo_tags";

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

    // Question Table Create Statements
    // Todo table create statement
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


    // todo_tag table create statement
//    private static final String CREATE_TABLE_TODO_TAG = "CREATE TABLE "
//            + TABLE_TODO_TAG + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
//            + KEY_TODO_ID + " INTEGER," + KEY_TAG_ID + " INTEGER,"
//            + KEY_CREATED_AT + " DATETIME" + ")";



    public DatabaseHandler (Context context) {
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        // creating required tables
        db.execSQL (CREATE_TABLE_QUESTIONS);
        db.execSQL (CREATE_TABLE_ATMS);
        db.execSQL (CREATE_TABLE_RESPONSE);
        db.execSQL (CREATE_TABLE_RATING);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_ATMS);
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_RESPONSES);
        // create new tables
        onCreate (db);
    }

    // ------------------------ "questions" table methods ----------------//

    /**
     * Creating a question
     */
    public long createQuestion (Questions question) {
        SQLiteDatabase db = this.getWritableDatabase ();
        ContentValues values = new ContentValues ();
        values.put (KEY_ID, question.getQuestion_id ());
        values.put (KEY_QUESTION, question.getQuestion ());
        values.put (KEY_CREATED_AT, getDateTime ());
        long question_id = db.insert (TABLE_QUESTIONS, null, values);
        return question_id;
    }

    /**
     * get single question
     */
    public Questions getQuestion (long question_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT  * FROM " + TABLE_QUESTIONS + " WHERE " + KEY_ID + " = " + question_id;
        Log.e (LOG, selectQuery);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        Questions question = new Questions ();
        question.setQuestion_id (c.getInt (c.getColumnIndex (KEY_ID)));
        question.setQuestion ((c.getString (c.getColumnIndex (KEY_QUESTION))));
        return question;
    }

    /**
     * getting all questions
     */
    public List<Questions> getAllQuestions () {
        List<Questions> questions = new ArrayList<Questions> ();
        String selectQuery = "SELECT  * FROM " + TABLE_QUESTIONS;
//        Log.e (LOG, selectQuery);
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor c = db.rawQuery (selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst ()) {
            do {
                Questions question = new Questions ();
                question.setQuestion_id (c.getInt ((c.getColumnIndex (KEY_ID))));
                question.setQuestion ((c.getString (c.getColumnIndex (KEY_QUESTION))));
                // adding to question list
                questions.add (question);
            } while (c.moveToNext ());
        }
        return questions;
    }
    /**
     * getting questions count
     */
    public int getQuestionCount () {
        String countQuery = "SELECT  * FROM " + TABLE_QUESTIONS;
//        Log.e (LOG, countQuery);
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        return count;
    }

    /**
     * Updating a question
     */
    public int updateQuestion (Questions question) {
        SQLiteDatabase db = this.getWritableDatabase ();
        ContentValues values = new ContentValues ();
        values.put (KEY_QUESTION, question.getQuestion ());
        // updating row
        return db.update (TABLE_QUESTIONS, values, KEY_ID + " = ?",
                new String[] {String.valueOf (question.getQuestion_id ())});
    }

    /**
     * Deleting a question
     */
    public void deleteQuestion (long question_id) {
        SQLiteDatabase db = this.getWritableDatabase ();
        db.delete (TABLE_QUESTIONS, KEY_ID + " = ?",
                new String[] {String.valueOf (question_id)});
    }

    /**
     * Deleting all questions
     */
    public void deleteAllQuestion () {
        SQLiteDatabase db = this.getWritableDatabase ();
        db.execSQL ("delete from " + TABLE_QUESTIONS);
    }


    // ------------------------ "atms" table methods ----------------//

    /**
     * Creating a atm
     */
    public long createAtm (Atms atm) {
        SQLiteDatabase db = this.getWritableDatabase ();
        ContentValues values = new ContentValues ();
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

    /**
     * get single question
     */
    public Atms getAtm (long atm_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT  * FROM " + TABLE_ATMS + " WHERE " + KEY_ID + " = " + atm_id;
        Log.e (LOG, selectQuery);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        Atms atm = new Atms ();
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

    /**
     * getting all atms
     */
    public List<Atms> getAllAtms () {
        List<Atms> atms = new ArrayList<Atms> ();
        String selectQuery = "SELECT  * FROM " + TABLE_ATMS;
//        Log.e (LOG, selectQuery);
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor c = db.rawQuery (selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst ()) {
            do {
                Atms atm = new Atms ();
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

    /**
     * getting atm count
     */
    public int getAtmCount () {
        String countQuery = "SELECT  * FROM " + TABLE_ATMS;
//        Log.e (LOG, countQuery);
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        return count;
    }

    /**
     * Updating an atm
     */
    public int updateAtm (Atms atm) {
        SQLiteDatabase db = this.getWritableDatabase ();
        ContentValues values = new ContentValues ();
        values.put (KEY_ATM_ID, atm.getAtm_unique_id ());
        values.put (KEY_AGENCY_ID, atm.getAtm_agency_id ());
        values.put (KEY_LAST_AUDIT_DATE, atm.getAtm_last_audit_date ());
        values.put (KEY_BANK_NAME, atm.getAtm_bank_name ());
        values.put (KEY_ADDRESS, atm.getAtm_address ());
        values.put (KEY_CITY, atm.getAtm_city ());
        values.put (KEY_PINCODE, atm.getAtm_pincode ());
        // updating row
        return db.update (TABLE_ATMS, values, KEY_ID + " = ?",
                new String[] {String.valueOf (atm.getAtm_id ())});
    }

    /**
     * Deleting an atm
     */
    public void deleteAtm (long atm_id) {
        SQLiteDatabase db = this.getWritableDatabase ();
        db.delete (TABLE_ATMS, KEY_ID + " = ?",
                new String[] {String.valueOf (atm_id)});
    }

    /**
     * Deleting all atms
     */
    public void deleteAllAtms () {
        SQLiteDatabase db = this.getWritableDatabase ();
        db.execSQL ("delete from " + TABLE_ATMS);
    }


    // ------------------------ "Response" table methods ----------------//

    /**
     * Creating a atm
     */
    public long createResponse (Response response) {
        SQLiteDatabase db = this.getWritableDatabase ();
        ContentValues values = new ContentValues ();
        Log.d ("Response", "Response inserted successfully in the database");
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

    /**
     * get single response
     */
    public Response getResponse (long response_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT  * FROM " + TABLE_RESPONSES + " WHERE " + KEY_ID + " = " + response_id;
        Log.e (LOG, selectQuery);
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

    /**
     * getting all atms
     */
    public List<Response> getAllResponse () {
        List<Response> responses = new ArrayList<Response> ();
        String selectQuery = "SELECT  * FROM " + TABLE_RESPONSES;
//        Log.e (LOG, selectQuery);
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

    /**
     * getting atm count
     */
    public int getResponseCount () {
        String countQuery = "SELECT  * FROM " + TABLE_RESPONSES;
//        Log.e (LOG, countQuery);
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        return count;
    }

    /**
     * Updating an atm
     */
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

    /**
     * Deleting an atm
     */
    public void deleteResponse (long response_id) {
        SQLiteDatabase db = this.getWritableDatabase ();
        db.delete (TABLE_RESPONSES, KEY_ID + " = ?",
                new String[] {String.valueOf (response_id)});
    }

    /**
     * Deleting all atms
     */
    public void deleteAllResponses () {
        SQLiteDatabase db = this.getWritableDatabase ();
        db.execSQL ("delete from " + TABLE_RESPONSES);
    }


    // ------------------------ "rating" table methods ----------------//

    /**
     * Creating a question
     */
    public long createRating (Rating rating) {
        SQLiteDatabase db = this.getWritableDatabase ();
        ContentValues values = new ContentValues ();
        Log.d ("Rating", "Rating inserted successfully in the database");
        values.put (KEY_ATM_ID, rating.getAtm_unique_id ());
        values.put (KEY_AUDITOR_ID, rating.getAuditor_id ());
        values.put (KEY_RATING, rating.getRating ());
        values.put (KEY_CREATED_AT, getDateTime ());
        long rating_id = db.insert (TABLE_RATINGS, null, values);
        return rating_id;
    }

    /**
     * get single question
     */
    public Rating getRating (long rating_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT  * FROM " + TABLE_RATINGS + " WHERE " + KEY_ID + " = " + rating_id;
        Log.e (LOG, selectQuery);
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

    /**
     * getting all questions
     */
    public List<Rating> getAllRatings () {
        List<Rating> ratings = new ArrayList<Rating> ();
        String selectQuery = "SELECT  * FROM " + TABLE_RATINGS;
//        Log.e (LOG, selectQuery);
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor c = db.rawQuery (selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst ()) {
            do {
                Rating rating = new Rating ();
                rating.setRating_id (c.getInt (c.getColumnIndex (KEY_ID)));
                rating.setAuditor_id (c.getInt (c.getColumnIndex (KEY_AUDITOR_ID)));
                rating.setRating (c.getInt (c.getColumnIndex (KEY_RATING)));
                rating.setAtm_unique_id ((c.getString (c.getColumnIndex (KEY_ATM_ID))));
                // adding to question list
                ratings.add (rating);
            } while (c.moveToNext ());
        }
        return ratings;
    }

    /**
     * getting questions count
     */
    public int getRatingCount () {
        String countQuery = "SELECT  * FROM " + TABLE_RATINGS;
//        Log.e (LOG, countQuery);
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        return count;
    }

    /**
     * Updating a question
     */
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

    /**
     * Deleting a question
     */
    public void deleteRating (long rating_id) {
        SQLiteDatabase db = this.getWritableDatabase ();
        db.delete (TABLE_RATINGS, KEY_ID + " = ?",
                new String[] {String.valueOf (rating_id)});
    }

    /**
     * Deleting all questions
     */
    public void deleteAllRating () {
        SQLiteDatabase db = this.getWritableDatabase ();
        db.execSQL ("delete from " + TABLE_RATINGS);
    }


    // ------------------------ "tags" table methods ----------------//

    /**
     * Creating tag
     /
    public long createTag (Tag tag) {
        SQLiteDatabase db = this.getWritableDatabase ();

        ContentValues values = new ContentValues ();
        values.put (KEY_TAG_NAME, tag.getTagName ());
        values.put (KEY_CREATED_AT, getDateTime ());

        // insert row
        long tag_id = db.insert (TABLE_TAG, null, values);

        return tag_id;
    }

    /**
     * getting all tags
     /
    public List<Tag> getAllTags () {
        List<Tag> tags = new ArrayList<Tag> ();
        String selectQuery = "SELECT  * FROM " + TABLE_TAG;

        Log.e (LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor c = db.rawQuery (selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst ()) {
            do {
                Tag t = new Tag ();
                t.setId (c.getInt ((c.getColumnIndex (KEY_ID))));
                t.setTagName (c.getString (c.getColumnIndex (KEY_TAG_NAME)));

                // adding to tags list
                tags.add (t);
            } while (c.moveToNext ());
        }
        return tags;
    }

    /**
     * Updating a tag
     /
    public int updateTag (Tag tag) {
        SQLiteDatabase db = this.getWritableDatabase ();

        ContentValues values = new ContentValues ();
        values.put (KEY_TAG_NAME, tag.getTagName ());

        // updating row
        return db.update (TABLE_TAG, values, KEY_ID + " = ?",
                new String[] {String.valueOf (tag.getId ())});
    }

    /**
     * Deleting a tag
     /
    public void deleteTag (Tag tag, boolean should_delete_all_tag_todos) {
        SQLiteDatabase db = this.getWritableDatabase ();

        // before deleting tag
        // check if todos under this tag should also be deleted
        if (should_delete_all_tag_todos) {
            // get all todos under this tag
            List<Todo> allTagToDos = getAllToDosByTag (tag.getTagName ());

            // delete all todos
            for (Todo todo : allTagToDos) {
                // delete todo
                deleteToDo (todo.getId ());
            }
        }

        // now delete the tag
        db.delete (TABLE_TAG, KEY_ID + " = ?",
                new String[] {String.valueOf (tag.getId ())});
    }

    // ------------------------ "todo_tags" table methods ----------------//

    /**
     * Creating todo_tag
     /
    public long createTodoTag (long todo_id, long tag_id) {
        SQLiteDatabase db = this.getWritableDatabase ();

        ContentValues values = new ContentValues ();
        values.put (KEY_TODO_ID, todo_id);
        values.put (KEY_TAG_ID, tag_id);
        values.put (KEY_CREATED_AT, getDateTime ());

        long id = db.insert (TABLE_TODO_TAG, null, values);

        return id;
    }

    /**
     * Updating a todo tag
     /
    public int updateNoteTag (long id, long tag_id) {
        SQLiteDatabase db = this.getWritableDatabase ();

        ContentValues values = new ContentValues ();
        values.put (KEY_TAG_ID, tag_id);

        // updating row
        return db.update (TABLE_TODO, values, KEY_ID + " = ?",
                new String[] {String.valueOf (id)});
    }

    /**
     * Deleting a todo tag
     /
    public void deleteToDoTag (long id) {
        SQLiteDatabase db = this.getWritableDatabase ();
        db.delete (TABLE_TODO, KEY_ID + " = ?",
                new String[] {String.valueOf (id)});
    }

    */


    // closing database
    public void closeDB () {
        SQLiteDatabase db = this.getReadableDatabase ();
        if (db != null && db.isOpen ())
            db.close ();
    }
    /**
     * get datetime
     */
    private String getDateTime () {
        SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss", Locale.getDefault ());
        Date date = new Date ();
        return dateFormat.format (date);
    }
}