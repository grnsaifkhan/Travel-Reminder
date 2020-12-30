package com.example.travelreminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment mapViewFragment;
    Fragment infoSaveFragment;
    Fragment travelListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mapViewFragment = new MapFragment();
        infoSaveFragment = new InfoSave_Fragment();
        travelListFragment = new TravelListview_Fragment();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation) ;
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        //set Default fragment
        //set default fragment
        loadFragment(travelListFragment);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        Log.e("TAG", "DATABASE CONNECTED " + database);


    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, travelListFragment);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.travel:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container,travelListFragment)
                                    .commit();
                            return true;
                        case R.id.menu_location:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container,mapViewFragment)
                                    .commit();
                            return true;

                        case R.id.menu_search:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container,infoSaveFragment)
                                    .commit();
                            return true;
                        case R.id.menu_account:
                            return true;
                    }
                    return true;
                }

            };
}