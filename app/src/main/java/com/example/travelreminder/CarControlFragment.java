package com.example.travelreminder;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CarControlFragment extends Fragment {

    private static final String CHANNEL_ID = "CHANNEL_TWO";

    private ProgressBar mProgressBar;
    private int mProgressStatus, mTemperatureValue ;
    private TextView mTextViewPercentage, tvBatteryStatus, tvDoorData, tvHeadLightData, tvTemperature;
    private Button bt_powerStatus, bt_carLockStatus, bt_doorLock, bt_headLight,  bt_temperatureIncrease, bt_temperatureDecrease, bt_ac_status;

    DatabaseHelper mDatabaseHelper;
    Button bt_save;
    EditText et_travel_name, et_destination;

    GoogleMap googleMap;
    SupportMapFragment supportMapFragment;
    LocationManager locationManager;
    LocationListener locationListener;

    //Firebase
    DatabaseReference databaseReference;
    final long MIN_TIME = 1000;
    final long MIN_DIST = 5;




    LatLng latLng;

    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.car_control_fragment, container, false);

        //supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.pb);
        mTextViewPercentage = (TextView) rootView.findViewById(R.id.tv_percentage);
        tvBatteryStatus = (TextView) rootView.findViewById(R.id.tv_battery_status);
        tvDoorData = (TextView) rootView.findViewById(R.id.tv_doorLockStatus);
        tvHeadLightData = (TextView) rootView.findViewById(R.id.tv_headlightStatus);
        tvTemperature = (TextView) rootView.findViewById(R.id.tv_temperatureStatus);

        bt_powerStatus = (Button) rootView.findViewById(R.id.bt_powerStatus);
        bt_carLockStatus = (Button) rootView.findViewById(R.id.bt_carLockStatus);
        bt_doorLock = (Button) rootView.findViewById(R.id.bt_doorLockStatus);
        bt_headLight = (Button) rootView.findViewById(R.id.bt_headlightStatus);
        bt_ac_status = (Button) rootView.findViewById(R.id.bt_ac_status);
        bt_temperatureIncrease = (Button) rootView.findViewById(R.id.bt_temperatureStatusIncrs);
        bt_temperatureDecrease = (Button) rootView.findViewById(R.id.bt_temperatureStatusDecrs);




        databaseReference = FirebaseDatabase.getInstance().getReference().child("AutoStatus");
        databaseReference.addValueEventListener(new ValueEventListener() {

            HashMap<String,String> firebaseData = new HashMap<String,String>();
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    firebaseData.put(dataSnapshot.getKey(),dataSnapshot.getValue().toString());
                    Log.e("FIREBASE",firebaseData.get(dataSnapshot.getKey()));
                }
                double percentage = Double.parseDouble(firebaseData.get("BatteryValue"));
                mProgressStatus = (int)(percentage*100);
                mProgressBar.setProgress(mProgressStatus);
                mTextViewPercentage.setText(""+mProgressStatus+"%");
                tvBatteryStatus.setText(firebaseData.get("BatteryStatus"));
                tvDoorData.setText(firebaseData.get("DoorLockStatus"));
                tvHeadLightData.setText(firebaseData.get("HeadlightStatus"));

                double temperatureValue = Double.parseDouble(firebaseData.get("TemperatureValue"));
                mTemperatureValue = (int)(temperatureValue*100);
                tvTemperature.setText(""+mTemperatureValue+"°C");

                if (firebaseData.get("PowerStatus").equals("poweron")){
                    bt_powerStatus.setText("Power Off");
                }else{
                    bt_powerStatus.setText("Power On");
                }

                if (firebaseData.get("CarLockStatus").equals("locked")){
                    bt_carLockStatus.setText("Unlock");
                }else{
                    bt_carLockStatus.setText("Lock");
                }

                if (firebaseData.get("DoorLockStatus").equals("Door Locked")){
                    bt_doorLock.setText("Unlock");
                }else{
                    bt_doorLock.setText("Lock");
                }

                if (firebaseData.get("HeadlightStatus").equals("Light on")){
                    bt_headLight.setText("Off");
                }else{
                    bt_headLight.setText("On");
                }

                if (firebaseData.get("ACStatus").equals("on")){
                    bt_ac_status.setText("Off");
                }else{
                    bt_ac_status.setText("On");
                }

                setTemperature(temperatureValue);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);*/

        statusChange();

        return rootView;
    }

    public void statusChange(){
        bt_powerStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bt_powerStatus.getText().equals("Power On")){
                    updateStatusOnFirebase("PowerStatus","poweron");
                    bt_powerStatus.setText("Power Off");
                }else{
                    updateStatusOnFirebase("PowerStatus","poweroff");
                    bt_powerStatus.setText("Power On");
                }
            }
        });

        bt_carLockStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bt_carLockStatus.getText().equals("Lock")){
                    updateStatusOnFirebase("CarLockStatus","locked");
                    bt_carLockStatus.setText("Unlock");
                }else{
                    updateStatusOnFirebase("CarLockStatus","unlocked");
                    bt_carLockStatus.setText("Lock");
                }
            }
        });

        bt_doorLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bt_doorLock.getText().equals("Lock")){
                    updateStatusOnFirebase("DoorLockStatus","Door Locked");
                    bt_doorLock.setText("Unlock");
                }else{
                    updateStatusOnFirebase("DoorLockStatus","Door Unlocked");
                    bt_doorLock.setText("Lock");
                }
            }
        });
        bt_headLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bt_headLight.getText().equals("On")){
                    updateStatusOnFirebase("HeadlightStatus","Light on");
                    bt_headLight.setText("Off");
                }else{
                    updateStatusOnFirebase("HeadlightStatus","Light off");
                    bt_headLight.setText("On");
                }
            }
        });

        bt_ac_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bt_ac_status.getText().equals("Off")){
                    updateStatusOnFirebase("ACStatus","off");
                    bt_ac_status.setText("On");
                }else{
                    updateStatusOnFirebase("ACStatus","on");
                    bt_ac_status.setText("Off");
                }
            }
        });


    }

    public void setTemperature(final Double temperatureData){
        bt_temperatureIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bt_ac_status.getText().equals("Off") && temperatureData<0.3){
                    double tempValue = temperatureData + 0.01;
                    String tempValueString = String.valueOf(tempValue);
                    updateStatusOnFirebase("TemperatureValue",tempValueString);
                }
            }
        });

        bt_temperatureDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bt_ac_status.getText().equals("Off") && temperatureData>(-0.04)){
                    double tempValue = temperatureData - 0.01;
                    String tempValueString = String.valueOf(tempValue);
                    updateStatusOnFirebase("TemperatureValue",tempValueString);
                }
            }
        });
    }

    public void updateStatusOnFirebase(final String key, final String value){
                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                DatabaseReference mDbRef = mDatabase.getReference("AutoStatus/"+key);
                mDbRef.setValue(value);
            }
    }
