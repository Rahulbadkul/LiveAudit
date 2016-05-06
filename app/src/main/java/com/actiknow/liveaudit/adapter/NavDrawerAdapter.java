package com.actiknow.liveaudit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.actiknow.liveaudit.R;


public class NavDrawerAdapter extends BaseAdapter {
    Context context;
    String[] data;
    private LayoutInflater inflater = null;
    public NavDrawerAdapter (Context context, String[] data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return data.length;
    }
    @Override
    public Object getItem(int position) {
        return data[position];
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.listview_item_nav_drawer, null);
        TextView text = (TextView) vi.findViewById(R.id.textViewnavdraweritem);
        ImageView icon = (ImageView) vi.findViewById(R.id.imageViewnavdraweritem);
        switch (position) {
            case 0 : icon.setImageResource(R.drawable.ic_home);
                icon.setVisibility (View.VISIBLE);
                break;
        }
        text.setText (data[position]);
        return vi;
    }
}
