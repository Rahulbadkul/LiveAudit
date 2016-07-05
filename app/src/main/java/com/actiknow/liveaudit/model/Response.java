package com.actiknow.liveaudit.model;

import android.util.Log;

import com.actiknow.liveaudit.utils.Utils;

public class Response {
    private int response_id, switch_flag, question_id;
    private String question, comment, image1, image2;

    public Response () {
    }

    public Response (int response_id, int question_id, String question, int switch_flag, String comment, String image1, String image2) {
        this.response_id = response_id;
        this.question_id = question_id;
        this.question = question;
        this.switch_flag = switch_flag;
        this.comment = comment;
        this.image1 = image1;
        this.image2 = image2;
    }

    public int getResponse_id () {
        return response_id;
    }

    public void setResponse_id (int response_id) {
        this.response_id = response_id;
        Utils.showLog (Log.DEBUG, "response_id", "" + response_id, false);
    }

    public int getQuestion_id () {
        return question_id;
    }

    public void setQuestion_id (int question_id) {
        this.question_id = question_id;
        Utils.showLog (Log.DEBUG, "question_id", "" + question_id, false);
    }

    public int getSwitch_flag () {
        return switch_flag;
    }

    public void setSwitch_flag (int switch_flag) {
        this.switch_flag = switch_flag;
        Utils.showLog (Log.DEBUG, "switch_flag", "" + switch_flag, false);
    }

    public String getQuestion () {
        return question;
    }

    public void setQuestion (String question) {
        this.question = question;
        Utils.showLog (Log.DEBUG, "question", question, false);
    }

    public String getComment () {
        return comment;
    }

    public void setComment (String comment) {
        this.comment = comment;
        Utils.showLog (Log.DEBUG, "comment", comment, false);
    }

    public String getImage1 () {
        return image1;
    }

    public void setImage1 (String image1) {
        this.image1 = image1;
        Utils.showLog (Log.DEBUG, "image1", image1, false);
    }

    public String getImage2 () {
        return image2;
    }

    public void setImage2 (String image2) {
        this.image2 = image2;
        Utils.showLog (Log.DEBUG, "image2", image2, false);
    }
}
