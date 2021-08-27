package com.cst2335.cst2335_finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;


public class SoccerEmpty extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccer_empty);



        //this is to get the data that was passed from FragmentExample
        Bundle dataToPass = getIntent().getExtras();

        //This is copied directly from FragmentExample... 43-48
        SoccerDetailsFragment dFragment = new SoccerDetailsFragment();
        dFragment.setArguments(dataToPass); //pass data to the the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, dFragment) //add fragment to frame layout
                //this will load the fragment
                //calls the onCreate() in DetailFragment
                .commit();


        //this empty activity is to show the details fragment when article is clicked

    }
}
