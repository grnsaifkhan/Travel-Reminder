package com.example.travelreminder;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class InfoSave_Fragment extends Fragment {

    private static final String CHANNEL_ID = "CHANNEL_TWO";

    private ProgressBar mProgressBar;
    private int mProgressStatus ;
    private TextView mTextViewPercentage;

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
        View rootView = inflater.inflate(R.layout.info_save_fragment, container, false);

        //supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        bt_save = (Button)rootView.findViewById(R.id.save_button);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.pb);
        mTextViewPercentage = (TextView) rootView.findViewById(R.id.tv_percentage);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("AutoStatus");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    double percentage = (Double)dataSnapshot.getValue();
                    mProgressStatus = (int)(percentage*100);
                    mProgressBar.setProgress(mProgressStatus);
                    mTextViewPercentage.setText(""+mProgressStatus+"%");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);*/


        return rootView;
    }

}
