package com.actiknow.liveaudit.utils;

import com.actiknow.liveaudit.model.Questions;
import com.actiknow.liveaudit.model.Response;

import java.util.ArrayList;
import java.util.List;



public class Constants {
    public static String auditor_name = "";
    public static String username = "";
    public static String password = "";
    public static int auditor_id_main = 4;

    public static int user_id_main = 4;    //  0 => default
    public static int atm_id = 0;
    public static String atm_name = "";
    public static String atm_bank = "";
    public static String atm_location  = "";

    public static double resp_lat = 0;
    public static double resp_lng = 0;

    public static int atm_checklist_item = 0;   //  0 => default, 1 => cctv, 2 => machine, 3 => ac, 4 => guard

    public static int splash_screen_first_time = 0; // 0 => default

    public static List<Questions> questionsList = new ArrayList<Questions> ();
    public static String atm_unique_id = "";

    public static int total_questions = 0;
    public static int count = 0;
    public static double final_rating = 0;

    public static List<Response> responseList = new ArrayList<Response> ();
}
