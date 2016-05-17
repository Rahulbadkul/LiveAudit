package com.actiknow.liveaudit.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class LoginDetailsPref {
    public static String AUDITOR_NAME = "auditor_name";
    public static String USERNAME = "username";
    public static String AUDITOR_ID = "auditor_id";
    //    private String merchantDetails[] = {MOBILE, PASSWORD, MERCHANT_ID};
    private static LoginDetailsPref loginDetailsPref;
    // public static String ROLL = "roll";
    private String LOGIN_DETAILS = "LOGIN_DETAILS";

    public static LoginDetailsPref getInstance () {
        if (loginDetailsPref == null)
            loginDetailsPref = new LoginDetailsPref ();
        return loginDetailsPref;
    }

    private SharedPreferences getPref (Context context) {
        return context.getSharedPreferences (LOGIN_DETAILS, Context.MODE_PRIVATE);
    }

    public String getStringPref (Context context, String key) {
        return getPref (context).getString (key, "");
    }

    public int getIntPref (Context context, String key) {
        return getPref (context).getInt (key, 0);
    }

    public void putStringPref (Context context, String key, String value) {
        SharedPreferences.Editor editor = getPref (context).edit ();
        editor.putString (key, value);
        editor.apply ();
    }

    public void putIntPref (Context context, String key, int value) {
        SharedPreferences.Editor editor = getPref (context).edit ();
        editor.putInt (key, value);
        editor.apply ();
    }

    /*public void putMerchantDetailsPref(Context context,String value[])
    {
        SharedPreferences.Editor editor = getPref(context).edit();
        for (int i=0; i<merchantDetails.length;i++)
            editor.putString(merchantDetails[i], value[i]);
        editor.apply();
    }*/
}
