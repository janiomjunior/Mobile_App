package com.cst2335.cst2335_finalproject.trivia;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cst2335.cst2335_finalproject.CarActivity;
import com.cst2335.cst2335_finalproject.MainActivity;
import com.cst2335.cst2335_finalproject.R;
import com.cst2335.cst2335_finalproject.SoccerActivity;
import com.cst2335.cst2335_finalproject.SongActivity;
import com.cst2335.cst2335_finalproject.TriviaMain;
import com.cst2335.cst2335_finalproject.trivia.apiObjects.TriviaQuestion;
import com.cst2335.cst2335_finalproject.trivia.database.TriviaDatabase;
import com.cst2335.cst2335_finalproject.trivia.player.TriviaPlayer;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class TriviaPlayActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ArrayList<TriviaQuestion> questionList = new ArrayList<>();
    private static final String ACTIVITY = "TriviaPlayActivity";
    private static final int firstQuestion = 0;
    private int lastQuestion = 0;
    private int nextQuestion;
    private int previousQuestion;
    private int currentQuestion = 0;
    private int correctQuantity = 0;
    private int incorrectQuantity = 0;
    private boolean finalAnswer = false;
    private String buttonClicked = "";
    public static final String CORRECT_ANSWER = "CORRECT";
    public static final String INCORRECT_ANSWER1 = "INCORRECT1";
    public static final String INCORRECT_ANSWER2 = "INCORRECT2";
    public static final String INCORRECT_ANSWER3 = "INCORRECT3";
    private TriviaPlayer player;
    /**
     * This method creates the activity on app is opened
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_play);

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

        questionList = (ArrayList<TriviaQuestion>)getIntent().getSerializableExtra("AllQuestions");
        lastQuestion = questionList.size() - 1;
        //Load questions starting from the first element
        loadQuestion(firstQuestion);
        //Load button settings with the default values. No button clicked yet
        loadButtonsSettings(false, "none");

        //Initializing a mew player object with the player name form the first activity
        player = new TriviaPlayer(getIntent().getStringExtra("player"));
        Log.i(ACTIVITY, "Current player is: " + getIntent().getStringExtra("player"));//Log for debug purpose

        //Create the progress bar view and set the default color to purple
        ProgressBar progressBarQuestions = findViewById(R.id.triviaProgressQuestions);
        progressBarQuestions.setMax(questionList.size());
        progressBarQuestions.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#673AB7")));

        //Declaring answers buttons to configure functionality
        Button correctAnswer = findViewById(R.id.triviaAnswer1);
        Button incorrectAnswer1 = findViewById(R.id.triviaAnswer2);
        Button incorrectAnswer2 = findViewById(R.id.triviaAnswer3);
        Button incorrectAnswer3 = findViewById(R.id.triviaAnswer4);
        Button submit = findViewById(R.id.triviaSubmitQuestion);

        //Every time the player click one of the answer button bellow, it loads the button settings to change the button color
        correctAnswer.setOnClickListener(click ->{
            //Call method loadButtonsSettings e pass that the button was clicked and which button
            buttonClicked = "correctAnswer";
            loadButtonsSettings(true, buttonClicked);
            finalAnswer = true;
        });
        incorrectAnswer1.setOnClickListener(click ->{
            //Call method loadButtonsSettings e pass that the button was clicked and which button
            buttonClicked = "incorrectAnswer1";
            loadButtonsSettings(true, buttonClicked);
            finalAnswer = false;
        });
        incorrectAnswer2.setOnClickListener(click ->{
            //Call method loadButtonsSettings e pass that the button was clicked and which button
            buttonClicked = "incorrectAnswer2";
            loadButtonsSettings(true, buttonClicked);
            finalAnswer = false;
        });
        incorrectAnswer3.setOnClickListener(click ->{
            //Call method loadButtonsSettings e pass that the button was clicked and which button
            buttonClicked = "incorrectAnswer3";
            loadButtonsSettings(true, buttonClicked);
            finalAnswer = false;
        });

        //Submit on click listener
        submit.setOnClickListener(click -> {
            isCorrect(finalAnswer);
            loadAnsweredQuestionButtonSettings();

            player.setQuestionAnswered(currentQuestion, buttonClicked);

            if(player.getQuestionAnswered().size() == questionList.size()){
                player.setScore(correctQuantity);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TriviaPlayActivity.this);
                alertDialogBuilder.setTitle("Well done!")
                        .setMessage("You score " + player.getScore() + " points" + "\n"
                                + "Would you like to save your score?")
                        .setPositiveButton("SAVE", (positiveClick, arg) ->  {
                            SQLiteDatabase db;
                            TriviaDatabase dbOpener = new TriviaDatabase(this);
                            db = dbOpener.getWritableDatabase(); //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer
                            /** Create a new row on database to save player' name and score */
                            ContentValues newRowValues = new ContentValues();
                            //Now provide a value for every database column defined in TriviaDatabase.java:
                            //put string with name in the NAME column:
                            newRowValues.put(TriviaDatabase.COL_NAME, player.getName());
                            //put an integer with score in the SCORE column:
                            newRowValues.put(TriviaDatabase.COL_SCORE, player.getScore());
                            long newID = db.insert(TriviaDatabase.TABLE_NAME, null, newRowValues);

                            Toast.makeText(this, "Score saved", Toast.LENGTH_SHORT).show();
                            Intent startAgain = new Intent(TriviaPlayActivity.this, TriviaMain.class);
                            startActivity(startAgain);
                        })

                        .setNeutralButton("EXIT", (neutralClick, arg) -> {
                            Intent exitTrivia = new Intent(TriviaPlayActivity.this, MainActivity.class);
                            startActivity(exitTrivia);
                        })
                        .setNegativeButton("NEW GAME", (negativeClick, arg) -> {
                            Toast.makeText(this, "New Game", Toast.LENGTH_SHORT).show();
                            Intent startAgain = new Intent(TriviaPlayActivity.this, TriviaMain.class);
                            startActivity(startAgain);
                        })
                        .create().show();
            }
            Log.i(ACTIVITY, "Current question: " + currentQuestion);//Log for debug purpose
        });
        //Creating OnclickListener that leads to Next Question
        ImageButton nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(clock -> {


            if(currentQuestion < lastQuestion){
                nextQuestion = currentQuestion + 1;
                loadQuestion(nextQuestion);
                progressBarQuestions.setProgress(nextQuestion + 1);
                currentQuestion++;

                if(progressBarQuestions.getProgress() == questionList.size()){
                    progressBarQuestions.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                }

            }
            if(player.getQuestionAnswered().get(currentQuestion) != null){
                buttonClicked = (String) player.getQuestionAnswered().get(currentQuestion);

            }
            else {
                correctAnswer.setEnabled(true);
                incorrectAnswer1.setEnabled(true);
                incorrectAnswer2.setEnabled(true);
                incorrectAnswer3.setEnabled(true);
                submit.setEnabled(true);
                submit.setBackgroundColor(getColor(R.color.purple_700));
                submit.setTextColor(getColor(R.color.white));
                buttonClicked = "";

            }
            //Loads the answers buttons settings with the default colors from buttons that are not clicked yet
            loadButtonsSettings(false, "none");
            loadAnsweredQuestionButtonSettings();
        });

        //Creating OnclickListener that leads to previous Question
        ImageButton previousButton = findViewById(R.id.previousButton);
        previousButton.setOnClickListener(clock -> {

            if(currentQuestion > firstQuestion){

                 if(progressBarQuestions.getProgress() == questionList.size()){
                     progressBarQuestions.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#673AB7")));
                 }

                 previousQuestion = currentQuestion - 1;
                 loadQuestion(previousQuestion);
                 progressBarQuestions.setProgress(previousQuestion + 1);
                 currentQuestion--;
            }
            if(player.getQuestionAnswered().get(currentQuestion) != null){
                buttonClicked = (String) player.getQuestionAnswered().get(currentQuestion);
            }
            else {
                correctAnswer.setEnabled(true);
                incorrectAnswer1.setEnabled(true);
                incorrectAnswer2.setEnabled(true);
                incorrectAnswer3.setEnabled(true);
                submit.setEnabled(true);
                submit.setBackgroundColor(getColor(R.color.purple_700));
                submit.setTextColor(getColor(R.color.white));
                buttonClicked = "";
            }

            loadButtonsSettings(false, "none");
            loadAnsweredQuestionButtonSettings();


        });
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

    public void loadAnsweredQuestionButtonSettings(){
        Button correctAnswer = findViewById(R.id.triviaAnswer1);
        Button incorrectAnswer1 = findViewById(R.id.triviaAnswer2);
        Button incorrectAnswer2 = findViewById(R.id.triviaAnswer3);
        Button incorrectAnswer3 = findViewById(R.id.triviaAnswer4);
        Button submit = findViewById(R.id.triviaSubmitQuestion);

        switch (buttonClicked) {
            case "correctAnswer":
                correctAnswer.setBackgroundColor(Color.parseColor("#04bf17"));
                correctAnswer.setTextColor(getColor(R.color.white));
                correctAnswer.setEnabled(false);
                incorrectAnswer1.setEnabled(false);
                incorrectAnswer2.setEnabled(false);
                incorrectAnswer3.setEnabled(false);
                submit.setEnabled(false);
                submit.setBackgroundColor(Color.parseColor("#636362"));
                break;
            case "incorrectAnswer1":
                incorrectAnswer1.setBackgroundColor(Color.parseColor("#bf1704"));
                incorrectAnswer1.setTextColor(getColor(R.color.white));
                correctAnswer.setBackgroundColor(Color.parseColor("#04bf17"));
                correctAnswer.setEnabled(false);
                incorrectAnswer1.setEnabled(false);
                incorrectAnswer2.setEnabled(false);
                incorrectAnswer3.setEnabled(false);
                submit.setEnabled(false);
                submit.setBackgroundColor(Color.parseColor("#636362"));
                break;
            case "incorrectAnswer2":
                incorrectAnswer2.setBackgroundColor(Color.parseColor("#bf1704"));
                incorrectAnswer2.setTextColor(getColor(R.color.white));
                correctAnswer.setBackgroundColor(Color.parseColor("#04bf17"));
                correctAnswer.setEnabled(false);
                incorrectAnswer1.setEnabled(false);
                incorrectAnswer2.setEnabled(false);
                incorrectAnswer3.setEnabled(false);
                submit.setEnabled(false);
                submit.setBackgroundColor(Color.parseColor("#636362"));
                break;
            case "incorrectAnswer3":
                incorrectAnswer3.setBackgroundColor(Color.parseColor("#bf1704"));
                incorrectAnswer3.setTextColor(getColor(R.color.white));
                correctAnswer.setBackgroundColor(Color.parseColor("#04bf17"));
                correctAnswer.setEnabled(false);
                incorrectAnswer1.setEnabled(false);
                incorrectAnswer2.setEnabled(false);
                incorrectAnswer3.setEnabled(false);
                submit.setEnabled(false);
                submit.setBackgroundColor(Color.parseColor("#636362"));
                break;
        }
    }

    /**
     * This method checks if the button clicked is the correct answer or not and adds to the specific variable
     * to increment the values of the correct answer or incorrect answer
     * @param isCorrect
     */
    public void isCorrect(boolean isCorrect){
        TextView correct = findViewById(R.id.correctQuantity);
        TextView wrong = findViewById(R.id.wrongQuantity);
        //If answer is correct, it increments the correctQuantity value and set the text to display the value
        if(isCorrect){
            correctQuantity++;
            if(correctQuantity <= 9){
                correct.setText(String.valueOf("0" + correctQuantity));
            }else{
                correct.setText(String.valueOf(correctQuantity));
            }

        }
        //If answer is correct, it increments the incorrectQuantity value and set the text to display the value
        else{
            incorrectQuantity++;
            if(incorrectQuantity <= 9){
                wrong.setText(String.valueOf("0" + incorrectQuantity));
            }else{
                wrong.setText(String.valueOf(incorrectQuantity));
            }

        }
    }

    /**
     * This method loads and change the button style background and text color depending on which
     * button the user click. If receives @param buttonClick to check is user clicked or not and
     * @param button that receives which button was clicked by the user.
     */
    public void loadButtonsSettings(boolean buttonClick, String button){

        //Declaring answers buttons to configure functionality
        Button correctAnswer = findViewById(R.id.triviaAnswer1);
        Button incorrectAnswer1 = findViewById(R.id.triviaAnswer2);
        Button incorrectAnswer2 = findViewById(R.id.triviaAnswer3);
        Button incorrectAnswer3 = findViewById(R.id.triviaAnswer4);

        //If correctAnswer button was clicked this will Changes the color of that button and set the others to default color
        if(buttonClick && button.equals("correctAnswer")){

            correctAnswer.setBackgroundColor(getColor(R.color.purple_500));
            correctAnswer.setTextColor(getColor(R.color.white));

            incorrectAnswer1.setBackgroundColor(getColor(R.color.white));
            incorrectAnswer1.setTextColor(ColorStateList.valueOf(Color.parseColor("#673AB7")));

            incorrectAnswer2.setBackgroundColor(getColor(R.color.white));
            incorrectAnswer2.setTextColor(ColorStateList.valueOf(Color.parseColor("#673AB7")));

            incorrectAnswer3.setBackgroundColor(getColor(R.color.white));
            incorrectAnswer3.setTextColor(ColorStateList.valueOf(Color.parseColor("#673AB7")));
        }
        //If incorrectAnswer1 button was clicked this will Changes the color of that button and set the others to default color
        else if(buttonClick && button.equals("incorrectAnswer1")){
            correctAnswer.setBackgroundColor(getColor(R.color.white));
            correctAnswer.setTextColor(ColorStateList.valueOf(Color.parseColor("#673AB7")));

            incorrectAnswer1.setBackgroundColor(getColor(R.color.purple_500));
            incorrectAnswer1.setTextColor(getColor(R.color.white));

            incorrectAnswer2.setBackgroundColor(getColor(R.color.white));
            incorrectAnswer2.setTextColor(ColorStateList.valueOf(Color.parseColor("#673AB7")));

            incorrectAnswer3.setBackgroundColor(getColor(R.color.white));
            incorrectAnswer3.setTextColor(ColorStateList.valueOf(Color.parseColor("#673AB7")));
        }
        //If incorrectAnswer2 button was clicked this will Changes the color of that button and set the others to default color
        else if(buttonClick && button.equals("incorrectAnswer2")){
            correctAnswer.setBackgroundColor(getColor(R.color.white));
            correctAnswer.setTextColor(ColorStateList.valueOf(Color.parseColor("#673AB7")));

            incorrectAnswer1.setBackgroundColor(getColor(R.color.white));
            incorrectAnswer1.setTextColor(ColorStateList.valueOf(Color.parseColor("#673AB7")));

            incorrectAnswer2.setBackgroundColor(getColor(R.color.purple_500));
            incorrectAnswer2.setTextColor(getColor(R.color.white));

            incorrectAnswer3.setBackgroundColor(getColor(R.color.white));
            incorrectAnswer3.setTextColor(ColorStateList.valueOf(Color.parseColor("#673AB7")));
        }
        //If incorrectAnswer3 button was clicked this will Changes the color of that button and set the others to default color
        else if(buttonClick && button.equals("incorrectAnswer3")){
            correctAnswer.setBackgroundColor(getColor(R.color.white));
            correctAnswer.setTextColor(ColorStateList.valueOf(Color.parseColor("#673AB7")));

            incorrectAnswer1.setBackgroundColor(getColor(R.color.white));
            incorrectAnswer1.setTextColor(ColorStateList.valueOf(Color.parseColor("#673AB7")));

            incorrectAnswer2.setBackgroundColor(getColor(R.color.white));
            incorrectAnswer2.setTextColor(ColorStateList.valueOf(Color.parseColor("#673AB7")));

            incorrectAnswer3.setBackgroundColor(getColor(R.color.purple_500));
            incorrectAnswer3.setTextColor(getColor(R.color.white));
        }
        //If no button was clicked this will Changes the color of all of them to default color
        //This is used as soon as the activity is created
        else {
            correctAnswer.setBackgroundColor(getColor(R.color.white));
            correctAnswer.setTextColor(ColorStateList.valueOf(Color.parseColor("#673AB7")));

            incorrectAnswer1.setBackgroundColor(getColor(R.color.white));
            incorrectAnswer1.setTextColor(ColorStateList.valueOf(Color.parseColor("#673AB7")));

            incorrectAnswer2.setBackgroundColor(getColor(R.color.white));
            incorrectAnswer2.setTextColor(ColorStateList.valueOf(Color.parseColor("#673AB7")));

            incorrectAnswer3.setBackgroundColor(getColor(R.color.white));
            incorrectAnswer3.setTextColor(ColorStateList.valueOf(Color.parseColor("#673AB7")));
        }
    }

    /**
     * This method loads questions from the list and changes the buttons according to question type (boolean or multiple choice)
     * @param position
     */
    @SuppressLint("SetTextI18n")
    public void loadQuestion(int position){

        //Declaring answers buttons to configure functionality
        Button correctAnswer = findViewById(R.id.triviaAnswer1);
        Button incorrectAnswer1 = findViewById(R.id.triviaAnswer2);
        Button incorrectAnswer2 = findViewById(R.id.triviaAnswer3);
        Button incorrectAnswer3 = findViewById(R.id.triviaAnswer4);

        //Setting question category at the top text view
        TextView questionType = findViewById(R.id.triviaCategoryTitle);
        questionType.setText(questionList.get(position).getCategory());

        //Setting question content to the question card
        TextView questionContent = findViewById(R.id.questionContent);
        questionContent.setText(questionList.get(position).getQuestion());

        //Setting current question number and and total of question on the text view on the right side of the progress bar
        TextView currentQuestion = findViewById(R.id.triviaQuestionProgress);
        if(position < 9){
            //add a zero if question number is below 10 and sum the list position + 1 to avoid start at zero
            currentQuestion.setText("0" + (position + 1) + "/" + questionList.size());
        }
        else{
            currentQuestion.setText(String.valueOf(position + 1) + "/" + questionList.size());
        }

        //If question type is boolean, it will hide the two other buttons and show only the true or false options to click
        if(questionList.get(position).getType().equals("boolean")){
            correctAnswer.setText(questionList.get(position).getCorrectAnswer());
            incorrectAnswer1.setText(questionList.get(position).getIncorrectAnswers().get(0));
            incorrectAnswer2.setVisibility(View.INVISIBLE);
            incorrectAnswer3.setVisibility(View.INVISIBLE);

        }
        //If the questions is not boolean, it is a multiple choice type and all the buttons will be visible and load all answers options
        else{
            correctAnswer.setText(questionList.get(position).getCorrectAnswer());
            incorrectAnswer1.setText(questionList.get(position).getIncorrectAnswers().get(0));
            incorrectAnswer2.setVisibility(View.VISIBLE);
            incorrectAnswer2.setText(questionList.get(position).getIncorrectAnswers().get(1));
            incorrectAnswer3.setVisibility(View.VISIBLE);
            incorrectAnswer3.setText(questionList.get(position).getIncorrectAnswers().get(2));
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
                AlertDialog.Builder helpDialog = new AlertDialog.Builder(TriviaPlayActivity.this);
                helpDialog.setTitle("How to play trivia?")
                        .setMessage("1) Select your answer and click submit" + "\n"
                                + "2) If you answer is correct, it will turn green" + "\n"
                                + "3) If you answer is wrong, it will turn red and show the correct one in green" + "\n"
                                + "4) Click next or previous button to move back and forth" + "\n"
                                + "5) Once you finish, you will see your score and be prompted to save or exit the game")
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
}