package com.actiknow.liveaudit.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actiknow.liveaudit.R;
import com.actiknow.liveaudit.activity.MainActivity;
import com.actiknow.liveaudit.app.AppController;
import com.actiknow.liveaudit.model.Response;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        return ! TextUtils.isEmpty (target) && android.util.Patterns.EMAIL_ADDRESS.matcher (target).matches ();
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

    public static void showOkDialog (final Activity activity, String message, final boolean finish_flag) {
        TextView tvMessage;
        MaterialDialog dialog = new MaterialDialog.Builder (activity)
                .customView (R.layout.dialog_basic, true)
                .positiveText (R.string.dialog_basic_positive)
                .onPositive (new MaterialDialog.SingleButtonCallback () {
                    @Override
                    public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss ();
                        if (finish_flag) {
                            Intent intent = new Intent (activity, MainActivity.class);
                            intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            activity.startActivity (intent);
                            activity.overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                    }
                }).build ();

        tvMessage = (TextView) dialog.getCustomView ().findViewById (R.id.tvMessage);
        tvMessage.setText (message);
        Utils.setTypefaceToAllViews (activity, tvMessage);
        dialog.show ();

/*
        AlertDialog.Builder builder = new AlertDialog.Builder (activity);
        builder.setMessage (message)
                .setCancelable (false)
                .setPositiveButton ("OK", new DialogInterface.OnClickListener () {
                    public void onClick (DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create ();
        alert.show ();

*/
    }

    public static void showSnackBar (CoordinatorLayout coordinatorLayout, String message) {
        final Snackbar snackbar = Snackbar
                .make (coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction ("DISMISS", new View.OnClickListener () {
                    @Override
                    public void onClick (View view) {
                    }
                });
        snackbar.show ();
    }

    public static void showToast (Activity activity, String message) {
        Toast.makeText (activity, message, Toast.LENGTH_SHORT).show ();
    }

    public static void setTypefaceToAllViews (Activity activity, View view) {
        Typeface tf = SetTypeFace.getTypeface (activity);
        SetTypeFace.applyTypeface (SetTypeFace.getParentView (view), tf);
    }

    public static void showProgressDialog (ProgressDialog progressDialog, String message) {
        // Initialize the progressDialog before calling this function
        TextView tvMessage;
        progressDialog.show ();
        progressDialog.getWindow ().setBackgroundDrawable (new ColorDrawable (android.graphics.Color.TRANSPARENT));
        progressDialog.setContentView (R.layout.progress_dialog);
        tvMessage = (TextView) progressDialog.findViewById (R.id.tvProgressDialogMessage);
        if (message != null) {
            tvMessage.setText (message);
            tvMessage.setVisibility (View.VISIBLE);
        }
        else
            tvMessage.setVisibility (View.GONE);
        progressDialog.setCancelable (false);
    }

    public static void showLog (int log_type, String tag, String message, boolean show_flag) {
        if (Constants.show_log) {
            if (show_flag) {
                switch (log_type) {
                    case Log.DEBUG:
                        Log.d (tag, message);
                        break;
                    case Log.ERROR:
                        Log.e (tag, message);
                        break;
                    case Log.INFO:
                        Log.i (tag, message);
                        break;
                    case Log.VERBOSE:
                        Log.v (tag, message);
                        break;
                    case Log.WARN:
                        Log.w (tag, message);
                        break;
                    case Log.ASSERT:
                        Log.wtf (tag, message);
                        break;
                }
            }
        }
    }

    public static void showErrorInEditText (EditText editText, String message) {
        editText.setError (message);
    }

    public static void hideSoftKeyboard (Activity activity) {
        View view = activity.getCurrentFocus ();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService (Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow (view.getWindowToken (), 0);
        }
    }

    public static boolean isPackageExists (Activity activity, String targetPackage) {
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = activity.getPackageManager ();
        packages = pm.getInstalledApplications (0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals (targetPackage))
                return true;
        }
        return false;
    }

    public static void setSeekBar (SeekBar sbRating, TextView tvRatingNumber) {
        int count = 0;
        for (int i = 0; i < Constants.questionsList.size (); i++) {
            Response response;
            response = Constants.responseList.get (i);
            count = count + response.getSwitch_flag ();
        }
        int rating = ((count) * 100) / Constants.questionsList.size ();
        Utils.showLog (Log.DEBUG, AppConfigTags.RATING, "" + rating, true);
//        tvRatingNumber.setText (String.valueOf (rating / 10));
        tvRatingNumber.setText (String.valueOf (rating) + "%");
        sbRating.setProgress (rating);
    }

    public static void sendRequest (StringRequest strRequest, int timeout_seconds) {
        strRequest.setShouldCache (false);
        int timeout = timeout_seconds * 1000;
        AppController.getInstance ().addToRequestQueue (strRequest);
        strRequest.setRetryPolicy (new DefaultRetryPolicy (timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public static Bitmap compressBitmap (Bitmap bitmap, Activity activity) {
        Bitmap decoded = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream ();
            if (NetworkConnection.isNetworkAvailable (activity)) {
                bitmap.compress (Bitmap.CompressFormat.JPEG, Constants.image_quality, out);
            } else {
                bitmap.compress (Bitmap.CompressFormat.JPEG, Constants.image_quality, out);
            }
            decoded = Utils.scaleDown (BitmapFactory.decodeStream (new ByteArrayInputStream (out.toByteArray ())), Constants.max_image_size, true);
        } catch (Exception e) {
            e.printStackTrace ();
            Utils.showLog (Log.ERROR, "EXCEPTION", e.getMessage (), true);
        }
        return decoded;
    }

    public static Bitmap scaleDown (Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min ((float) maxImageSize / realImage.getWidth (), (float) maxImageSize / realImage.getHeight ());
        int width = Math.round ((float) ratio * realImage.getWidth ());
        int height = Math.round ((float) ratio * realImage.getHeight ());
        Bitmap newBitmap = Bitmap.createScaledBitmap (realImage, width, height, filter);
        return newBitmap;
    }

    public static int getScreenHeight (Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics ();
        activity.getWindowManager ().getDefaultDisplay ().getMetrics (displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        return height;
    }
}
