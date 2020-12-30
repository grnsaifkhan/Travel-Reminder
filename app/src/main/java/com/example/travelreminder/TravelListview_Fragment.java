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

public class TravelListview_Fragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.travel_listview_fragment,container,false);
        ArrayList<String> travelArrayList = new ArrayList<>();
        //Dummy Data
        travelArrayList.add("Chemnitz");
        travelArrayList.add("Dresden");
        travelArrayList.add("Leipzig");
        travelArrayList.add("Berlin");
        travelArrayList.add("Hermburg");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.listview_layout,travelArrayList);

        ListView travelList = (ListView) rootView.findViewById(R.id.travel_list_view);
        travelList.setAdapter(adapter);

        return rootView;
    }

}