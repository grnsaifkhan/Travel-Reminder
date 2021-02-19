package com.example.travelreminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

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

        final ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
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
            final HashMap<String , String> data  = new HashMap<>();
            data.put("travel_id" , travelData.getString(0));
            data.put("travel_name" , travelData.getString(1));
            data.put("travel_destination" , travelData.getString(2));
            data.put("travel_date" , travelData.getString(3));
            data.put("travel_time", travelData.getString(4));
            arrayList.add(data);
        }

        travelList = (ListView) rootView.findViewById(R.id.travel_list_view);

        customListViewAdapter = new CustomListViewAdapter(getContext(), arrayList);
        travelList.setAdapter(customListViewAdapter);

        travelList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, String> travelDetails = new HashMap<>();
                travelDetails = arrayList.get(i);
                final int position = i;
                final String travelId = travelDetails.get("travel_id");
                String travelName = travelDetails.get("travel_name");

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Remove "+travelName+"?");
                builder.setMessage("Once removed, data cannot be recovered!");
                builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabaseHelper.deleteData(Integer.parseInt(travelId));
                        arrayList.remove(position);
                        customListViewAdapter.notifyDataSetChanged();
                        customListViewAdapter.notifyDataSetInvalidated();
                        Toast.makeText(getActivity(), "Data removed successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                return true;
            }
        });

        return rootView;
    }


}