package com.actiknow.liveaudit.model;

import android.util.Log;

public class Atms {
    private int atm_id;
    private String atm_unique_id, atm_last_audit_date, atm_bank_name, atm_address, atm_city, atm_pincode;

    public Atms () {
    }

    public Atms (int atm_id, String atm_unique_id, String atm_last_audit_date, String atm_bank_name, String atm_address, String atm_city, String atm_pincode) {
        this.atm_id = atm_id;
        this.atm_unique_id = atm_unique_id;
        this.atm_last_audit_date = atm_last_audit_date;
        this.atm_bank_name = atm_bank_name;
        this.atm_address = atm_address;
        this.atm_city = atm_city;
        this.atm_pincode = atm_pincode;
    }

    public int getAtm_id() {
        return atm_id;
    }

    public void setAtm_id (int atm_id) {
        this.atm_id = atm_id;
        Log.d ("atm_id", "" + atm_id);
    }

    public String getAtm_unique_id () {
        return atm_unique_id;
    }

    public void setAtm_unique_id (String atm_unique_id) {
        this.atm_unique_id = atm_unique_id;
        Log.d ("atm_unique_id", atm_unique_id);
    }

    public String getAtm_last_audit_date () {
        return atm_last_audit_date;
    }

    public void setAtm_last_audit_date (String atm_last_audit_date) {
        this.atm_last_audit_date = atm_last_audit_date;
        Log.d ("atm_last_audit_date", atm_last_audit_date);
    }

    public String getAtm_bank_name () {
        return atm_bank_name;
    }

    public void setAtm_bank_name (String atm_bank_name) {
        this.atm_bank_name = atm_bank_name;
        Log.d ("atm_bank_name", atm_bank_name);
    }

    public String getAtm_address () {
        return atm_address;
    }

    public void setAtm_address (String atm_address) {
        this.atm_address = atm_address;
        Log.d ("atm_address", atm_address);
    }

    public String getAtm_city () {
        return atm_city;
    }

    public void setAtm_city (String atm_city) {
        this.atm_city = atm_city;
        Log.d ("atm_city", atm_city);
    }

    public String getAtm_pincode () {
        return atm_pincode;
    }

    public void setAtm_pincode (String atm_pincode) {
        this.atm_pincode = atm_pincode;
        Log.d ("atm_pincode", atm_pincode);
    }
}
