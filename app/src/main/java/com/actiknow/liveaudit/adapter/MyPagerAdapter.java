package com.actiknow.liveaudit.adapter;

import android.support.v4.app.FragmentManager;

import com.actiknow.liveaudit.fragment.BaseFragment;
import com.actiknow.liveaudit.model.Questions;
import com.actiknow.liveaudit.utils.Constants;


// Extend from SmartFragmentStatePagerAdapter now instead for more dynamic ViewPager items
public class MyPagerAdapter extends SmartFragmentStatePagerAdapter {
    private static int NUM_ITEMS = Constants.questionsList.size () + 1;

    public MyPagerAdapter (FragmentManager fragmentManager) {
        super (fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount () {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public android.support.v4.app.Fragment getItem (int position) {

        if(position != NUM_ITEMS-1){
            final Questions question = Constants.questionsList.get (position);
            return BaseFragment.newInstance (position, question.getQuestion (), question.getQuestion_id ());
        }
        else
            return BaseFragment.newInstance (position);
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle (int position) {
        return "Page " + position;
    }
}