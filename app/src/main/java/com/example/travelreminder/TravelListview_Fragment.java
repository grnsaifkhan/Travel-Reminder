package com.example.travelreminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class TravelListview_Fragment extends Fragment {

    private static final String TAG = "TravelListViewFragment" ;
    ListView travelList;
    CustomListViewAdapter customListViewAdapter;

    DatabaseHelper mDatabaseHelper;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.travel_listview_fragment,container,false);
        mDatabaseHelper = new DatabaseHelper(getContext());

        Log.d(TAG, "populateListView: Displaying data in the ListView");

        //get data and append the list
        final Cursor travelData = mDatabaseHelper.getData();

        ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
/*        for (int i = 0;i<10;i++){
            HashMap<String , String> data  = new HashMap<>();
            data.put("travel_name" ,"Munich");
            data.put("travel_date" , "12/13/1020");
            data.put("travel_time", "12:56");

            arrayList.add(data);
        }*/
        //travelList = (ListView) rootView.findViewById(R.id.travel_list_view);

        /*while(travelData.getCount()==0){
            tv_empty_text.setText("No Course Added");
        }*/

        while(travelData.moveToNext()){
            HashMap<String , String> data  = new HashMap<>();
            data.put("travel_name" , travelData.getString(1));
            data.put("travel_destination" , travelData.getString(2));
            data.put("travel_date" , travelData.getString(3));
            data.put("travel_time", travelData.getString(4));
            arrayList.add(data);
        }

        travelList = (ListView) rootView.findViewById(R.id.travel_list_view);

        customListViewAdapter = new CustomListViewAdapter(getContext(), arrayList);
        travelList.setAdapter(customListViewAdapter);

        return rootView;
    }


}