package com.cst2335.cst2335_finalproject.trivia;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

//Importing builder package
import com.cst2335.cst2335_finalproject.CarActivity;
import com.cst2335.cst2335_finalproject.MainActivity;
import com.cst2335.cst2335_finalproject.R;
import com.cst2335.cst2335_finalproject.SoccerActivity;
import com.cst2335.cst2335_finalproject.SongActivity;
import com.cst2335.cst2335_finalproject.TriviaMain;
import com.cst2335.cst2335_finalproject.trivia.apiObjects.TriviaCategory;
import com.cst2335.cst2335_finalproject.trivia.apiObjects.TriviaQuestion;
import com.cst2335.cst2335_finalproject.trivia.builder.URLBuilder;
import com.google.android.material.navigation.NavigationView;

public class TriviaActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private final static String ACTIVITY = "TriviaActivity";
    private ArrayList<TriviaCategory> categoryList = new ArrayList<>();
    private ArrayList<TriviaQuestion> questionList = new ArrayList<>();
    private ArrayList<String> categoryName = new ArrayList<>();
    private ArrayList<String> categoryID = new ArrayList<>();
    private URLBuilder urlBuilder = new URLBuilder();
    private int responseCode;
    private CategoryList categoryListURL;
    private LoadQuestions loadQuestions;
    private static final String CATEGORY_URL = "https://opentdb.com/api_category.php";
    private JSONObject questions;
    private ArrayList<String> incorrectAnswers;

    /**
     * This method creates the activity on app is opened
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);

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
        ProgressBar progressBar = findViewById(R.id.triviaWelcomeProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        //Loading all questions categories from API for the user to choose
        categoryListURL = new CategoryList();
        categoryListURL.execute(CATEGORY_URL);

        Log.i(ACTIVITY, "Current player is: " + getIntent().getStringExtra("player")); //Debug purpose only

        //Instantiating and initializing difficulty buttons
        ImageButton any = findViewById(R.id.any);
        ImageButton easy = findViewById(R.id.easy);
        ImageButton medium = findViewById(R.id.medium);
        ImageButton hard = findViewById(R.id.hard);

        //On click listener for any button
        any.setOnClickListener(click -> {
            urlBuilder.setDifficulty(0);
            Toast.makeText(this, "Any", Toast.LENGTH_SHORT).show();
            Log.i("ACTIVITY_TRIVIA: ",  "The URL is: " + urlBuilder.getURL());
            any.setBackgroundColor(Color.parseColor("#cad3fe"));
            easy.setBackgroundColor(0);
            medium.setBackgroundColor(0);
            hard.setBackgroundColor(0);
        });

        //On click listener for easy button
        easy.setOnClickListener(click -> {
            urlBuilder.setDifficulty(1);
            Toast.makeText(this, "Easy", Toast.LENGTH_SHORT).show();
            Log.i("ACTIVITY_TRIVIA: ",  "The URL is: " + urlBuilder.getURL());
            easy.setBackgroundColor(Color.parseColor("#cad3fe"));
            any.setBackgroundColor(0);
            medium.setBackgroundColor(0);
            hard.setBackgroundColor(0);
        });

        //On click listener for medium button
        medium.setOnClickListener(click -> {
            urlBuilder.setDifficulty(2);
            Toast.makeText(this, "Medium", Toast.LENGTH_SHORT).show();
            Log.i("ACTIVITY_TRIVIA: ",  "The URL is: " + urlBuilder.getURL());
            medium.setBackgroundColor(Color.parseColor("#cad3fe"));
            any.setBackgroundColor(0);
            easy.setBackgroundColor(0);
            hard.setBackgroundColor(0);
        });

        //On click listener for hard button
        hard.setOnClickListener(click -> {
            urlBuilder.setDifficulty(3);
            Toast.makeText(this, "Hard", Toast.LENGTH_SHORT).show();
            Log.i("ACTIVITY_TRIVIA: ",  "The URL is: " + urlBuilder.getURL());
            hard.setBackgroundColor(Color.parseColor("#cad3fe"));
            any.setBackgroundColor(0);
            easy.setBackgroundColor(0);
            medium.setBackgroundColor(0);
        });




        // ******Creating OnclickListener that leads to TriviaPlayActivity***************
        Button startTrivia =  findViewById(R.id.start);
        startTrivia.setOnClickListener( click -> {

            AppCompatEditText quantity = findViewById(R.id.enterQuantity);
            RadioGroup typeGroupChoice = findViewById(R.id.radioChoiceGroup);
            int typeID = typeGroupChoice.getCheckedRadioButtonId();

            //If user don't enter quantity of question, set the default quantity value to 10
            if(quantity.getText() == null || quantity.getText().toString().isEmpty()){
                quantity.setText("10");
                urlBuilder.setQuantity(10);
            }
            //If user set a quantity les than 0 or greater than 100, show toast message asking to enter a valid number
            else if(Integer.parseInt(quantity.getText().toString()) <= 0 || Integer.parseInt(quantity.getText().toString()) >= 100){
                Toast.makeText(this, "Please, enter a valid number of questions from 1 to 99", Toast.LENGTH_LONG).show();
                return;
            }
            //Otherwise, get the question value entered by the user and set the url bulder quantity
            else{
                urlBuilder.setQuantity(Integer.parseInt(String.valueOf(quantity.getText())));
                Log.i(ACTIVITY,  "The URL is: " + urlBuilder.getURL());
            }
            //Set the question type based on what the user selected the radio button. If user didn't selected no type, the construction will set as any type of questions
            urlBuilder.setType(typeID);

            //After build the url, load the questions from the api with the custom url
            loadQuestions = new LoadQuestions();
            loadQuestions.execute(urlBuilder.getURL());
        });
        // ******End of OnclickListener that leads to TriviaPlayActivity***************

    }//End of onCreate method


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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(position == 0){
            urlBuilder.setCategory(0);
        }else{
            //Show a Toast message whenever a category is selected
            Toast.makeText(this, categoryName.get(position), Toast.LENGTH_SHORT).show();
            //Set category id to the urlBuilder
            urlBuilder.setCategory(Integer.parseInt(categoryID.get(position)));
        }
        Log.i(ACTIVITY,  "The URL is: " + urlBuilder.getURL());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    /**
     * This class extends AsyncTask and is used to retrieve data from the API http server.
     */
    @SuppressLint("StaticFieldLeak")
    private class CategoryList extends AsyncTask<String, Integer, String> {

        /**
         * This method runs the data retrieve all the questions categories from the API in the background processor
         * @param args
         * @return
         */
        @Override
        protected String doInBackground(String... args) {

            try {
                //create a URL object of what server to contact:
                URL url = new URL(args[0]);
                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();

                //Build the entire string response:
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8), 8);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line)
                            .append("\n");
                }
                String result = sb.toString(); //result is the whole string
                Log.i("JSON", result);//Debug###########

                // convert string to JSON
                JSONObject categories = new JSONObject(result);
                JSONArray jsonArray = categories.getJSONArray("trivia_categories");
                Log.i("JSON", categories.toString());

                //Adding default category to the list if user don't choose one
                categoryName.add("Any category");
                categoryID.add("0");
                int progress = 100/jsonArray.length();

                for(int i=0; i < jsonArray.length(); i++) {

                    String id = "";
                    String name = "";
                    categories = jsonArray.getJSONObject(i);

                    for(int j=0; j < 1; j++){
                        id = String.valueOf(categories.getInt("id"));
                        name = categories.getString("name");
                    }
//                    categoryList.add(new TriviaCategory(id, name));
                    categoryName.add(name);
                    categoryID.add(id);
                    //Saving category id and name as array object of TriviaCategory
                    categoryList.add(new TriviaCategory(id, name));
                    Log.i(ACTIVITY, "Progress: " + progress*(i+1));
                    publishProgress(progress*(i+1));
                }
                publishProgress(100);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return "Done";
        }

        /**
         * This method updates the progress of data that is being retrieved
         * @param args
         */
        public void onProgressUpdate(Integer ... args) {
            ProgressBar progressBar = findViewById(R.id.triviaWelcomeProgressBar);
            progressBar.setVisibility(View.VISIBLE);
            Log.i("HTTP", "In onProgressUpdate"); //added this line here just to debug
        }


        /**
         * After data is retrieved, this method populates the spinner with all questions categories collected from the API
         * @param fromDoInBackground
         */
        //Type3 from doInBackground is the return from doInBackground
        public void onPostExecute(String fromDoInBackground){ //The return value "Done"

            Spinner spinner = findViewById(R.id.categorySpinner);
            spinner.setOnItemSelectedListener(TriviaActivity.this);

            ArrayAdapter aa = new ArrayAdapter(TriviaActivity.this, android.R.layout.simple_spinner_item, categoryName);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(aa);

            ProgressBar progressBar = findViewById(R.id.triviaWelcomeProgressBar);
            progressBar.setVisibility(View.INVISIBLE);


        }

    }
    /**
     * This method runs the data retrieve all questions from the API in the background processor
     * @return
     */
    @SuppressLint("StaticFieldLeak")
    public class LoadQuestions extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... args) {

            try {
                //create a URL object of what server to contact:
                URL url = new URL(args[0]);
                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();

                //Build the entire string response:
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8), 8);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line)
                            .append("\n");
                }
                String result = sb.toString(); //result is the whole string
                Log.i("JSON", result);//Debug###########

                // convert string to JSON
                questions = new JSONObject(result);
                //Saving API response code
                responseCode = questions.getInt("response_code");

                Log.i("LoadQuestions", String.valueOf(responseCode)); //For debug only


            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return "Done";
        }


        public void onProgressUpdate(Integer ... args) {

            Log.i("ResponseCode", "In onProgressUpdate " + responseCode); //added this line here just to debug
        }

        /**
         * This method collects all questions information bases on the users choice and saves in a questionList array.
         * @param fromDoInBackground
         */
        public void onPostExecute(String fromDoInBackground){ //The return value "Done"

            Intent goTriviaPlay = new Intent(TriviaActivity.this, TriviaPlayActivity.class);

            if(responseCode == 0){
                Log.i(ACTIVITY,  "IF - Response code is: " + responseCode);//added this line here just to debug

                try {
                    JSONArray questionArray = questions.getJSONArray("results");
                    JSONArray incorrectAnswerArray;

                    for(int i=0; i < questionArray.length(); i++) {

                        String category = "";
                        String type = "";
                        String difficulty = "";
                        String question = "";
                        String correctAnswer = "";
                        questions = questionArray.getJSONObject(i);

                        for(int j=0; j < 1; j++){
                            category = questions.getString("category");
                            type = questions.getString("type");
                            difficulty = questions.getString("difficulty");
                            question = questions.getString("question");

                            //Replacing the weird strings &quot; and &#039; to ' from every question to improve readability
                            question = question.replace("&quot;", "'").replace("&#039;", "'").replace("&eacute;", "é");

                            correctAnswer = questions.getString("correct_answer");
                            //Replacing the weird strings &quot; and &#039; to ' from every correct answer to improve readability
                            correctAnswer = correctAnswer.replace("&quot;", "'").replace("&#039;", "'").replace("&eacute;", "é");

                            incorrectAnswerArray = questions.getJSONArray("incorrect_answers");
                            incorrectAnswers = new ArrayList<>();

                            String incorrectAnswer = "";
                            for(int k=0; k < incorrectAnswerArray.length(); k++){
                                incorrectAnswer = incorrectAnswerArray.getString(k);
                                //Replacing the weird strings &quot; and &#039; to ' from every incorrect answer to improve readability
                                incorrectAnswer = incorrectAnswer.replace("&quot;", "'").replace("&#039;", "'");
                                incorrectAnswers.add(incorrectAnswer);
                            }
                        }
                        //Saving questions as array object of TriviaQuestion
                        questionList.add(new TriviaQuestion(category, type, difficulty, question, correctAnswer, incorrectAnswers));
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    //Sends the array with all questions to the other activity to be loaded for the user
                    goTriviaPlay.putExtra("AllQuestions", questionList);
                    //Sending the player name from previous activity to the next activity
                    goTriviaPlay.putExtra("player", getIntent().getStringExtra("player"));
                    startActivity(goTriviaPlay);
                }



            }
            //If user chooses some specific preferences and the API returns a response code that is not 0, an alert dialog will be shown informing the users
            //that it was not possible to find questions with that setting.
            else{
                Log.i(ACTIVITY,  "ELSE - Response code is: " + responseCode);//added this line here just to debug
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TriviaActivity.this);
                alertDialogBuilder.setTitle("Trivia questions not found!")
                        .setMessage("Sorry, we could not find any questions with your chosen configurations."
                                + "Would you like to change preferences or exit the game?")
                        .setPositiveButton("CHANGE", (click, arg) ->  Log.i("DIALOG TRY AGAIN",  "The URL is: " + urlBuilder.getURL()))
                        .setNegativeButton("EXIT", (click, arg) -> {
                            Intent exitTrivia = new Intent(TriviaActivity.this, MainActivity.class);
                            startActivity(exitTrivia);
                        })
                        .create().show();
            }
            Log.i("ResponseCode", "In onPostExecute " + responseCode); //added this line here just to debug
        }
    }//End of LoadQuestions AsyncTask class

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
                AlertDialog.Builder helpDialog = new AlertDialog.Builder(TriviaActivity.this);
                helpDialog.setTitle("How to setup you preferences?")
                        .setMessage("1) Choose the quantity of questions" + "\n"
                                + "2) Choose the difficulty level" + "\n"
                                + "3) Select the category" + "\n"
                                + "4) Choose the type of questions" + "\n"
                                + "5) Click start game" + "\n"
                                + "IF YOU JUST WANT TO PLAY RIGHT AWAY, JUST CLICK START GAME AND WE WILL CHOOSE THE PREFERENCES FOR YOU")
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

}//End of TriviaActivity class