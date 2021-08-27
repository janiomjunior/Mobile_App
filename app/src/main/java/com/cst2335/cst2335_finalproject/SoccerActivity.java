package com.cst2335.cst2335_finalproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.cst2335.cst2335_finalproject.trivia.TriviaActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;


public class SoccerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ListView lvRss;

    Button goToFavourites;

    public static final String ITEM_TITLE = "TITLE";
    public static final String ITEM_DATE = "PUBDATE";
    public static final String ITEM_LINK = "LINK";
    public static final String ITEM_DESCRIPTION = "DESCRIPTION";

    //setting array lists of type string
    ArrayList<String> titles;
    ArrayList<String> date;
    ArrayList<String> links;
    ArrayList<String> description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccer);


        //This gets the toolbar from the layout:
        Toolbar tBar = (Toolbar)findViewById(R.id.soccerToolBar);
        setSupportActionBar(tBar);

        //Progress bar
        ProgressBar bar =  findViewById(R.id.soccerProgressBar);
        bar.setVisibility(View.INVISIBLE);

        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.soccer_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        //fragment lab 7 example 03/25
        boolean isTablet = findViewById(R.id.fragmentLocation) != null;

        lvRss = (ListView) findViewById(R.id.lvRss);

        titles = new ArrayList<String>();
        date = new ArrayList<String>();
        links = new ArrayList<String>();
        description = new ArrayList<String>();


        goToFavourites = findViewById(R.id.articleFavorites);
        goToFavourites.setOnClickListener(v -> {
            Intent goToFavourites = new Intent(SoccerActivity.this, SoccerFavorites.class);
            startActivity(goToFavourites);
        });


        //goes to fragment
        lvRss.setOnItemClickListener((list, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_TITLE, titles.get(position));
            dataToPass.putString(ITEM_DATE, date.get(position));
            dataToPass.putString(ITEM_LINK, links.get(position));
            dataToPass.putString(ITEM_DESCRIPTION, description.get(position));

            if(isTablet)
            {
                SoccerDetailsFragment dFragment = new SoccerDetailsFragment();
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(SoccerActivity.this, SoccerEmpty.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });

        new ProcessInBackground().execute();
    }




    public InputStream getInputStream (URL url) {
        try {
            return url.openConnection().getInputStream();
        }
        catch (IOException e) {
            return null;
        }
    }

    //async task to execute
    public class ProcessInBackground extends AsyncTask<Integer, Void, Exception> {

        ProgressDialog progressDialog = new ProgressDialog(SoccerActivity.this);

        Exception exception = null;

        //before we go to background
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Busy loading RSS Feed...please wait");
            progressDialog.show();
        }

        //Integer is the variable that is being accepted from ^
        @Override
        protected Exception doInBackground(Integer... params) {

            try {
                //set up URL object
                //https://www.goal.com/feeds/en/news
                URL url = new URL("https://www.goal.com/feeds/en/news");

                //makes new xml pull parsers
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                //make false to not provide support to xml name spaces
                factory.setNamespaceAware(false);

                //makes new pull parser
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(getInputStream(url), "UTF-8");

                //data extracts only inside item tag <> so must make it false in the beginning
                boolean insideItem = false;

                int eventType = xpp.getEventType();

                //continuously loops, reading this until it reaches the end document, then stops
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            //we are inside the tag, so now it's true
                            insideItem = true;
                        }
                        else if (xpp.getName().equalsIgnoreCase("title")) {
                            //denotes if we are inside the title tag and the title tag
                            //is inside the item tag then continue on with the if loop
                            if (insideItem) {
                                //returns text between the title tags
                                titles.add(xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                            //denotes if we are inside the title tag and the title tag
                            //is inside the item tag then continue on with the if loop
                            if (insideItem) {
                                //returns text between the title tags
                                date.add(xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("link")) {
                            //same process as the else if statement
                            //if it fulfills the conditions of being inside these tags, continue on
                            if (insideItem) {
                                //returns text between the link tags
                                links.add(xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("description")) {
                            //same process as the else if statement
                            //if it fulfills the conditions of being inside these tags, continue on
                            if (insideItem) {
                                //returns text between the link tags
                                description.add(xpp.nextText());
                            }
                        }

                    }
                    else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {

                        insideItem = false;
                    }
                    //goes to the next tag
                    eventType = xpp.next();
                }
            }
            catch (MalformedURLException e) {
                exception = e;
            }
            catch (XmlPullParserException e) {
                exception = e;
            }
            catch (IOException e) {
                exception = e;
            }
            return exception;
        }

        /**
         * This method will make the connection and show it on the list view
         * Array Adapter is used to update the list and will be used to show the
         * array of titles (we are only showing one item) on the list view.
         * @param s
         */
        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);


            ArrayAdapter<String> adapter  = new ArrayAdapter<String>(SoccerActivity.this, android.R.layout.simple_list_item_1, titles);

            //sets the adapter
            lvRss.setAdapter(adapter);

            //do this when everything is done so no more is shown
            progressDialog.dismiss();
        }
    }

    /**
     * This method loads the toolbar @param menu and @return to the activity */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.soccer_menu, menu);
        return true;
    }

    /**
     * This method executes actions when each item of menu is clicked
     * it will show the alert dialog explaining how the app is used if the
     * user has any issue
     * @param item
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menuHomeIcon1:

                Toast.makeText(this, R.string.soccerHome, Toast.LENGTH_SHORT).show();

                // ******Creating intent that leads to TriviaActivity***************
                Intent home = new Intent(this, MainActivity.class);
                this.startActivity(home);
                // ******End of intent that leads to MainActivity***************
                return true;

            case R.id.aboutSoccer:
                //Toast.makeText(this, R.string.soccerAbout, Toast.LENGTH_SHORT).show();
                //creates the alert dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Soccer App Help")

                //what is the message
                .setMessage("Do you need help on how to use this application?")

                //positive button = yes button
                .setPositiveButton("Yes", (click, arg) -> {

                    Toast.makeText(this, R.string.helpMe, Toast.LENGTH_LONG).show();

                });

                //negative button = nothing needed
                alertDialogBuilder.setNegativeButton("No", (click, arg) -> {

                });

                //show the dialog created above
                alertDialogBuilder.create().show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    // Needed for the OnNavigationItemSelected interface:

    /**
     * this method will execute the following actions
     * of going to different apps depending on what is clicked
     * @param item
     * @return true
     */
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {


        switch(item.getItemId())
        {
            case R.id.goToCar:
                Intent goToCar = new Intent(this, CarActivity.class);
                startActivity(goToCar);
                break;
            case R.id.goToTrivia:
                Intent goToTrivia = new Intent(this, TriviaActivity.class);
                startActivity(goToTrivia);
                break;
            case R.id.goToSong:
                Intent goToSong = new Intent(this, SongActivity.class);
                startActivity(goToSong);
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.soccer_drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);


        return false;
    }

}
