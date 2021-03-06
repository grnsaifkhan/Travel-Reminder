package com.example.travelreminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
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

        travelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, String> travelDetails = new HashMap<>();
                travelDetails = arrayList.get(i);
                final int position = i;
                final String travelId = travelDetails.get("travel_id");
                String travelName = travelDetails.get("travel_name");
                String destination = travelDetails.get("travel_destination");
                String travelDate = travelDetails.get("travel_date");
                String travelTime = travelDetails.get("travel_time");
                String[] date = travelDate.split("/");
                String[] time = travelTime.split(":");
                alarmCreator(travelId,travelName,destination,travelDate,travelTime);


            }
        });

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

    public void alarmCreator(String travelId, final String travelName, final String destination, final String travelDate, final String travelTime) {
        final int notificationId = 1;
        final int RQS_1 = 1002;
        final String[] date = travelDate.split("/");
        final String[] time = travelTime.split(":");
        final int travelID = Integer.parseInt(travelId);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Notify you?");
        builder.setMessage("Travel name: "+ travelName+"\n Destination: "+destination+"\n Travel Date: "
                +travelDate+"\n Travel time: "+travelTime);
        builder.setPositiveButton("Notify me", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Calendar myAlarmTime = Calendar.getInstance();
                myAlarmTime.setTimeInMillis(System.currentTimeMillis());
                myAlarmTime.set(Integer.parseInt(date[2]),(Integer.parseInt(date[1])-1) , Integer.parseInt(date[0]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), 0);


                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                if (alarmManager==null) return;

                Intent _myIntent = new Intent(getContext(), TourNotificationReceiver.class);
                _myIntent.putExtra("notificationid",notificationId);
                _myIntent.putExtra("travelName",travelName);
                _myIntent.putExtra("destination",destination);
                _myIntent.putExtra("travelDate",travelDate);
                _myIntent.putExtra("travelTime",travelTime);
                PendingIntent _myPendingIntent = PendingIntent.getBroadcast(getContext(),
                        RQS_1+Integer.parseInt(date[0])+Integer.parseInt(date[1])+Integer.parseInt(date[2])+Integer.parseInt(time[0])+Integer.parseInt(time[1]),
                        _myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                alarmManager.set(AlarmManager.RTC_WAKEUP, myAlarmTime.getTimeInMillis(),_myPendingIntent);

                Toast.makeText(getContext(), "You will be notified!!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Dismiss Notification", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelAlarm(RQS_1,Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]),
                        Integer.parseInt(time[0]),Integer.parseInt(time[1]));
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void cancelAlarm(int RQS_1,int day,int month,int year,int hour, int minute) {
        Intent intent = new Intent(getContext(), TourNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(), RQS_1 + day + month + year + hour + minute, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
        Toast.makeText(getContext(), "Notification is dismissed", Toast.LENGTH_SHORT).show();

    }


}