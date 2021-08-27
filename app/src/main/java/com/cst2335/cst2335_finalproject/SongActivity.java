package com.cst2335.cst2335_finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.cst2335.cst2335_finalproject.trivia.TriviaActivity;
import com.cst2335.cst2335_finalproject.trivia.TriviaPlayActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * the main class of the project
 * @author janiomjunior
 */

public class SongActivity extends AppCompatActivity {
    // the variables will be used to retrieve the data from AsyncTask */
    String title, searchTerm, songId, artist, artistId, attrib = "";

    // create instances of classes to work with MyListAdapter, SongFound, DetailFragment
    //and a variable SharedPreferences*/
    private MyListAdapter myAdapter;
    private SongFound obj;
    SongDetailFragment dFragment;
    SharedPreferences prefs = null;


    private AppCompatActivity parentActivity;

    // setting the variables to be used on database information */
    public static final String ITEM_TITLE = "TITLE";
    public static final String ITEM_ARTIST = "ARTIST";
    public static final String ITEM_SONGID = "SONGID";
    public static final String ITEM_ARITSTID = "ARTISTID";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_ID = "ID";
    public static final String REMOVEBUTTON = "N";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        //This gets the toolbar from the layout:
        Toolbar tBar = findViewById(R.id.toolbar);

        //This loads the toolbar, which calls onCreateOptionsMenu below:
        setSupportActionBar(tBar);

        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.song_open, R.string.song_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        //retrieve the search button from activity_song.xml
        Button search = findViewById(R.id.buttonSearch);
        EditText bandArtist = findViewById(R.id.textBandArtist);

        //creates a a file to store the last search for open the app the next time
        prefs = getSharedPreferences("LastTermSearched", Context.MODE_PRIVATE);
        //update the EditText bandArtist with the last saved search term
        bandArtist.setText(prefs.getString("search", ""));

        //initialize the list
        myAdapter = new MyListAdapter();

        //retrieve the ListView from activity_song.xml
        ListView myList = findViewById(R.id.theListView);

        myList.setAdapter( myAdapter);

        search.setOnClickListener(click ->
        {
            //will get the term searched and check if not null, if yes, will call a Toast
            if( bandArtist.getText().toString().equals("")) {
                Toast.makeText(this, getString(R.string.toast_EditText_null) , Toast.LENGTH_LONG).show();
            } else {
                elements.clear();
                myAdapter.notifyDataSetChanged();
                // will create a instance of class SongsSearch to be working with AsyncTask
                SongsSearch req = new SongsSearch();
                req.execute();  //Type 1
                myList.setAdapter( myAdapter );//populate the list
                saveSharedPrefs(bandArtist.getText().toString());
                Snackbar.make(click, getString(R.string.loadingTheList) , Snackbar.LENGTH_SHORT).show();
            }

        });

        //will retrieve the Button Favorites to open the songs saved on database
        Button buttonFav = findViewById(R.id.buttonFav);

        //listener intent for SongFav.java
        Intent favActivity = new Intent(SongActivity.this, SongFavorites.class);
        buttonFav.setOnClickListener(click1 -> startActivity(favActivity));

        //retrieve clear button to clear the list after searched if pushed
        Button clear = findViewById(R.id.buttonClear);
        clear.setOnClickListener(cb ->
        {
            // the AlertDialog used to popup a message to clear the information from listView
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Do you want clear the list?")

                        .setPositiveButton("Yes", (cbY, arg) -> {
                        elements.clear();
                        //SR.remove(pos);
                        TextView theListOfSongs = findViewById((R.id.theSongTitle));
                        //set the List of song visible
                        theListOfSongs.setVisibility(View.INVISIBLE);

                        Button clearButton = findViewById((R.id.buttonClear));
                        //set the button to clear the list visible
                        clearButton.setVisibility(View.INVISIBLE);
                        myAdapter.notifyDataSetChanged();

                    })
                    //What the No button does:
                    //.setNegativeButton("No", (cbN, arg) -> { })

                    //An optional third button:
                     .setNeutralButton("Cancel", (cbL, arg) -> {  })

                    //Show the dialog
                    .create().show();

           // return true;

        });

        //used to identify if is a tablet or phone screen
        boolean isTablet = findViewById(R.id.frameLayout) != null;//check if the FrameLayout is loaded

        //Click listener for ListView
        myList.setOnItemClickListener( (par, view, pos, id) -> {

        //Clicking on one of the songs in the list should show the artist id, song id, and title in the fragment

            Bundle dataToPass = new Bundle();

            dataToPass.putString(ITEM_ARTIST, elements.get(pos).getArtist() );
            dataToPass.putString(ITEM_TITLE, elements.get(pos).getTitle() );
            dataToPass.putString(ITEM_SONGID, elements.get(pos).getSongId() );
            dataToPass.putString(ITEM_ARITSTID, elements.get(pos).getArtistId());
            dataToPass.putString(REMOVEBUTTON, "N");
            dataToPass.putInt(ITEM_POSITION, pos);
            dataToPass.putLong(ITEM_ID, id);

            //will check if the device is a tablet or a phone and call the correct .xml
            if(isTablet) {
                dFragment = new SongDetailFragment(); // add a detailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(SongActivity.this, SongEmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }

        });

    }

    // the array list created to store the instance of class SongFound from retrieved songs
    private ArrayList <SongFound> elements = new ArrayList<>();

    @Override
    /**
     *  used to keep the menu with toolbar
     * @param menu
     * @retunr boolean
     *  */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.song_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.menuTrivia:
                Intent activityTrivia = new Intent(this, TriviaMain.class);
                startActivity(activityTrivia);
                message = getString(R.string.clickedTrivia);
                break;
            case R.id.menuCarDB:
                Intent activityCar = new Intent(this, CarActivity.class);
                startActivity(activityCar);
                message = getString(R.string.clickedCar);
                break;
            case R.id.menuSoccer:
                Intent activitySoccer = new Intent(this, SoccerActivity.class);
                startActivity(activitySoccer);
                message = getString(R.string.clickedSoccer);
                break;

            case R.id.menuSongster:
                Intent activitySong = new Intent(this, SongActivity.class);
                startActivity(activitySong);
                message = getString(R.string.clickedSong);
                break;

            case R.id.menuAbout:
                Intent activityAbout = new Intent(this, SongAbout.class);
                startActivity(activityAbout);
                message = getString(R.string.clickedAbout);
                break;

            case R.id.menuHomeIcon:
                Intent activityHome = new Intent(this, MainActivity.class);
                startActivity(activityHome);
                message = getString(R.string.clickedHome);
                break;

            case R.id.menuHelp:
                message = getString(R.string.clickedHelp);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage(R.string.helpInstructions)

                        .setPositiveButton("OK", (cbY, arg) -> {
                        })
                        .setNeutralButton("Cancel", (cbL, arg) -> {  })
                        .create().show();
                break;

        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return true;
    }

    // Needed for the OnNavigationItemSelected interface:
    public boolean onNavigationItemSelected( MenuItem item) {

        String message = null;

        switch(item.getItemId())
        {
            //what to do when the menu item is selected:

            case R.id.goToCar:
                Intent activityCar = new Intent(this, CarActivity.class);
                startActivity(activityCar);
                message = getString(R.string.clickedCar);
                break;
            case R.id.goToSoccer:
                Intent activitySoccer = new Intent(this, SoccerActivity.class);
                startActivity(activitySoccer);
                message = getString(R.string.clickedSoccer);
                break;
            case R.id.goToTrivia:
                Intent activityTrivia = new Intent(this, TriviaPlayActivity.class);
                startActivity(activityTrivia);
                message = getString(R.string.clickedTrivia);
                break;
            case R.id.goToSong:
                Intent activitySong = new Intent(this, SongActivity.class);
                startActivity(activitySong);
                message = getString(R.string.clickedSong);
                break;
        }
//
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        Toast.makeText(this, "NavigationDrawer: " + message, Toast.LENGTH_LONG).show();
        return false;
    }

    /**
     * this method will put the string searched into the file created
     * @param searchTerm
     *  */
    private void saveSharedPrefs(String searchTerm) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("search", searchTerm);
        editor.commit();
    }

    /**
     * ListAdapter is an Interface that you must implement by writing
     * these 4 public functions
     * this list adapter will be used to update the songs list view on activity_song.xml
     * */
    class MyListAdapter extends BaseAdapter{


        /**
         * returns the number of items
         * @return integer
         * */
        @Override
        public int getCount() {
            return elements.size();
        }

        /**
         * returns what to show at row position
         * @param position
         * @return Object
         * */
        @Override
        public Object getItem(int position) {
            return elements.get(position);
        }
        /**
         * returns the database id of the item at position i
         * @param position
         * @return long
         * */
        @Override
        public long getItemId(int position) {
            return (long) position;
        }

        /**
         * creates a View object to go in a row of the ListView
         * @param position
         * @param view
         * @param viewGroup
         * @return View
         * */
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.song_retrieved, viewGroup, false );

            //set what the text should be for this row:
            TextView tAOB = newView.findViewById(R.id.title);
            tAOB.setText(" " + elements.get(position).getTitle().toString());

            return newView;

        }
    }

    /**
     * this class extends the class AsyncTask with 3 generics parameters to
     * supply
     * */
    class SongsSearch extends AsyncTask<String, Integer, String> {

        EditText bandArtist = findViewById(R.id.textBandArtist);
        /**
         * this is where you do your work
         * @param strings
         * @return strings
         * */
        @Override
        protected String doInBackground(String... strings) {

            searchTerm = bandArtist.getText().toString().toLowerCase();

            // will get the searchTerm and replace the blank spaces with '+' */
            String replaceTerm = searchTerm.replace(' ', '+' );
            try
            {
                //create a URL object of what server to contact:
                URL url = new URL("https://www.songsterr.com/a/ra/songs.xml?pattern=" + replaceTerm);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();

                //From part 3: slide 19
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( response  , "UTF-8");


                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT
                //int insideInfo;

                while(eventType != XmlPullParser.END_DOCUMENT)
                {

                    if(eventType == XmlPullParser.START_TAG)
                    {
                        //If you get here, then you are pointing at a start tag. Will check if the tag is "Song"
                        if(xpp.getName().equals("Song")){
                            //will charge the information on progress bar
                            publishProgress(50);
                            // will save the parser if attribute is id on variable songId
                            songId = xpp.getAttributeValue ( null , "id");
                            do {
                                xpp.next();
                                // will save the parser on variable attrib
                                attrib = xpp.getName();
                            }while (!"title".equals(attrib) );
                            xpp.next();
                            // will save the parser on variable title
                            title = xpp.getText();
                            do {
                                xpp.next();
                                // will save the parser on variable attrib
                                attrib = xpp.getName();
                            }while (!"artist".equals(attrib) );
                            artistId = xpp.getAttributeValue(null, "id");
                            do {
                                xpp.next();
                                // will save the parser on variable attrib
                                attrib = xpp.getName();
                            }while (!"name".equals(attrib) );
                            xpp.next();
                            artist = xpp.getText();
                        }

                    }
                    if(title != null && songId != null && artistId != null && artist != null){
                        // create a new instance of class SongFound with the variables retrieved from the xml file */
                        obj = new SongFound(title, songId, artistId, artist);
                        elements.add(obj);
                        title = songId = artistId = artist = null;
                    }

                    eventType = xpp.next(); //move to the next xml event and store it in a variable
                }
                publishProgress(100);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return "Done";
        }

        /**
         * this is where update the gui
         * @param args
         */
        public void onProgressUpdate(Integer ... args)
        {
            //will turn the progress bar visible and load the bar
            ProgressBar someProgressBar = findViewById(R.id.progressBar);
            someProgressBar.setVisibility(View.VISIBLE);
            someProgressBar.setProgress(50);
            someProgressBar.setProgress(100);
        }

        /**
         * this means that doInBackground has finished. You can do your final GUI update
         * @param fromDoInBackground
         */
        public void onPostExecute(String fromDoInBackground)
        {

                    TextView theListOfSongs = findViewById((R.id.theSongTitle));
                    //set the List of song visible
                    theListOfSongs.setVisibility(View.VISIBLE);

                    Button clearButton = findViewById((R.id.buttonClear));
                    //set the button to clear the list visible
                    clearButton.setVisibility(View.VISIBLE);

                    ProgressBar someProgressBar = findViewById(R.id.progressBar);
                    //set the progress bar visible
                    someProgressBar.setVisibility(View.INVISIBLE);

                    //update the list view with the list of songs
                    myAdapter.notifyDataSetChanged();
        }
    }
}

