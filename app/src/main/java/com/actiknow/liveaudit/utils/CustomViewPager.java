package com.actiknow.liveaudit.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {

    Context context;
    //    private boolean enabled;
    private int childId;


    public CustomViewPager (Context context, AttributeSet attrs) {
        super (context, attrs);
        this.context = context;
//        this.enabled = true;
    }

    public void setChildId (int childId) {
        this.childId = childId;
    }

    /*

    @Override
    public boolean onInterceptTouchEvent (MotionEvent event) {
        boolean result = false;
        View scroll = getChildAt (childId);
        if (scroll != null) {
            Rect rect = new Rect ();
//            CommonLogic.logMessage ("PDF Page Rectangle  ", TAG, Log.VERBOSE);
            scroll.getHitRect (rect);
            if (rect.contains ((int) event.getX (), (int) event.getY ())) {
                setPagingEnabled (false);
                result = true;
            }
        }
        return result;
    }

*/

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        //     if (!BaseFragment.flag) {
        //       return false;
        //    } else {
            return super.onTouchEvent (event);
        //     }
    }

    @Override
    public boolean onInterceptTouchEvent (MotionEvent event) {
        //    if (!BaseFragment.flag) {
        //        return false;
        //    } else {
        return super.onInterceptTouchEvent (event);
        //    }
    }

//    public void setPagingEnabled (boolean enabled) {
//        this.enabled = enabled;
//    }
}