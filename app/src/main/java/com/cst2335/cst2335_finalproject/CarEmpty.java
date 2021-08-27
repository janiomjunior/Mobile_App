package com.cst2335.cst2335_finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CarEmpty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_empty);

        Bundle dataToPass = getIntent().getExtras(); //get the data
        CarDetailFrag dFragment = new CarDetailFrag(); //add a DetailFragment
        dFragment.setArguments( dataToPass ); //pass it a bundle for information
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.carFrag, dFragment) //Add the fragment in FrameLayout
                .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
    }
}