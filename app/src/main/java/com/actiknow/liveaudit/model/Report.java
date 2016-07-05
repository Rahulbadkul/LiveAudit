package com.actiknow.liveaudit.model;

import android.util.Log;

import com.actiknow.liveaudit.utils.Utils;

public class Report {
    private int report_id, atm_id, auditor_id, agency_id, rating;
    private String atm_unique_id, issues_json_array, geo_image_string, latitude, longitude, signature_image_string;

    public Report () {
    }

    public Report (int report_id, int atm_id, int auditor_id, int rating, int agency_id,
                   String atm_unique_id, String issues_json_array, String geo_image_string,
                   String latitude, String longitude, String signature_image_string) {
        this.report_id = report_id;
        this.auditor_id = auditor_id;
        this.agency_id = agency_id;
        this.rating = rating;
        this.atm_unique_id = atm_unique_id;
        this.issues_json_array = issues_json_array;
        this.geo_image_string = geo_image_string;
        this.latitude = latitude;
        this.longitude = longitude;
        this.signature_image_string = signature_image_string;
    }

    public int getReport_id () {
        return report_id;
    }

    public void setReport_id (int report_id) {
        this.report_id = report_id;
        Utils.showLog (Log.DEBUG, "report_id", "" + report_id, false);
    }

    public int getAtm_id () {
        return atm_id;
    }

    public void setAtm_id (int atm_id) {
        this.atm_id = atm_id;
        Utils.showLog (Log.DEBUG, "atm_id", "" + atm_id, false);
    }

    public int getAuditor_id () {
        return auditor_id;
    }

    public void setAuditor_id (int auditor_id) {
        this.auditor_id = auditor_id;
        Utils.showLog (Log.DEBUG, "auditor_id", "" + auditor_id, false);
    }

    public int getAgency_id () {
        return agency_id;
    }

    public void setAgency_id (int agency_id) {
        this.agency_id = agency_id;
        Utils.showLog (Log.DEBUG, "agency_id", "" + agency_id, false);
    }

    public int getRating () {
        return rating;
    }

    public void setRating (int rating) {
        this.rating = rating;
        Utils.showLog (Log.DEBUG, "agenid", "" + agency_id, false);
    }

    public String getIssues_json_array () {
        return issues_json_array;
    }

    public void setIssues_json_array (String issues_json_array) {
        this.issues_json_array = issues_json_array;
        Utils.showLog (Log.DEBUG, "issues_json_array", issues_json_array, false);
    }

    public String getAtm_unique_id () {
        return atm_unique_id;
    }

    public void setAtm_unique_id (String atm_unique_id) {
        this.atm_unique_id = atm_unique_id;
        Utils.showLog (Log.DEBUG, "atm_unique_id", atm_unique_id, false);
    }

    public String getGeo_image_string () {
        return geo_image_string;
    }

    public void setGeo_image_string (String geo_image_string) {
        this.geo_image_string = geo_image_string;
        Utils.showLog (Log.DEBUG, "geo_image_string", geo_image_string, false);
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

    public String getSignature_image_string () {
        return signature_image_string;
    }

    public void setSignature_image_string (String signature_image_string) {
        this.signature_image_string = signature_image_string;
        Utils.showLog (Log.DEBUG, "signature_image_string", signature_image_string, false);
    }
}
