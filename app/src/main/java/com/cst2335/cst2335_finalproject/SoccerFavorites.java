package com.cst2335.cst2335_finalproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SoccerFavorites extends AppCompatActivity {
    SQLiteDatabase db;
    ArrayList<String> titles;
    ArrayList<String> links;
    ArrayList<String> description;

    //declare name to be stored
    ArrayList<Favourites> favourites;

    //Another listview to show the articles that was stored
    ListView lvRss2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccer_favorites);

        //inflate the view
        lvRss2 = (ListView) findViewById(R.id.lvRss2);

        boolean isTablet = findViewById(R.id.fragmentLocation) != null;

        lvRss2.setOnItemClickListener((list, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(SoccerActivity.ITEM_TITLE, titles.get(position));
            dataToPass.putString(SoccerActivity.ITEM_LINK, links.get(position));
            dataToPass.putString(SoccerActivity.ITEM_DESCRIPTION, description.get(position));

            if(isTablet)
            {
                SoccerDetailsFragment dFragment = new SoccerDetailsFragment();
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame2, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(SoccerFavorites.this, SoccerEmpty2.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });
    }


    /**
     *  purpose of this method is load the data
     *  or in this case, the list of articles that was saved by the user
     *  this will be loaded onto the layout of empty activity 2
     *  which contains the second frame layout
     */
    private void loadDataFromDatabase() {

        //this is to create a database connection:
        SoccerArticlesDB dbOpener = new SoccerArticlesDB(this);

        //this will call onCreate() if never built the table before
        db = dbOpener.getWritableDatabase();

        //retrieving all the columns
        String[] columns = {SoccerArticlesDB.ID_COL, SoccerArticlesDB.LINK_COL, SoccerArticlesDB.DATE_COL, SoccerArticlesDB.DESCRIPTION_COL};
        //query results from database
        Cursor results = db.query(false, dbOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        //Results objects has rows of results that now match the query
        //Finding column indices
        int idColumnIndex = results.getColumnIndex(dbOpener.ID_COL);
        int titleColumnIndex = results.getColumnIndex(dbOpener.TITLE_COL);
        int linkColumnIndex = results.getColumnIndex(dbOpener.LINK_COL);
        int dateColumnIndex = results.getColumnIndex(dbOpener.DATE_COL);
        int descriptionColumnIndex = results.getColumnIndex(dbOpener.DESCRIPTION_COL);

        //iterate over the results and move to the next item
        while (results.moveToNext()) {

            long primaryId = results.getLong(idColumnIndex);
            String title = results.getString(titleColumnIndex);
            String link = results.getString(linkColumnIndex);
            String date = results.getString(dateColumnIndex);
            String descriptionofArticle = results.getString(descriptionColumnIndex);

            //add the new articles to the favourites array list
            favourites.add(new Favourites(title, link, date, descriptionofArticle, primaryId));
        }
    }

    /**
     * this method will load the data base when the application
     * starts from what is saved
     */
    @Override
    protected void onStart() {
        super.onStart();
        favourites = new ArrayList<>();
        loadDataFromDatabase();
    }
}
