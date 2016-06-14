package com.actiknow.liveaudit.model;

import android.util.Log;

import com.actiknow.liveaudit.utils.Utils;

public class GeoImage {
    private int response_geo_image_id, auditor_id, agency_id;
    private String atm_unique_id, geo_image_string, latitude, longitude;

    public GeoImage () {
    }

    public GeoImage (int response_geo_image_id, int auditor_id, int agency_id, String atm_unique_id, String geo_image_string, String latitude, String longitude) {
        this.response_geo_image_id = response_geo_image_id;
        this.auditor_id = auditor_id;
        this.agency_id = agency_id;
        this.atm_unique_id = atm_unique_id;
        this.geo_image_string = geo_image_string;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getResponse_geo_image_id () {
        return response_geo_image_id;
    }

    public void setResponse_geo_image_id (int response_geo_image_id) {
        this.response_geo_image_id = response_geo_image_id;
        Utils.showLog (Log.DEBUG, "response_geo_image_id", "" + response_geo_image_id, false);
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
}
