package com.example.travelreminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomListViewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<HashMap<String, String>> travels;
    public static LayoutInflater inflater = null;



    public CustomListViewAdapter(Context context , ArrayList<HashMap<String , String>> data){

        mContext = context;
        travels = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return travels.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {


            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_row, viewGroup, false);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView dateText = (TextView) convertView.findViewById(R.id.dateText);
            TextView timeText = (TextView) convertView.findViewById(R.id.timeText);

            HashMap<String, String> travelDetails = new HashMap<>();

        travelDetails = travels.get(position);


            name.setText(travelDetails.get("name"));
            dateText.setText(travelDetails.get("dateText"));
            timeText.setText(travelDetails.get("timeText"));

        return convertView;
    }
}
