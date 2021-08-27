package com.cst2335.cst2335_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.cst2335.cst2335_finalproject.trivia.TriviaActivity;
import com.cst2335.cst2335_finalproject.trivia.TriviaPlayActivity;
import com.cst2335.cst2335_finalproject.trivia.database.TriviaDatabase;
import com.cst2335.cst2335_finalproject.trivia.player.TriviaPlayer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class TriviaMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener{

    public static final String ACTIVITY_NAME = "TRIVIA_MAIN";
    SharedPreferences prefs = null;
    SQLiteDatabase db;
    MyListAdapter myAdapter;
    private ArrayList<TriviaPlayer> playerList = new ArrayList<>();

    /**
     * This method creates the activity on app is opened
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_main);

        //Added toolbar and navigation drawer
        Toolbar tBar = (Toolbar)findViewById(R.id.triviaToolbar);
        tBar.setBackgroundColor(getColor(R.color.purple_500));
        tBar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(tBar);
        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, tBar, R.string.triviaDrawerOpen, R.string.triviaDrawerClose);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //This line prevents showing the keyboard automatically for the edit text when activity starts
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Loading nickname from shared preferences if exists
        prefs = getSharedPreferences("TriviaPrefs", Context.MODE_PRIVATE);
        AppCompatEditText nickName = findViewById(R.id.nickName);
        nickName.setText(prefs.getString("nickName", ""));

        //This set the list adapter to the list views and load the data from database
        myAdapter = new MyListAdapter();
        ListView topScoreListView = findViewById(R.id.triviaListView);
        topScoreListView.setAdapter(myAdapter);
        loadDataFromDatabase();

        topScoreListView.setOnItemLongClickListener((parent, view, position, id) -> {
            //Create alert dialog to delete rows (messages) from the chat
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Do you want to delete this player?")
                    .setMessage("Name: " + playerList.get(position).getName() + "\n"
                            + "Score: " + playerList.get(position).getScore() + "\n"
                            + "ID: " + id)
                    .setPositiveButton("Yes", (click, arg) -> {
                        TriviaPlayer selectedPlayer = playerList.get(position);
                        //Deletes message from the database
                        deletePlayer(selectedPlayer);
                        //Deletes message from the ArrayList
                        playerList.remove(position);
                        //Notify de adapter that change was made
                        myAdapter.notifyDataSetChanged();

//                        if(isTablet == true){
//                            getSupportFragmentManager()
//                                    .beginTransaction()
//                                    .remove(dFragment) //Add the fragment in FrameLayout
//                                    .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
//                        }

                    })
                    .setNegativeButton("Cancel", (click, arg) -> {  })
                    .create().show();
            return true;
        });

        // ******Creating OnclickListener that leads to TriviaActivity***************
        Button trivia =  findViewById(R.id.welcomeStartButton);
        Intent goTrivia = new Intent(this, TriviaActivity.class);
        trivia.setOnClickListener( click -> {
            if(nickName.getText().toString().equals("") == false){
                //Saving input from user to shared preferences and loading next trivia activity
                saveSharedPrefs(nickName.getText().toString());
                //Send player name to next activity
                goTrivia.putExtra("player", nickName.getText().toString());

                startActivity(goTrivia);
            }
            else{
                //If user do not enter a nickname, show a Toast message asking to do it
                Toast.makeText(this, "Please, enter your nickname!", Toast.LENGTH_SHORT).show();
            }
        });
        // ******End of OnclickListener that leads to TriviaActivity***************
    }

    /** This method loads all messages stored in the database */
    private void loadDataFromDatabase()
    {
        //get a database connection:
        TriviaDatabase dbOpener = new TriviaDatabase(this);
        db = dbOpener.getWritableDatabase(); //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer


        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String [] columns = {TriviaDatabase.COL_ID, TriviaDatabase.COL_NAME, TriviaDatabase.COL_SCORE};
        //query all the results from the database:
        Cursor results = db.query(false, TriviaDatabase.TABLE_NAME, columns, null, null, null, null, TriviaDatabase.COL_SCORE + " desc", "5");

        //Call printCursor to show important information from the database to the logcat
        printCursor(results, db.getVersion());

        //Now the results object has rows of results that match the query.
        //find the column indices:
        int idColIndex = results.getColumnIndex(TriviaDatabase.COL_ID);
        int nameColumnIndex = results.getColumnIndex(TriviaDatabase.COL_NAME);
        int scoreColIndex = results.getColumnIndex(TriviaDatabase.COL_SCORE);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            String name = results.getString(nameColumnIndex);
            int score = results.getInt(scoreColIndex);
            long id = results.getLong(idColIndex);

            //add the new Player to the array list:
            playerList.add(new TriviaPlayer(name, score, id));
        }

//        playerList.add(new TriviaPlayer("Jia", 100, 1));
//        playerList.add(new TriviaPlayer("Janio", 98, 2));
//        playerList.add(new TriviaPlayer("Danny", 80, 3));
//        playerList.add(new TriviaPlayer("Marcelo", 75, 3));
        myAdapter.notifyDataSetChanged();
        //At this point, the playerList array has loaded every row from the cursor.
    }


    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    // Needed for the OnNavigationItemSelected interface:
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {


        switch(item.getItemId())
        {
            case R.id.triviaDrawerMenuCar:
                Intent goToCar = new Intent(this, CarActivity.class);
                startActivity(goToCar);
                break;
            case R.id.triviaDrawerMenuSoccer:
                Intent goToSoccer = new Intent(this, SoccerActivity.class);
                startActivity(goToSoccer);
                break;
            case R.id.triviaDrawerMenuSong:
                Intent goToSong = new Intent(this, SongActivity.class);
                startActivity(goToSong);
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);


        return false;
    }

    /** This method removes a row from the database based on the column id selected from the list in the app */
    protected void deletePlayer(TriviaPlayer player)
    {
        db.delete(TriviaDatabase.TABLE_NAME, TriviaDatabase.COL_ID + "= ?", new String[] {Long.toString(player.getID())});
    }

    /** This method prints log information in the logcat about Cursor and database. This is for debug purpose */
    private void printCursor( Cursor c, int version){
        Cursor cursor = c;
        int dbVersion = version;
        //Print log info messages in the logcat
        Log.i(ACTIVITY_NAME, "Database version: " + dbVersion);
        Log.i(ACTIVITY_NAME, "Number of columns in the cursor: " + cursor.getColumnCount());
        /** For loop to print each column name log in the logcat */
        for(int i = 0; i < 3; i++ ) {
            Log.i(ACTIVITY_NAME, "Name of column " + i + " is: " + cursor.getColumnName(i));
        }
        Log.i(ACTIVITY_NAME, "Number of rows in the cursor: " + cursor.getCount());

    }

    /**
     * This class MyListAdapter extends BaseAdapter that is used to read elements from the array playerList
     * and inflates the trivia_top_players_listview layout with each element from the array.
     */
    class MyListAdapter extends BaseAdapter {

        /** This method @return the array size */
        @Override
        public int getCount() {
            return playerList.size();
        }

        /** This method @return the player name */
        @Override
        public String getItem(int position) {
            return playerList.get(position).getName();
        }

        /** This method @return the player id */
        @Override
        public long getItemId(int position) {
            return playerList.get(position).getID();
        }

        /**
         * This method creates the view with the elements of the array and @return the view
         * @param position
         * @param view
         * @param parent
         */
        @Override
        public View getView(int position, View view, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View topPlayerView = inflater.inflate(R.layout.trivia_top_players_listview, parent, false);

            TextView triviaTopPlayerName = topPlayerView.findViewById(R.id.triviaTopPlayerName);
            triviaTopPlayerName.setText(playerList.get(position).getName());
            TextView triviaTopPlayerScore = topPlayerView.findViewById(R.id.triviaTopPlayerScore);
            triviaTopPlayerScore.setText(String.valueOf(playerList.get(position).getScore()));

            return topPlayerView;
        }
    }

    /**
     * This method loads the toolbar @param menu and @return to the activity */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * This method executes actions when each item of menu is clicked
     * @param item
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menuHomeIcon:

                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                // ******Creating intent that leads to TriviaActivity***************
                Intent home = new Intent(this, MainActivity.class);
                this.startActivity(home);
                // ******End of intent that leads to TriviaActivity***************
                return true;

            case R.id.menuTrivia:

                Toast.makeText(this, "Trivia App", Toast.LENGTH_SHORT).show();
                // ******Creating intent that leads to TriviaActivity***************
                Intent trivia = new Intent(this, TriviaMain.class);
                this.startActivity(trivia);
                // ******End of intent that leads to TriviaActivity***************

                return true;
            case R.id.menuSongster:

                Toast.makeText(this, "Songster App", Toast.LENGTH_SHORT).show();
//                // ******Creating intent that leads to SongsterActivity***************
//                Intent songster = new Intent(this, SongsterActivity.class);
//                this.startActivity(songster);
//                // ******End of intent that leads to SongsterActivity***************

                return true;
            case R.id.menuCarDB:

                Toast.makeText(this, "CarDB App", Toast.LENGTH_SHORT).show();
//                // ******Creating intent that leads to CarDBActivity***************
//                Intent carDB = new Intent(this, CarDBActivity.class);
//                this.startActivity(carDB);
//                // ******End of intent that leads to CarDBActivity***************

                return true;
            case R.id.menuSoccer:

                Toast.makeText(this, "Soccer App", Toast.LENGTH_SHORT).show();
                // ******Creating intent that leads to SoccerActivity***************
                Intent soccer = new Intent(this, SoccerActivity.class);
                this.startActivity(soccer);
                // ******End of intent that leads to SoccerActivity***************

                return true;
            case R.id.menuHelp:
                AlertDialog.Builder helpDialog = new AlertDialog.Builder(TriviaMain.this);
                helpDialog.setTitle("How to play trivia?")
                        .setMessage("1) Enter your name" + "\n"
                                + "2) Click next" + "\n"
                                + "3) On the next screen you can find more help if you need" + "\n"
                                + "BELOW YOU CAN SEE THE TOP FIVE PLAYERS RANKED BY SCORE, GO GET THEM!")
                        .setPositiveButton("OK", (positiveClick, arg) ->  {
                            //Do nothing
                        })
                        .create().show();
                Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuAbout:
                Toast.makeText(this, "About App", Toast.LENGTH_SHORT).show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }//End of onOptionsItemSelected method


    /**
     * This method saves the nickname to the devices shared preference so it could be used so it remembers
     * when the user opens the app again next time
     * @param nickName
     */
    private void saveSharedPrefs(String nickName) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nickName", nickName);
        editor.commit();
    }
}