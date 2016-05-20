package com.actiknow.liveaudit.model;

import android.util.Log;

import com.actiknow.liveaudit.utils.Utils;

public class Atm {
    private int atm_id, atm_agency_id;
    private String atm_unique_id, atm_last_audit_date, atm_bank_name, atm_address, atm_city, atm_pincode;

    public Atm () {
    }

    public Atm (int atm_id, int atm_agency_id, String atm_unique_id, String atm_last_audit_date, String atm_bank_name, String atm_address, String atm_city, String atm_pincode) {
        this.atm_id = atm_id;
        this.atm_agency_id = atm_agency_id;
        this.atm_unique_id = atm_unique_id;
        this.atm_last_audit_date = atm_last_audit_date;
        this.atm_bank_name = atm_bank_name;
        this.atm_address = atm_address;
        this.atm_city = atm_city;
        this.atm_pincode = atm_pincode;
    }

    public int getAtm_id () {
        return atm_id;
    }

    public void setAtm_id (int atm_id) {
        this.atm_id = atm_id;
        Utils.showLog (Log.DEBUG, "atm_id", "" + atm_id, false);
    }

    public int getAtm_agency_id () {
        return atm_agency_id;
    }

    public void setAtm_agency_id (int atm_agency_id) {
        this.atm_agency_id = atm_agency_id;
        Utils.showLog (Log.DEBUG, "atm_agency_id", "" + atm_agency_id, false);
    }

    public String getAtm_unique_id () {
        return atm_unique_id;
    }

    public void setAtm_unique_id (String atm_unique_id) {
        this.atm_unique_id = atm_unique_id;
        Utils.showLog (Log.DEBUG, "atm_unique_id", atm_unique_id, false);
    }

    public String getAtm_last_audit_date () {
        return atm_last_audit_date;
    }

    public void setAtm_last_audit_date (String atm_last_audit_date) {
        this.atm_last_audit_date = atm_last_audit_date;
        Utils.showLog (Log.DEBUG, "atm_last_audit_date", atm_last_audit_date, false);
    }

    public String getAtm_bank_name () {
        return atm_bank_name;
    }

    public void setAtm_bank_name (String atm_bank_name) {
        this.atm_bank_name = atm_bank_name;
        Utils.showLog (Log.DEBUG, "atm_bank_name", atm_bank_name, false);
    }

    public String getAtm_address () {
        return atm_address;
    }

    public void setAtm_address (String atm_address) {
        this.atm_address = atm_address;
        Utils.showLog (Log.DEBUG, "atm_address", atm_address, false);
    }

    public String getAtm_city () {
        return atm_city;
    }

    public void setAtm_city (String atm_city) {
        this.atm_city = atm_city;
        Utils.showLog (Log.DEBUG, "atm_city", atm_city, false);
    }

    public String getAtm_pincode () {
        return atm_pincode;
    }

    public void setAtm_pincode (String atm_pincode) {
        this.atm_pincode = atm_pincode;
        Utils.showLog (Log.DEBUG, "atm_pincode", atm_pincode, false);
    }
}
