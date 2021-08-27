package com.cst2335.cst2335_finalproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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


import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


public class CarActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_NAME = "NAME";
    public static final String ITEM_MAKE = "MAKE";
    public static final String ITEM_MODEL = "MODEL";
    public static final String ITEM_SAVED = "IS_SAVED";
    public static final String ITEM_ID = "ID";
    public static final String ACTIVITY_NAME = "CarActivity";
    SharedPreferences prefsCar = null;
    private ArrayList<CarInfo> carInfos = new ArrayList<>();
    MyListAdapter carAdapter;
    CarInfo selectedCar;
    private ArrayList<CarInfo> carsSaved = new ArrayList<>();
    SavedListAdapter savedAdapter;
    SQLiteDatabase db;

    /**
     * @param savedInstanceState data saved
     * @param isTablet tablet or not
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        CarDBOpener dbOpener = new CarDBOpener(this);
        db = dbOpener.getWritableDatabase();
        //isTablet or not
        boolean isTablet = findViewById(R.id.fragmentLocation) != null; //Fr Lab7 example
        View parentLayout = findViewById(android.R.id.content);
        String carActivityText = getResources().getString(R.string.carActivityText);
        String closeButtonText = getResources().getString(R.string.closeButtonText);
        Snackbar.make(parentLayout, carActivityText, Snackbar.LENGTH_LONG)
                .setAction(closeButtonText, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();

        //This gets the toolbar from the layout:
        Toolbar tBar = (Toolbar)findViewById(R.id.toolbar);

        //This loads the toolbar, which calls onCreateOptionsMenu below:
        setSupportActionBar(tBar);
        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer,tBar , R.string.openButtonText, R.string.closeButtonText);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //initalized listview
        carAdapter = new MyListAdapter();
        ListView carList = findViewById(R.id.carListView);
        carList.setAdapter(carAdapter);

        savedAdapter = new SavedListAdapter();
        ListView savedList = findViewById(R.id.savedListView);
        savedList.setAdapter(savedAdapter);

        //Loading nickname from shared preferences if exists
        prefsCar = getSharedPreferences("CarPrefs", Context.MODE_PRIVATE);
        EditText carMake = findViewById(R.id.carMake);
        carMake.setText(prefsCar.getString("Last search manufacturer", "Manufacturer"));

        //Progress bar
        ProgressBar bar =  findViewById(R.id.carBar);
        bar.setVisibility(View.INVISIBLE);



        //Declare search button and give it function to build and execute the url
        Button carSearch = findViewById(R.id.carSearchButton);
        String carSearchingText = getResources().getString(R.string.carSearchingText);
        carSearch.setOnClickListener( click ->
        {
            //AsynTask for connecting to car database
            CarQuery req = new CarQuery();
            Toast.makeText(this, carSearchingText, Toast.LENGTH_LONG).show();
            //StringBuilder to build url
            StringBuilder sbCarDB = new StringBuilder("https://vpic.nhtsa.dot.gov/api/vehicles/GetModelsForMake/");
            sbCarDB.append(carMake.getText());
            sbCarDB.append("?format=XML");
            req.execute(sbCarDB.toString());

        });

        //give list view click functions
        carList.setOnItemClickListener( (list, view, pos, id) -> {
            selectedCar = carInfos.get(pos);

            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_NAME,selectedCar.getName() );
            dataToPass.putString(ITEM_MAKE,selectedCar.getMake() );
            dataToPass.putString(ITEM_MODEL,selectedCar.getModelID() );
            dataToPass.putBoolean(ITEM_SAVED,false );
            dataToPass.putInt(ITEM_POSITION, pos);

            if(isTablet)
            {
                CarDetailFrag dFragment = new CarDetailFrag(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(CarActivity.this, CarEmpty.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }

        });
        //give list view click functions
        savedList.setOnItemClickListener( (list, view, pos, id) -> {
            selectedCar = carsSaved.get(pos);

            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_NAME,selectedCar.getName() );
            dataToPass.putString(ITEM_MAKE,selectedCar.getMake() );
            dataToPass.putString(ITEM_MODEL,selectedCar.getModelID() );
            dataToPass.putLong(ITEM_ID,selectedCar.getID() );
            dataToPass.putBoolean(ITEM_SAVED,true);
            dataToPass.putInt(ITEM_POSITION, pos);

            if(isTablet)
            {
                CarDetailFrag dFragment = new CarDetailFrag(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(CarActivity.this, CarEmpty.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }

        });

        Button carHelp = findViewById(R.id.carHelp);
        String helpText = getResources().getString(R.string.carHelpDeet1) + "\n"+ "\n" + getResources().getString(R.string.carHelpDeet2);

        carHelp.setOnClickListener( click ->
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("")

                    //What is the message:
                    .setMessage(helpText)

                    //What the No button does:
                    .setNegativeButton("Done", (click2, arg) -> { })

                    //Show the dialog
                    .create().show();
        });
        //show database
        Button carViewDB = findViewById(R.id.carViewDB);
        carViewDB.setOnClickListener( click ->
        {
            carsSaved.clear();
            loadDataFromDatabase();
            savedAdapter.notifyDataSetChanged();
        });

    }

    /**
     * AsyntTask to get info from url
     * @param modelID model ID
     * @param modelName model Name
     * @param make Make of the model
     * @param total Use to calc progress bar progression
     * @param count Use to calc progress bar progression
     */

    //nested class for the AsyncTask
    private class CarQuery extends AsyncTask<String,Integer,String> {
        String modelID;
        String modelName;
        String make;
        int total;
        int count=0;


        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected String doInBackground(String... args) {
            try{
                URL url = new URL(args[0]);
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
                while(eventType != XmlPullParser.END_DOCUMENT)
                {

                    if(eventType == XmlPullParser.START_TAG )
                    {
                        //If you get here, then you are pointing at a start tag
                        if(xpp.getName().equals("Count"))
                        {
                            xpp.next();
                            total = Integer.parseInt(xpp.getText());
                        }else if(xpp.getName().equals("Make_Name")){
                            xpp.next();
                            make = xpp.getText();
                            count++;
                            carInfos.add(new CarInfo(""));
                            carInfos.get(count-1).setMake(make);
                            publishProgress((count/total)*100);

                        } else if(xpp.getName().equals("Model_ID"))
                        {
                            xpp.next();
                            modelID = xpp.getText();
                            carInfos.get(count-1).setModelID(modelID);
                            publishProgress((count/total)*100);


                        }else if(xpp.getName().equals("Model_Name")){
                            xpp.next();
                            modelName = xpp.getText();
                            carInfos.get(count-1).setName(modelName);
                            publishProgress((count/total)*100);
                        }

                    }
                    eventType = xpp.next(); //move to the next xml event and store it in a variable

                }
            } catch(Exception e){}

            return "Done";
        }
        public void onProgressUpdate(Integer ... args)
        {
            ProgressBar bar =  findViewById(R.id.carBar);
            bar.setVisibility(View.VISIBLE);
            bar.setProgress(args[0]);
        }
        public void onPostExecute(String fromDoInBackground)
        {
            //  ProgressBar bar =  findViewById(R.id.carBar);
            // bar.setVisibility(View.INVISIBLE);
            carAdapter.notifyDataSetChanged();
        }
    }

    /**
     * @param modelID Model ID
     * @param modelName Model Name
     * @param make Make of the name
     * @param id ID for the database
     *
     */
    //nested class for the carInfo object for the list view
    private class CarInfo{
        private String modelID;
        private String modelName;
        private String make;
        private long id;

        //constructor for CarInfo
        public CarInfo(String modelID){
            this.modelID = modelID;
        }
        public CarInfo(String modelID, String modelName, String make, long id){
            this.modelID = modelID;
            this.modelName = modelName;
            this.make = make;
            this.id = id;
        }

        public void setName(String name){ this.modelName = name;}
        public void setMake(String make){ this.make = make;}
        public void setModelID(String modelID){ this.modelID = modelID;}

        public long getID(){ return id;}
        public String getName(){ return modelName;}
        public String getModelID(){ return modelID;}
        public String getMake(){ return make;}
    }


    //nested class for view adapter
    class MyListAdapter extends BaseAdapter {
        @Override
        public int getCount() { return carInfos.size();}

        @Override
        public CarInfo getItem(int position) {
            return carInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            View listview = inflater.inflate(R.layout.cardb_listview, null);
            TextView carModel = listview.findViewById(R.id.carListModelID);
            carModel.setText(carInfos.get(position).getModelID());
            return listview;
        }
    }
    //nested class for view adapter
    class SavedListAdapter extends BaseAdapter {
        @Override
        public int getCount() { return carsSaved.size();}

        @Override
        public CarInfo getItem(int position) {
            return carsSaved.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            View listview = inflater.inflate(R.layout.cardb_listview, null);
            TextView carModel = listview.findViewById(R.id.carListModelID);
            carModel.setText(carsSaved.get(position).getModelID());
            return listview;
        }
    }

    /**
     * loadDataFromDatabase() from Lab 5, loads the database to view's list
     */
    private void loadDataFromDatabase()
    {
        //get a database connection:
        //CarDBOpener dbOpener = new CarDBOpener(this);
        // db = dbOpener.getWritableDatabase(); //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer


        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String [] columns = {CarDBOpener.COL_ID, CarDBOpener.COL_NAME, CarDBOpener.COL_MAKE, CarDBOpener.COL_MDL};
        //query all the results from the database:
        Cursor results = db.query(false, CarDBOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        //Now the results object has rows of results that match the query.
        //find the column indices:
        int nameColIndex = results.getColumnIndex(CarDBOpener.COL_NAME);
        int modelColIndex = results.getColumnIndex(CarDBOpener.COL_MDL);
        int idColIndex = results.getColumnIndex(CarDBOpener.COL_ID);
        int makeColIndex = results.getColumnIndex(CarDBOpener.COL_MAKE);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            String name = results.getString(nameColIndex);
            String make = results.getString(makeColIndex);
            String model = results.getString(modelColIndex);
            long id = results.getLong(idColIndex);

            carsSaved.add(new CarInfo(model,name,make,id));
        }

        printCursor(results,db.getVersion());
    }

    /**
     * Cursor method to interate through db, from Lab 5
     * @param c Cursor
     * @param ver version
     */
    private void printCursor(Cursor c, int ver){

        Log.i (null, "Number of columns: " + c.getColumnCount() );
        Log.i (null, "Name of columns: " + Arrays.toString(c.getColumnNames()));
        Log.i (null,"number of rows: " + c.getCount());

        StringBuilder tableBuilder = new StringBuilder("Current data table:\n");

        if (c.moveToFirst() ){
            String[] columnNames = c.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableBuilder.append(String.format("%s: %s   ", name,
                            c.getString(c.getColumnIndex(name))));
                }
                tableBuilder.append("\n");

            } while (c.moveToNext());
        }

        Log.i(null, tableBuilder.toString());
    }
    /**
     * This method saves to sharedPreferences whenever the app is closed or put to background */
    @Override
    protected void onPause() {
        super.onPause();
        Log.e(ACTIVITY_NAME, "In function:" + "onPause()");
        EditText carMake = findViewById(R.id.carMake);
        saveSharedPrefs(carMake.getText().toString());
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
     * This method saves the last searched manufactruer to the devices shared preference so it could be used so it remembers
     * when the user opens the app again next time
     * @param carPref
     */
    private void saveSharedPrefs(String carPref) {
        SharedPreferences.Editor editor = prefsCar.edit();
        editor.putString("Last search manufacturer", carPref);
        editor.commit();

    }

    /**
     * This method executes actions when each item of menu is clicked
     * @param item
     * @return true
     */

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
                Intent songster = new Intent(this, SongActivity.class);
               this.startActivity(songster);
//                // ******End of intent that leads to SongsterActivity***************

                return true;

            case R.id.menuSoccer:

                Toast.makeText(this, "Soccer App", Toast.LENGTH_SHORT).show();
//                // ******Creating intent that leads to SoccerActivity***************
                Intent soccer = new Intent(this, SoccerActivity.class);
                this.startActivity(soccer);
//                // ******End of intent that leads to SoccerActivity***************

                return true;
            case R.id.menuHelp:
                String help = getResources().getString(R.string.carHelpText);
                String help1 = getResources().getString(R.string.carHelpDeet1);
                String help2 = getResources().getString(R.string.carHelpDeet2);
                AlertDialog.Builder helpDialog = new AlertDialog.Builder(CarActivity.this);
                helpDialog.setTitle(help)
                        .setMessage( help1+ "\n" + "\n"
                                + help2+ "\n" + "\n")
                        .setPositiveButton("OK", (positiveClick, arg) ->  {
                            //Do nothing
                        })
                        .create().show();
                Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuAbout:
                String about = getResources().getString(R.string.carTitle);
                String about1 = getResources().getString(R.string.carAuthor);
                String about2 = getResources().getString(R.string.carVersion);
                AlertDialog.Builder aboutDialog = new AlertDialog.Builder(CarActivity.this);
                aboutDialog.setTitle("")
                        .setMessage(about + "\n" + "\n"
                                + about1+ "\n" + "\n"
                                + about2+ "\n" + "\n")
                        .setPositiveButton("OK", (positiveClick, arg) ->  {
                            //Do nothing
                        })
                        .create().show();
                Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }//End of onOptionsItemSelected method

    @Override
    public boolean onNavigationItemSelected( MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.carDrawerHomeIcon:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                // ******Creating intent that leads to TriviaActivity***************
                Intent home = new Intent(this, MainActivity.class);
                startActivity(home);
                // ******End of intent that leads to TriviaActivity***************
                break;

            case R.id.carDrawerTrivia:
                Toast.makeText(this, "Trivia App", Toast.LENGTH_SHORT).show();
                // ******Creating intent that leads to TriviaActivity***************
                Intent trivia = new Intent(this, TriviaMain.class);
                startActivity(trivia);
                // ******End of intent that leads to TriviaActivity***************
                break;
            case R.id.carDrawerSong:
                Toast.makeText(this, "Songster App", Toast.LENGTH_SHORT).show();
                // ******Creating intent that leads to SongsterActivity***************
                Intent songster = new Intent(this, SongActivity.class);
                this.startActivity(songster);
//                // ******End of intent that leads to SongsterActivity***************
                break;
            case R.id.carDrawerSoccer:
                Toast.makeText(this, "Soccer App", Toast.LENGTH_SHORT).show();
//                // ******Creating intent that leads to SoccerActivity***************
                Intent soccer = new Intent(this, SoccerActivity.class);
                this.startActivity(soccer);
//                // ******End of intent that leads to SoccerActivity***************
                break;

        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}