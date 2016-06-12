package com.actiknow.liveaudit.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.actiknow.liveaudit.R;
import com.actiknow.liveaudit.model.Question;
import com.actiknow.liveaudit.utils.Utils;

import java.util.List;

public class AllQuestionsAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Question> questionList;

    public AllQuestionsAdapter (Activity activity, List<Question> questionList) {
        this.activity = activity;
        this.questionList = questionList;
    }

    @Override
    public int getCount () {
        return questionList.size ();
    }

    @Override
    public Object getItem (int location) {
        return questionList.get (location);
    }

    @Override
    public long getItemId (int position) {
        return position;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        final QuestionViewHolder holder;
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate (R.layout.listview_item_question, null);
            holder = new QuestionViewHolder ();
            holder.tvQuestionText = (TextView) convertView.findViewById (R.id.tvQuestionInList);
            holder.switchYesNo = (Switch) convertView.findViewById (R.id.switchYesNoInList);
            convertView.setTag (holder);
        } else {
            holder = (QuestionViewHolder) convertView.getTag ();
        }

        Utils.setTypefaceToAllViews (activity, holder.tvQuestionText);


        final Question question = questionList.get (position);
        holder.tvQuestionText.setText (question.getQuestion ());

        convertView.setOnClickListener (new View.OnClickListener () {
            private void die () {
            }

            @Override
            public void onClick (View arg0) {
            }
        });
        return convertView;
    }

    static class QuestionViewHolder {
        TextView tvQuestionText;
        Switch switchYesNo;
    }
}