package com.actiknow.liveaudit.model;

import android.util.Log;

import com.actiknow.liveaudit.utils.Utils;

public class Question {
    private int question_id;
    private String question;

    public Question () {
    }

    public Question (int question_id, String question) {
        this.question_id = question_id;
        this.question = question;
    }

    public int getQuestion_id () {
        return question_id;
    }

    public void setQuestion_id (int question_id) {
        this.question_id = question_id;
        Utils.showLog (Log.DEBUG, "question_id", "" + question_id);
    }

    public String getQuestion () {
        return question;
    }

    public void setQuestion (String question) {
        this.question = question;
        Utils.showLog (Log.DEBUG, "question", question);
    }
}
