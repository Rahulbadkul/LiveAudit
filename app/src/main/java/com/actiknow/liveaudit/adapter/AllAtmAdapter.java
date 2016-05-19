package com.actiknow.liveaudit.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.actiknow.liveaudit.R;
import com.actiknow.liveaudit.activity.ViewPagerActivity;
import com.actiknow.liveaudit.model.Atm;
import com.actiknow.liveaudit.utils.Constants;
import com.actiknow.liveaudit.utils.Utils;

import java.util.List;

public class AllAtmAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private List<Atm> atms;

	public AllAtmAdapter (Activity activity, List<Atm> atms) {
		this.activity = activity;
		this.atms = atms;
	}

	@Override
	public int getCount() {
		return atms.size();
	}

	@Override
	public Object getItem(int location) {
		return atms.get(location);
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
		} else
			holder = (ViewHolder) convertView.getTag ();

		Utils.setTypefaceToAllViews (activity, holder.atm_pincode);

		final Atm atm = atms.get (position);
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
				Constants.atm_unique_id = atm.getAtm_unique_id ().toUpperCase ();
				Constants.atm_agency_id = atm.getAtm_agency_id ();
				Intent intent = new Intent (activity, ViewPagerActivity.class);
				activity.startActivity (intent);
				activity.overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});
		return convertView;
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