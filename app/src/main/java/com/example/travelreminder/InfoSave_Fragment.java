package com.example.travelreminder;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class InfoSave_Fragment extends Fragment {

    DatabaseHelper mDatabaseHelper;
    Button bt_save;
    EditText et_travel_name, et_destination, et_travel_date, et_travel_time;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.info_save_fragment, container, false);

        et_travel_name = (EditText) rootView.findViewById(R.id.travel_name);
        et_destination = (EditText) rootView.findViewById(R.id.destination);
        et_travel_date = (EditText) rootView.findViewById(R.id.travel_day);
        et_travel_time = (EditText) rootView.findViewById(R.id.travel_time);
        bt_save = (Button)rootView.findViewById(R.id.save_button);

        mDatabaseHelper = new DatabaseHelper(getContext());

        insertData();
        editTextToTimePicker();
        editTextToDatePicker();


        return rootView;
    }

    public void insertData(){
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_travel_name.length() == 0 || et_destination.length() == 0 ||et_travel_date.length()==0||et_travel_time.length()==0){
                    warningMessage("Warning!", "Please fill up every text field properly!");
                }else{
                    boolean inputData = mDatabaseHelper.insertData(et_travel_name.getText().toString(),et_destination.getText().toString(), et_travel_date.getText().toString(),et_travel_time.getText().toString());

                    if (inputData == true){
                        warningMessage("Notice : ","Travel info added successfully");
                        et_travel_name.setText("");
                        et_destination.setText("");
                        et_travel_date.setText("");
                        et_travel_time.setText("");
                    }
                    else {
                        warningMessage("Warning","Data not inserted");
                    }
                }
            }
        });
    }

    public void editTextToTimePicker(){
        et_travel_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mCurrentTime =  Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        et_travel_time.setText(selectedHour+":"+selectedMinute);
                    }
                },hour,minute,true);
                timePickerDialog.setTitle("Select Travel time");
                timePickerDialog.show();
            }
        });
    }

    public void editTextToDatePicker(){
        et_travel_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        et_travel_date.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },day,month,year);
                datePickerDialog.show();
            }
        });
    }

    public void warningMessage(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
