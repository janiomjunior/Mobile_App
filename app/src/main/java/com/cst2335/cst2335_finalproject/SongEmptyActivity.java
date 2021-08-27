package com.cst2335.cst2335_finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * this class is used to retrieve the data from FragmentExample
 */
public class SongEmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_song);

        Bundle dataToPass = getIntent().getExtras(); //get the data that was passed from FragmentExample

        //This is copied directly from FragmentExample.java lines 47-54
        SongDetailFragment dFragment = new SongDetailFragment();
        dFragment.setArguments( dataToPass ); //pass data to the the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, dFragment)
                .commit();
    }
}