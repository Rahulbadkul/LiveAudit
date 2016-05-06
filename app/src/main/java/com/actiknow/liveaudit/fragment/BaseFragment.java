package com.actiknow.liveaudit.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.actiknow.liveaudit.R;
import com.actiknow.liveaudit.utils.Constants;

import java.util.List;


public class BaseFragment extends android.support.v4.app.Fragment {
    private static final String IMAGE_RESOURCE = "image-resource";
    public static boolean flag = false;
    static boolean isLast = false;
    final int CAMERA_ACTIVITY_1 = 1;
    final int CAMERA_ACTIVITY_2 = 2;
    RelativeLayout rlRequirements;
    RelativeLayout rlRating;
    Bitmap bp1;
    Bitmap bp2;
    Bitmap bp1temp;
    Bitmap bp2temp;
    // Store instance variables
    private String question;
    private int question_id;
    private int page;
    private Switch switchYesNo;
    private EditText etComments;
    private ImageView ivImage1;
    private ImageView ivImage2;
    private int image;


    public static BaseFragment newInstance (int page) {
        BaseFragment fragmentFirst = new BaseFragment ();
        Bundle args = new Bundle ();
        args.putInt ("page_number", page);
        isLast = true;
        fragmentFirst.setArguments (args);
        return fragmentFirst;
    }

    public static BaseFragment newInstance (int page, String question, int question_id) {
        BaseFragment fragmentFirst = new BaseFragment ();
        Bundle args = new Bundle ();
        isLast = false;
        args.putString ("question", question);
        args.putInt ("question_id", question_id);
        fragmentFirst.setArguments (args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        page = getArguments ().getInt ("page_number", 0);
        if (! isLast) {
            question = getArguments ().getString ("question");
            question_id = getArguments ().getInt ("question_id");
        }
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        if (! isLast) {

            view = inflater.inflate (R.layout.fragment_first, container, false);
            TextView tvQuestion = (TextView) view.findViewById (R.id.tvQuestion);
            switchYesNo = (Switch) view.findViewById (R.id.switchYesNo);
            etComments = (EditText) view.findViewById (R.id.etComments);

            tvQuestion.setText (question);


            ivImage1 = (ImageView) view.findViewById (R.id.ivImage1);
            ivImage2 = (ImageView) view.findViewById (R.id.ivImage2);


            if (savedInstanceState == null) {

            } else {
                // if there is a bundle, use the saved image resource (if one is there)

                bp1temp = savedInstanceState.getParcelable ("BitmapImage1");
                bp2temp = savedInstanceState.getParcelable ("BitmapImage2");
                ivImage1.setImageBitmap (bp1temp);
                ivImage2.setImageBitmap (bp2temp);
            }


            ivImage1.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    Intent mIntent = null;
                    if (isPackageExists ("com.google.android.camera")) {
                        mIntent = new Intent ();
                        mIntent.setPackage ("com.google.android.camera");
                        mIntent.setAction (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    } else {
                        PackageManager packageManager = getActivity ().getPackageManager ();
                        String defaultCameraPackage = null;
                        List<ApplicationInfo> list = packageManager.getInstalledApplications (PackageManager.GET_UNINSTALLED_PACKAGES);
                        for (int n = 0; n < list.size (); n++) {
                            if ((list.get (n).flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                                Log.d ("TAG", "Installed Applications  : " + list.get (n).loadLabel (packageManager).toString ());
                                Log.d ("TAG", "package name  : " + list.get (n).packageName);
                                if (list.get (n).loadLabel (packageManager).toString ().equalsIgnoreCase ("Camera")) {
                                    defaultCameraPackage = list.get (n).packageName;
                                    break;
                                }
                            }
                        }

                        mIntent = new Intent ();
                        mIntent.setPackage (defaultCameraPackage);
                        mIntent.setAction (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

//                    mIntent = new Intent (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }

                    if (mIntent.resolveActivity (getActivity ().getPackageManager ()) != null) {
                        startActivityForResult (mIntent, CAMERA_ACTIVITY_1);
                    }

                }
            });

            ivImage2.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    Intent mIntent = null;
                    if (isPackageExists ("com.google.android.camera")) {
                        mIntent = new Intent ();
                        mIntent.setPackage ("com.google.android.camera");
                        mIntent.setAction (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    } else {
                        PackageManager packageManager = getActivity ().getPackageManager ();
                        String defaultCameraPackage = null;
                        List<ApplicationInfo> list = packageManager.getInstalledApplications (PackageManager.GET_UNINSTALLED_PACKAGES);
                        for (int n = 0; n < list.size (); n++) {
                            if ((list.get (n).flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                                Log.d ("TAG", "Installed Applications  : " + list.get (n).loadLabel (packageManager).toString ());
                                Log.d ("TAG", "package name  : " + list.get (n).packageName);
                                if (list.get (n).loadLabel (packageManager).toString ().equalsIgnoreCase ("Camera")) {
                                    defaultCameraPackage = list.get (n).packageName;
                                    break;
                                }
                            }
                        }

                        mIntent = new Intent ();
                        mIntent.setPackage (defaultCameraPackage);
                        mIntent.setAction (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

//                    mIntent = new Intent (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }

                    if (mIntent.resolveActivity (getActivity ().getPackageManager ()) != null) {
                        startActivityForResult (mIntent, CAMERA_ACTIVITY_2);
                    }

                }
            });


            switchYesNo.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener () {
                @Override
                public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
                    Log.d ("question ", "" + question);
                    Log.d ("question_id ", "" + question_id);

                    if(switchYesNo.isChecked ())
                        Constants.count++;
                    else
                        Constants.count--;


                    if (! switchYesNo.isChecked () && etComments.getText ().length () == 0) {
                        flag = false;
                    } else {
                        flag = true;
                    }
                }
            });

//        Log.d ("question ", "" + question);
//        Log.d ("question_id ", "" + question_id);

        } else {
            view = inflater.inflate (R.layout.fragment_last, container, false);
            final TextView tvRatingNumber = (TextView) view.findViewById (R.id.tvRatingNumber);
            SeekBar sbRating = (SeekBar) view.findViewById (R.id.sbRating);


//            tvRatingNumber.setText ((Constants.count/Constants.total_questions)*100);

//            sbRating.setProgress ((Constants.count / Constants.total_questions) * 100);

            Button btSubmit = (Button) view.findViewById (R.id.btSubmit);
            
            sbRating.setOnSeekBarChangeListener (new SeekBar.OnSeekBarChangeListener () {

                public void onStopTrackingTouch (SeekBar bar) {
                    int value = bar.getProgress (); // the value of the seekBar progress
                }

                public void onStartTrackingTouch (SeekBar bar) {
                }

                public void onProgressChanged (SeekBar bar, int paramInt, boolean paramBoolean) {
                    tvRatingNumber.setText ("" + paramInt / 10);
                }
            });

            btSubmit.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    Toast.makeText (getActivity (), "Your response have been submitted successfully", Toast.LENGTH_SHORT).show ();
            //        getActivity ().finish ();
                    Log.e ("value : ", "" + Constants.count / Constants.total_questions * 100);


                }
            });

        }
        return view;
    }

    public boolean isPackageExists (String targetPackage) {
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getActivity ().getPackageManager ();
        packages = pm.getInstalledApplications (0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals (targetPackage)) return true;
        }
        return false;
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult (requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case CAMERA_ACTIVITY_1:
                    bp1 = (Bitmap) data.getExtras ().get ("data");
                    ivImage1.setImageBitmap (bp1);
                    break;
                case CAMERA_ACTIVITY_2:
                    bp2 = (Bitmap) data.getExtras ().get ("data");
                    ivImage2.setImageBitmap (bp2);
                    break;

            }

        } catch (Exception e) {
        }
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        // Make sure you save the current image resource
        outState.putInt (IMAGE_RESOURCE, image);
        outState.putParcelable ("BitmapImage1", bp1);
        outState.putParcelable ("BitmapImage2", bp2);
        super.onSaveInstanceState (outState);
    }
}