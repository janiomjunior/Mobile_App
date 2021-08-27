package com.cst2335.cst2335_finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.cst2335.cst2335_finalproject.trivia.TriviaActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ******Creating OnclickListener that leads to soccer activity***************
        ImageView soccer =  findViewById(R.id.soccer);
        Intent goSoccer = new Intent(this, SoccerOpenerActivity.class);
        soccer.setOnClickListener( click -> startActivity(goSoccer));
        // ******End of OnclickListener that leads to soccer activity***************

        // ******Creating OnclickListener that leads to TriviaActivity***************
        ImageView trivia =  findViewById(R.id.trivia);
        Intent goTrivia = new Intent(this, TriviaMain.class);
        trivia.setOnClickListener( click -> startActivity(goTrivia));
        // ******End of OnclickListener that leads to TriviaActivity***************

        // ******Creating OnclickListener that leads to CarActivity***************
        ImageView car =  findViewById(R.id.car);
        Intent goCar = new Intent(this, CarActivity.class);
        car.setOnClickListener( click -> startActivity(goCar));
        // ******End of OnclickListener that leads to CarActivity***************
      
        // ******Creating OnclickListener that leads to SongsterrActivity***************
        ImageView song =  findViewById(R.id.song);
        Intent goSong = new Intent(this, SongActivity.class);
        song.setOnClickListener( click -> startActivity(goSong));
        // ******End of OnclickListener that leads to SongsterrActivity***************

    }


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
                // ******Creating intent that leads to SongsterActivity***************
                Intent songster = new Intent(this, SongActivity.class);
                this.startActivity(songster);
                // ******End of intent that leads to SongsterActivity***************

                return true;
            case R.id.menuCarDB:

                Toast.makeText(this, "CarDB App", Toast.LENGTH_SHORT).show();
//                // ******Creating intent that leads to CarDBActivity***************
                Intent carDB = new Intent(this, CarActivity.class);
                this.startActivity(carDB);
//                // ******End of intent that leads to CarDBActivity***************

                return true;
            case R.id.menuSoccer:

                Toast.makeText(this, "Soccer App", Toast.LENGTH_SHORT).show();
                // ******Creating intent that leads to SoccerActivity***************
                Intent soccer = new Intent(this, SoccerActivity.class);
                this.startActivity(soccer);
                // ******End of intent that leads to SoccerActivity***************

                return true;
            case R.id.menuAbout:
                Toast.makeText(this, "About App", Toast.LENGTH_SHORT).show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }//End of onOptionsItemSelected method
}