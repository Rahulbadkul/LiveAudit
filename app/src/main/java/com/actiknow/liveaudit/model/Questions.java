package com.actiknow.liveaudit.model;

import android.util.Log;

public class Questions {
    private int question_id;
    private String question;

    public Questions () {
    }

    public Questions (int question_id, String question) {
        this.question_id = question_id;
        this.question = question;
    }

    public int getQuestion_id () {
        return question_id;
    }

    public void setQuestion_id (int question_id) {
        this.question_id = question_id;
        Log.d ("question_id", "" + question_id);
    }

    public String getQuestion () {
        return question;
    }

    public void setQuestion (String question) {
        this.question = question;
        Log.d ("question", question);
    }
}
