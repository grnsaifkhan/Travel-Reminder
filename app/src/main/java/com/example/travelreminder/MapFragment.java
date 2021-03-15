package com.example.travelreminder;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class MapFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_CODE = 1;
    private static final int RECOGNIZER_RESULT = 1;

    MapView mMapView;
    private GoogleMap googleMap;

    DatabaseHelper mDatabaseHelper;

    EditText et_travel_name,et_destination,et_travel_date,et_travel_time;
    Button bt_save, btn_search;
    ImageButton ib_voice_command;

    SupportMapFragment supportMapFragment;
    LatLng latLng;

    private FusedLocationProviderClient fusedLocationClient;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_fragment, container, false);

        et_travel_name = (EditText) rootView.findViewById(R.id.travel_name);
        et_destination = (EditText) rootView.findViewById(R.id.destination);
        et_travel_date = (EditText) rootView.findViewById(R.id.travel_day);
        et_travel_time = (EditText) rootView.findViewById(R.id.travel_time);
        bt_save = (Button) rootView.findViewById(R.id.save_button);
        btn_search = (Button) rootView.findViewById(R.id.search_button);
        ib_voice_command = (ImageButton)rootView.findViewById(R.id.ib_my_location);

        mDatabaseHelper = new DatabaseHelper(getContext());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        searchForDestinationPoint();
        clickOnLocationOnMap();
        onVoiceSearch();
        autoCurrentLocationTrack();

        insertData();
        editTextToTimePicker();
        editTextToDatePicker();

        return rootView;
    }



    private void searchForDestinationPoint() {

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                btn_search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String destPoint = et_destination.getText().toString();
                        List<Address> addressList = null;
                        try {
                            if (destPoint != null || !destPoint.equals("")){
                                Geocoder geocoder = new Geocoder(getActivity());
                                try {
                                    addressList = geocoder.getFromLocationName(destPoint,1);
                                    Address address = addressList.get(0);
                                    latLng = new LatLng(address.getLatitude(),address.getLongitude());
                                    String destinationName = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1).get(0).getAddressLine(0);

                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(latLng);
                                    markerOptions.icon(bitmapDescriptorFromVector(getContext(),R.drawable.ic_baseline_destination_focus));
                                    markerOptions.title("Destination");
                                    markerOptions.snippet(destinationName);
                                    googleMap.clear();
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                            latLng, 15

                                    ));
                                    googleMap.addMarker(markerOptions);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            et_destination.setError("Please fill up destination properly");
                        }
                    }
                });
            }
        });

    }

    private void clickOnLocationOnMap(){
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        et_destination.setError(null);
                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                            String address = addresses.get(0).getAddressLine(0);
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.icon(bitmapDescriptorFromVector(getContext(),R.drawable.ic_baseline_destination_focus));
                            markerOptions.title("Destination");
                            markerOptions.snippet(address);
                            googleMap.clear();
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    latLng, 15

                            ));
                            googleMap.addMarker(markerOptions);
                            et_destination.setText(address);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void onVoiceSearch(){

        ib_voice_command.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getCurrentLocation();
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask location");
                startActivityForResult(speechIntent,RECOGNIZER_RESULT);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            et_destination.setText(matches.get(0).toString());

            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    String destPoint = et_destination.getText().toString();
                    List<Address> addressList = null;
                    try {
                        if (destPoint != null || !destPoint.equals("")){
                            Geocoder geocoder = new Geocoder(getActivity());
                            try {
                                addressList = geocoder.getFromLocationName(destPoint,1);
                                Address address = addressList.get(0);
                                latLng = new LatLng(address.getLatitude(),address.getLongitude());
                                String destinationName = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1).get(0).getAddressLine(0);

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.icon(bitmapDescriptorFromVector(getContext(),R.drawable.ic_baseline_destination_focus));
                                markerOptions.title("Destination");
                                markerOptions.snippet(destinationName);
                                googleMap.clear();
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        latLng, 15

                                ));
                                googleMap.addMarker(markerOptions);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        et_destination.setError("Please fill up destination properly");
                    }
                }
            });
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getCurrentLocation(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(getActivity())
                        .setTitle("Required Location Permission")
                        .setMessage("Allow permission to access this feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_CODE);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                Double latitude = location.getLatitude();
                                Double longitude = location.getLongitude();

                                Geocoder geocoder = new Geocoder(getActivity());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(latitude,longitude,1);
                                    String address = addresses.get(0).getAddressLine(0);
                                    et_destination.setText(address);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                            else{
                                setLocationListener();
                            }
                        }
                    });

        }
    }

    public void autoCurrentLocationTrack(){
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                        new AlertDialog.Builder(getActivity())
                                .setTitle("Required Location Permission")
                                .setMessage("Allow permission to access this feature")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ActivityCompat.requestPermissions(getActivity(),
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                                                MY_PERMISSIONS_REQUEST_CODE);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .create()
                                .show();


                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_CODE);
                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        // Logic to handle location object
                                        Double latitude = location.getLatitude();
                                        Double longitude = location.getLongitude();

                                        Geocoder geocoder = new Geocoder(getActivity());
                                        List<Address> addresses = null;
                                        try {
                                            addresses = geocoder.getFromLocation(latitude,longitude,1);
                                            String address = addresses.get(0).getAddressLine(0);
                                            latLng = new LatLng(latitude,longitude);
                                            MarkerOptions markerOptions = new MarkerOptions();
                                            markerOptions.position(latLng);
                                            markerOptions.icon(bitmapDescriptorFromVector(getContext(),R.drawable.ic_baseline_current_location));
                                            markerOptions.title("Current Location");
                                            markerOptions.snippet(address);
                                            googleMap.clear();
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                                    latLng, 15

                                            ));
                                            googleMap.addMarker(markerOptions);
                                        } catch (IOException e) {
                                            warningMessage("Something went wrong","Unable to find your current location. Please restart app");
                                            e.printStackTrace();
                                        }

                                    }
                                    else{
                                        setLocationListener();
                                    }
                                }
                            });
                }
            }
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void setLocationListener(){

        Boolean gps_enable, network_enable;

        gps_enable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enable  = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

         if (gps_enable == false) {
             new AlertDialog.Builder(getActivity())
                     .setTitle("Location permission required")
                     .setMessage("Please turn on location")
                     .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                             Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                             startActivity(intent);
                         }
                     })
                     .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                             dialogInterface.dismiss();
                         }
                     })
                     .create()
                     .show();
         }else{
             autoCurrentLocationTrack();
         }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                autoCurrentLocationTrack();
            }else{

            }
        }
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
                        et_travel_time.setText((selectedHour>9?selectedHour:"0"+selectedHour)+":"+(selectedMinute>9?selectedMinute:"0"+selectedMinute));
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
                        et_travel_date.setText((dayOfMonth>9?dayOfMonth:"0"+dayOfMonth)+"/"+((month+1)>9?(month+1):"0"+(month+1))+"/"+year);
                    }
                },day,month,year);
                datePickerDialog.setTitle("Select Travel date");
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
