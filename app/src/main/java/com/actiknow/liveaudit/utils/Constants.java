package com.actiknow.liveaudit.utils;


import com.actiknow.liveaudit.model.GeoImage;
import com.actiknow.liveaudit.model.Question;
import com.actiknow.liveaudit.model.Rating;
import com.actiknow.liveaudit.model.Response;

import java.util.ArrayList;
import java.util.List;



public class Constants {
    public static String auditor_name = "";
    public static String username = "";
    public static int auditor_id_main = 0;
    public static double latitude = 0.0;
    public static double longitude = 0.0;

    public static int splash_screen_first_time = 0; // 0 => default

    public static List<Question> questionsList = new ArrayList<Question> ();
    public static String atm_unique_id = "";
    public static int atm_agency_id = 0;

    public static int total_questions = 0;
    public static int count = 0;

    public static List<Response> responseList = new ArrayList<Response> ();
    public static Rating rating = new Rating ();
    public static GeoImage geoImage = new GeoImage ();

    public static String atm_location_in_manual = "";

    public static boolean show_log = true;


    public static String location_tagging_start_time = "08:00";
    public static String location_tagging_end_time = "20:00";


}
