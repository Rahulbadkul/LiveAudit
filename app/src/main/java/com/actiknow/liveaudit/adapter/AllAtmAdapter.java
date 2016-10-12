package com.actiknow.liveaudit.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.actiknow.liveaudit.R;
import com.actiknow.liveaudit.activity.MainActivity;
import com.actiknow.liveaudit.model.Atm;
import com.actiknow.liveaudit.utils.AppConfigTags;
import com.actiknow.liveaudit.utils.Constants;
import com.actiknow.liveaudit.utils.Utils;

import java.io.File;
import java.util.List;

public class AllAtmAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private List<Atm> atmList;

	private Typeface typeface;


	public AllAtmAdapter (Activity activity, List<Atm> atmList) {
		this.activity = activity;
		this.atmList = atmList;
		typeface = Typeface.createFromAsset (activity.getAssets (), "Kozuka-Gothic.ttf");
	}

	@Override
	public int getCount() {
		return atmList.size ();
	}

	@Override
	public Object getItem(int location) {
		return atmList.get (location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (inflater == null)
			inflater = (LayoutInflater) activity.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = inflater.inflate (R.layout.listview_item_atm, null);
			holder = new ViewHolder ();
			holder.atm_last_audit_date = (TextView) convertView.findViewById (R.id.tvLastAuditDate);
			holder.atm_atm_unique_id = (TextView) convertView.findViewById (R.id.tvAtmUniqueId);
			holder.atm_bank_name = (TextView) convertView.findViewById (R.id.tvBankName);
			holder.atm_address = (TextView) convertView.findViewById (R.id.tvAddress);
			holder.atm_city = (TextView) convertView.findViewById (R.id.tvCity);
			holder.atm_pincode = (TextView) convertView.findViewById (R.id.tvPincode);
			convertView.setTag (holder);
		} else
			holder = (ViewHolder) convertView.getTag ();

//		Utils.setTypefaceToAllViews (activity, holder.atm_pincode);


		holder.atm_last_audit_date.setTypeface (typeface);
		holder.atm_atm_unique_id.setTypeface (typeface);
		holder.atm_bank_name.setTypeface (typeface);
		holder.atm_address.setTypeface (typeface);
		holder.atm_city.setTypeface (typeface);
		holder.atm_pincode.setTypeface (typeface);


		final Atm atm = atmList.get (position);
		holder.atm_last_audit_date.setText (atm.getAtm_last_audit_date ());
		holder.atm_atm_unique_id.setText (atm.getAtm_unique_id ().toUpperCase ());
		holder.atm_bank_name.setText (atm.getAtm_bank_name ().toUpperCase ());
		holder.atm_address.setText (atm.getAtm_address ().toUpperCase ());
		holder.atm_city.setText (atm.getAtm_city ().toUpperCase ());
		holder.atm_pincode.setText (atm.getAtm_pincode ().toUpperCase ());

		convertView.setOnClickListener (new View.OnClickListener () {
			private void die () {
			}

			@Override
			public void onClick (View arg0) {
				if (checkPermissions ()) {

//				Constants.atm_unique_id = atm.getAtm_unique_id ().toUpperCase ();
//				Constants.atm_agency_id = atm.getAtm_agency_id ();

					Constants.report.setAtm_id (atm.getAtm_id ());
					Constants.report.setAuditor_id (Constants.auditor_id_main);
					Constants.report.setAgency_id (atm.getAtm_agency_id ());
					Constants.report.setAtm_unique_id (atm.getAtm_unique_id ().toUpperCase ());
					Constants.report.setLatitude (String.valueOf (Constants.latitude));
					Constants.report.setLongitude (String.valueOf (Constants.longitude));


					AlertDialog.Builder builder = new AlertDialog.Builder (activity);
					builder.setMessage ("Please take an image of the ATM Machine\nNote : This image will be Geotagged")
							.setCancelable (false)
							.setPositiveButton ("OK", new DialogInterface.OnClickListener () {
								public void onClick (DialogInterface dialog, int id) {
									dialog.dismiss ();
									Intent mIntent = null;
									if (Utils.isPackageExists (activity, "com.google.android.camera")) {
										mIntent = new Intent ();
										mIntent.setPackage ("com.google.android.camera");
										mIntent.setAction (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
									} else {
										PackageManager packageManager = activity.getPackageManager ();
										String defaultCameraPackage = null;
										List<ApplicationInfo> list = packageManager.getInstalledApplications (PackageManager.GET_UNINSTALLED_PACKAGES);
										for (int n = 0; n < list.size (); n++) {
											if ((list.get (n).flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
												Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "Installed Applications  : " + list.get (n).loadLabel (packageManager).toString (), false);
												Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "package name  : " + list.get (n).packageName, false);
												if (list.get (n).loadLabel (packageManager).toString ().equalsIgnoreCase ("Camera")) {
													defaultCameraPackage = list.get (n).packageName;
													break;
												}
											}
										}
										mIntent = new Intent ();
										mIntent.setPackage (defaultCameraPackage);
										mIntent.setAction (MediaStore.ACTION_IMAGE_CAPTURE);
										File f = new File (Environment.getExternalStorageDirectory () + File.separator + "img.jpg");
										mIntent.putExtra (MediaStore.EXTRA_OUTPUT, Uri.fromFile (f));
									}
									if (mIntent.resolveActivity (activity.getPackageManager ()) != null)
										activity.startActivityForResult (mIntent, MainActivity.GEO_IMAGE_REQUEST_CODE);
								}
							})
							.setNegativeButton ("CANCEL", new DialogInterface.OnClickListener () {
								public void onClick (DialogInterface dialog, int id) {
									dialog.dismiss ();
								}
							});
					AlertDialog alert = builder.create ();
					alert.show ();
				}
			}
		});
		return convertView;
	}

	public boolean checkPermissions () {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (activity.checkSelfPermission (Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				activity.requestPermissions (new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MainActivity.PERMISSION_REQUEST_CODE);
				return false;
			} else if (activity.checkSelfPermission (Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				activity.requestPermissions (new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.PERMISSION_REQUEST_CODE);
				return false;
			} else if (activity.checkSelfPermission (Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				activity.requestPermissions (new String[] {Manifest.permission.CAMERA}, MainActivity.PERMISSION_REQUEST_CODE);
				return false;
			}
/*
			if (activity.checkSelfPermission (Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
					activity.checkSelfPermission (Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
					activity.checkSelfPermission (Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
					activity.checkSelfPermission (Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED ||
					activity.checkSelfPermission (Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

				activity.requestPermissions (new String[] {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
								Manifest.permission.INTERNET, Manifest.permission.RECEIVE_BOOT_COMPLETED, Manifest.permission.WRITE_EXTERNAL_STORAGE},
						PERMISSION_REQUEST_CODE);
                return false;

*/
			else {
				return true;
			}
/*
            if (checkSelfPermission (Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions (new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MainActivity.PERMISSION_REQUEST_CODE);
            }
            if (checkSelfPermission (Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions (new String[] {Manifest.permission.INTERNET}, MainActivity.PERMISSION_REQUEST_CODE);
            }
            if (checkSelfPermission (Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions (new String[] {Manifest.permission.RECEIVE_BOOT_COMPLETED,}, MainActivity.PERMISSION_REQUEST_CODE);
            }
            if (checkSelfPermission (Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions (new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.PERMISSION_REQUEST_CODE);
            }
  */

		} else {
			return true;
		}
	}

	static class ViewHolder {
		TextView atm_last_audit_date;
		TextView atm_atm_unique_id;
		TextView atm_bank_name;
		TextView atm_address;
		TextView atm_city;
		TextView atm_pincode;
	}

}