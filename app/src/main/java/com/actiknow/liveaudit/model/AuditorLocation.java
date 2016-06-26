package com.actiknow.liveaudit.model;

import android.util.Log;

import com.actiknow.liveaudit.utils.Utils;

public class AuditorLocation {
    private int auditor_location_id, auditor_id;
    private String latitude, longitude, time;

    public AuditorLocation () {
    }

    public AuditorLocation (int auditor_id, String latitude, String longitude, String time) {
        this.auditor_id = auditor_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public int getAuditor_location_id () {
        return auditor_location_id;
    }

    public void setAuditor_location_id (int auditor_location_id) {
        this.auditor_location_id = auditor_location_id;
        Utils.showLog (Log.DEBUG, "auditor_location_id", "" + auditor_location_id, false);
    }

    public int getAuditor_id () {
        return auditor_id;
    }

    public void setAuditor_id (int auditor_id) {
        this.auditor_id = auditor_id;
        Utils.showLog (Log.DEBUG, "auditor_id", "" + auditor_id, false);
    }

    public String getLatitude () {
        return latitude;
    }

    public void setLatitude (String latitude) {
        this.latitude = latitude;
        Utils.showLog (Log.DEBUG, "latitude", latitude, false);
    }

    public String getLongitude () {
        return longitude;
    }

    public void setLongitude (String longitude) {
        this.longitude = longitude;
        Utils.showLog (Log.DEBUG, "longitude", longitude, false);
    }

    public String getTime () {
        return time;
    }

    public void setTime (String time) {
        this.time = time;
        Utils.showLog (Log.DEBUG, "time", time, false);
    }
}
