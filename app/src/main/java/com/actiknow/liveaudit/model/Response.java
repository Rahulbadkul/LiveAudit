package com.actiknow.liveaudit.model;

import android.util.Log;

public class Response {
    private int response_id, response_auditor_id, response_switch_flag, response_question_id;
    private String response_atm_unique_id, response_question, response_comment, response_image1, response_image2;

    public Response () {
    }

    public Response (int response_id, int response_auditor_id, String response_atm_unique_id, int response_question_id, int response_switch_flag, String response_comment, String response_image1, String response_image2) {
        this.response_id = response_id;
        this.response_auditor_id = response_auditor_id;
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
        Log.d ("response_id", "" + response_id);
    }

    public int getResponse_auditor_id () {
        return response_auditor_id;
    }

    public void setResponse_auditor_id (int response_auditor_id) {
        this.response_auditor_id = response_auditor_id;
        Log.d ("response_auditor_id", "" + response_auditor_id);
    }

    public String getResponse_atm_unique_id () {
        return response_atm_unique_id;
    }

    public void setResponse_atm_unique_id (String response_atm_unique_id) {
        this.response_atm_unique_id = response_atm_unique_id;
        Log.d ("response_atm_unique_id", response_atm_unique_id);
    }

    public int getResponse_question_id () {
        return response_question_id;
    }

    public void setResponse_question_id (int response_question_id) {
        this.response_question_id = response_question_id;
        Log.d ("response_question_id", "" + response_question_id);
    }

    public int getResponse_switch_flag () {
        return response_switch_flag;
    }

    public void setResponse_switch_flag (int response_switch_flag) {
        this.response_switch_flag = response_switch_flag;
        Log.d ("response_switch_flag", "" + response_switch_flag);
    }

    public String getResponse_question () {
        return response_question;
    }

    public void setResponse_question (String response_question) {
        this.response_question = response_question;
        Log.d ("response_question", response_question);
    }

    public String getResponse_comment () {
        return response_comment;
    }

    public void setResponse_comment (String response_comment) {
        this.response_comment = response_comment;
        Log.d ("response_comment", response_comment);
    }

    public String getResponse_image1 () {
        return response_image1;
    }

    public void setResponse_image1 (String response_image1) {
        this.response_image1 = response_image1;
        Log.d ("response_image1", response_image1);
    }

    public String getResponse_image2 () {
        return response_image2;
    }

    public void setResponse_image2 (String response_image2) {
        this.response_image2 = response_image2;
        Log.d ("response_image2", response_image2);
    }
}
