package com.actiknow.liveaudit.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Admin on 23-12-2015.
 */
public class Utils {
    public static int isValidEmail (String email) {
        if (email.length () != 0) {
            boolean validMail = isValidEmail2 (email);
            if (validMail)
                return 1;
            else
                return 2;
        } else
            return 0;
    }

    public static boolean isValidEmail2 (CharSequence target) {
        return ! TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher (target).matches ();
    }

    public static int isValidPassword (String password) {
        if (password.length () > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public static Bitmap base64ToBitmap (String b64) {
        byte[] imageAsBytes = Base64.decode (b64.getBytes (), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray (imageAsBytes, 0, imageAsBytes.length);
    }

    public static String bitmapToBase64 (Bitmap bmp) {
        if (bmp != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream ();
            bmp.compress (Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray ();
            String encodedImage = Base64.encodeToString (imageBytes, Base64.DEFAULT);
            return encodedImage;
        } else {
            return "";
        }
    }

    public static String convertTimeFormat (String OrigFormat) {
        if (OrigFormat != "null") {
            SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
            Date testDate = null;
            try {
                testDate = sdf.parse (OrigFormat);
            } catch (Exception ex) {
                ex.printStackTrace ();
            }
            SimpleDateFormat formatter = new SimpleDateFormat ("dd/MM/yyyy");
            String newFormat = formatter.format (testDate);
            return newFormat;
        } else {
            return "Unavailable";
        }
    }
}
