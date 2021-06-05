package com.example.teayudamos.recyclers;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.teayudamos.R;
import com.example.teayudamos.model.Location;
import com.google.firebase.firestore.GeoPoint;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class LocationAdapter extends ArrayAdapter<Location> {
    private Context mContext;
    int mResource;

    public LocationAdapter(Context context, int resource, ArrayList<Location> locations) {
        super(context, resource, locations);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String action = getItem(position).getAction().toUpperCase();
        String datetime = getItem(position).getDatetime();
        String userId = getItem(position).getUserId();
        ArrayList<GeoPoint> points = getItem(position).getPoints();
        String road = getItem(position).getRoad();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        //TextView tvAction = (TextView) convertView.findViewById(R.id.action);
        TextView tvDatetime = (TextView) convertView.findViewById(R.id.datetime);
        TextView tvRoad = (TextView) convertView.findViewById(R.id.road);
        CardView tvCard = (CardView) convertView.findViewById(R.id.card);

        if (action.equals("CAIDA")) {
            tvCard.setCardBackgroundColor(Color.parseColor("#f44336"));
        } else {
            tvCard.setBackgroundColor(Color.parseColor("#264653"));
        }

        //tvAction.setText(action);
        tvDatetime.setText(datetime);
        tvRoad.setText(road);
        return convertView;

    }
}
