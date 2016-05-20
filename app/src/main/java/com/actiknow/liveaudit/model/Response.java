package com.actiknow.liveaudit.model;

import android.util.Log;

import com.actiknow.liveaudit.utils.Utils;

public class Response {
    private int response_id, response_auditor_id, response_switch_flag, response_question_id, response_agency_id;
    private String response_atm_unique_id, response_question, response_comment, response_image1, response_image2;

    public Response () {
    }

    public Response (int response_id, int response_auditor_id, int response_agency_id, String response_atm_unique_id, int response_question_id, int response_switch_flag, String response_comment, String response_image1, String response_image2) {
        this.response_id = response_id;
        this.response_auditor_id = response_auditor_id;
        this.response_agency_id = response_agency_id;
        this.response_atm_unique_id = response_atm_unique_id;
        this.response_question_id = response_question_id;
        this.response_switch_flag = response_switch_flag;
        this.response_comment = response_comment;
        this.response_image1 = response_image1;
        this.response_image2 = response_image2;
    }

    public int getResponse_id () {
        return response_id;
    }

    public void setResponse_id (int response_id) {
        this.response_id = response_id;
        Utils.showLog (Log.DEBUG, "response_id", "" + response_id, false);
    }

    public int getResponse_auditor_id () {
        return response_auditor_id;
    }

    public void setResponse_auditor_id (int response_auditor_id) {
        this.response_auditor_id = response_auditor_id;
        Utils.showLog (Log.DEBUG, "response_auditor_id", "" + response_auditor_id, false);
    }

    public int getResponse_agency_id () {
        return response_agency_id;
    }

    public void setResponse_agency_id (int response_agency_id) {
        this.response_agency_id = response_agency_id;
        Utils.showLog (Log.DEBUG, "response_agency_id", "" + response_agency_id, false);
    }

    public String getResponse_atm_unique_id () {
        return response_atm_unique_id;
    }

    public void setResponse_atm_unique_id (String response_atm_unique_id) {
        this.response_atm_unique_id = response_atm_unique_id;
        Utils.showLog (Log.DEBUG, "response_atm_unique_id", response_atm_unique_id, false);
    }

    public int getResponse_question_id () {
        return response_question_id;
    }

    public void setResponse_question_id (int response_question_id) {
        this.response_question_id = response_question_id;
        Utils.showLog (Log.DEBUG, "response_question_id", "" + response_question_id, false);
    }

    public int getResponse_switch_flag () {
        return response_switch_flag;
    }

    public void setResponse_switch_flag (int response_switch_flag) {
        this.response_switch_flag = response_switch_flag;
        Utils.showLog (Log.DEBUG, "response_switch_flag", "" + response_switch_flag, false);
    }

    public String getResponse_question () {
        return response_question;
    }

    public void setResponse_question (String response_question) {
        this.response_question = response_question;
        Utils.showLog (Log.DEBUG, "response_question", response_question, false);
    }

    public String getResponse_comment () {
        return response_comment;
    }

    public void setResponse_comment (String response_comment) {
        this.response_comment = response_comment;
        Utils.showLog (Log.DEBUG, "response_comment", response_comment, false);
    }

    public String getResponse_image1 () {
        return response_image1;
    }

    public void setResponse_image1 (String response_image1) {
        this.response_image1 = response_image1;
        Utils.showLog (Log.DEBUG, "response_image1", response_image1, false);
    }

    public String getResponse_image2 () {
        return response_image2;
    }

    public void setResponse_image2 (String response_image2) {
        this.response_image2 = response_image2;
        Utils.showLog (Log.DEBUG, "response_image2", response_image2, false);
    }
}
