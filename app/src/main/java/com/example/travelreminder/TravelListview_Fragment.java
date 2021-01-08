package com.example.travelreminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class TravelListview_Fragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.travel_listview_fragment,container,false);

        ArrayList<HashMap<String , String>> arrayList = new ArrayList<>();

        for (int i = 0;i<20;i++){
            HashMap<String , String> data  = new HashMap<>();
            data.put("name" , "Munich");
            data.put("dateText" , "10-01-2021");
            data.put("timeText", "12:00");

            arrayList.add(data);
        }

        ListView travelList = (ListView) rootView.findViewById(R.id.travel_list_view);
        CustomListViewAdapter customListViewAdapter = new CustomListViewAdapter(getContext(), arrayList);
        travelList.setAdapter(customListViewAdapter);

        return rootView;
    }

}