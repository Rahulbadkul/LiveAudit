package com.actiknow.liveaudit.model;

import android.util.Log;

/**
 * Created by Admin on 17-05-2016.
 */
public class Rating {
    private int rating_id, auditor_id, rating;
    private String atm_unique_id;

    public Rating () {
    }

    public Rating (int rating_id, int auditor_id, int rating, String atm_unique_id) {
        this.rating_id = rating_id;
        this.auditor_id = auditor_id;
        this.rating = rating;
        this.atm_unique_id = atm_unique_id;
    }

    public int getRating_id () {
        return rating_id;
    }

    public void setRating_id (int rating_id) {
        this.rating_id = rating_id;
        Log.d ("rating_id", "" + rating_id);
    }

    public int getAuditor_id () {
        return auditor_id;
    }

    public void setAuditor_id (int auditor_id) {
        this.auditor_id = auditor_id;
        Log.d ("auditor_id", "" + auditor_id);
    }

    public int getRating () {
        return rating;
    }

    public void setRating (int rating) {
        this.rating = rating;
        Log.d ("rating", "" + rating);
    }

    public String getAtm_unique_id () {
        return atm_unique_id;
    }

    public void setAtm_unique_id (String atm_unique_id) {
        this.atm_unique_id = atm_unique_id;
        Log.d ("atm_unique_id", atm_unique_id);
    }

}
