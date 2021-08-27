package com.cst2335.cst2335_finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;

public class SoccerOpenerActivity extends AppCompatActivity {

    //variable names
    private EditText editTextName;
    private EditText editTextPass;
    private Button buttonLoginSoccer;
    private String name;

    //shared preferences to store and retrieve small amounts of data
    public String SHARED_PREFS = "shared_preferences";
    public String userNameSoccer = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccer_opener);

        buttonLoginSoccer = (Button) findViewById(R.id.loginButton);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextPass = (EditText) findViewById(R.id.editTextPass);

        //load name from SharedPreferences
        SharedPreferences shared_pref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        name = shared_pref.getString(userNameSoccer, "");

        //set the name
        editTextName.setText(name);

        //this will show in the bottom when this activity is opened
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.welcomeSnackBar, Snackbar.LENGTH_LONG);
        snackbar.show();

        buttonLoginSoccer.setOnClickListener(v -> {

            Intent goToProfile = new Intent(SoccerOpenerActivity.this, SoccerActivity.class);

            goToProfile.putExtra("Name", editTextName.getText().toString());
            startActivity(goToProfile);
        });

    }

    /**
     * this method will save the name that was entered in shared preference
     * so next time user opens the soccer app, their name will be displayed
     * prior to login
     */
    @Override
    protected void onPause() {
        super.onPause();

        //save the name entered
        SharedPreferences shared_pref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor Editor = shared_pref.edit();
        Editor.putString(userNameSoccer, editTextName.getText().toString());

        Editor.apply();
    }

}