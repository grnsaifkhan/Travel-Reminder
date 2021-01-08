package com.example.travelreminder;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_CODE = 1;
    MapView mMapView;
    private GoogleMap googleMap;
    EditText et_startPoint, et_destination;
    Button btn_add, btn_search;
    ImageButton ib_my_location;
    SupportMapFragment supportMapFragment;
    LatLng latLng;
    private FusedLocationProviderClient fusedLocationClient;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_fragment, container, false);

        et_startPoint = (EditText) rootView.findViewById(R.id.start_point);
        et_destination = (EditText) rootView.findViewById(R.id.destination);
        btn_add = (Button) rootView.findViewById(R.id.add_button);
        btn_search = (Button) rootView.findViewById(R.id.search_button);
        ib_my_location = (ImageButton)rootView.findViewById(R.id.ib_my_location);

        btn_add.setEnabled(false);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        searchForDestinationPoint();
        clickOnLocationOnMap();
        onStartPointLocation();


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
                             btn_add.setEnabled(true);
                             Geocoder geocoder = new Geocoder(getActivity());
                             try {
                                 addressList = geocoder.getFromLocationName(destPoint,1);
                             } catch (IOException e) {
                                 e.printStackTrace();
                             }
                             Address address = addressList.get(0);
                             latLng = new LatLng(address.getLatitude(),address.getLongitude());
                             MarkerOptions markerOptions = new MarkerOptions();
                             markerOptions.position(latLng);
                             markerOptions.title("Destination");
                             googleMap.clear();
                             googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                     latLng, 15

                             ));
                             googleMap.addMarker(markerOptions);

                             Toast.makeText(getActivity(), latLng.toString(), Toast.LENGTH_SHORT).show();
                         }
                     } catch (Exception e) {
                         e.printStackTrace();
                         btn_add.setEnabled(false);
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
                            et_destination.setText(address);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title("Destination");
                        googleMap.clear();
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                latLng, 15

                        ));
                        googleMap.addMarker(markerOptions);
                    }
                });
            }
        });
    }

    private void onStartPointLocation(){

        ib_my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });
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
                                    et_startPoint.setText(address);
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
         }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else{

            }
        }
    }

}
