package com.cst2335.cst2335_finalproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * this class is used to deal with the songs the the Favourites list
 */

public class SongFavorites extends AppCompatActivity {

    private AppCompatActivity parentActivity;
    FavListAdapter favAdapter;
    SQLiteDatabase dbFav = SongDetailFragment.db;
    SongDetailFragment dFragment;
    SongFound selectedSong;
    public ArrayList<SongFound> favList = SongDetailFragment.elementsDb;

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
        setContentView(R.layout.song_button_fav);

        loadDataFromDatabase(); //get any previously saved Contact objects

//      initialize the list
        favAdapter = new FavListAdapter();
        favAdapter.notifyDataSetChanged();

//      retrieve the ListView from favourites_ListView_song.xml
        ListView myList = findViewById(R.id.favouritesListView);

        myList.setAdapter(favAdapter);

        //used to identify if is a tablet or phone screen
        boolean isTablet = findViewById(R.id.frameLayout) != null;//check if the FrameLayout is loaded

        //Click listener for ListView
        myList.setOnItemClickListener( (par, view, pos, id) -> {

            //Clicking on one of the songs in the list should show the artist id, song id, and title in the fragment

            Bundle dataToPass = new Bundle();

            dataToPass.putString(ITEM_ARTIST, favList.get(pos).getArtist() );
            dataToPass.putString(ITEM_TITLE, favList.get(pos).getTitle() );
            dataToPass.putString(ITEM_SONGID, favList.get(pos).getSongId() );
            dataToPass.putString(ITEM_ARITSTID, favList.get(pos).getArtistId());
            dataToPass.putInt(ITEM_POSITION, pos);
            dataToPass.putLong(ITEM_ID, id);
            dataToPass.putString(REMOVEBUTTON, "Y");

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
                Intent nextActivity = new Intent(SongFavorites.this, SongEmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
            showSong(pos);

        });

        //will retrieve the Button Save Favorites to save the song at database
        Button buttonBack = findViewById(R.id.buttonBack);

        //listener to save the songs at database table
        buttonBack.setOnClickListener(click ->
        {
            Intent songActivity = new Intent (SongFavorites.this, SongActivity.class);
            startActivity(songActivity);
        });

    }

    /**
     * ListAdapter is an Interface that you must implement by writing
     * these 4 public functions
     * This list adapter will be used to update the list view with data retrieved from data base
     * */
    class FavListAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return favList.size();
        }

        @Override
        public Object getItem(int position) {
            return favList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return favList.get(position).getId();
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.song_retrieved, viewGroup, false );

            //set what the text should be for this row:
            TextView aAOB = newView.findViewById(R.id.artist);
            aAOB.setText(" " + favList.get(position).getArtist());

            //set what the text should be for this row:
            TextView tAOB = newView.findViewById(R.id.title);
            tAOB.setText(" " + favList.get(position).getTitle());

            return newView;

        }
    }

    private void loadDataFromDatabase()
    {
        favList.clear();

        //get a database connection:
        SongMyOpener dbOpener = new SongMyOpener(this);
        dbFav = dbOpener.getWritableDatabase(); //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer


        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String [] columns = {SongMyOpener.COL_ID, SongMyOpener.COL_ARTIST, SongMyOpener.COL_TITLE, SongMyOpener.COL_SONGID, SongMyOpener.COL_ARTISTID};
        //query all the results from the database:
        Cursor results = dbFav.query(false, SongMyOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        //printCursor(results, 1);

        //Now the results object has rows of results that match the query.
        //find the column indices:
        int artistIdColumnIndex = results.getColumnIndex(SongMyOpener.COL_ARTIST);
        int titleIdColumnIndex = results.getColumnIndex(SongMyOpener.COL_TITLE);
        int songIdColumnIndex = results.getColumnIndex(SongMyOpener.COL_SONGID);
        int artistIdColIndex = results.getColumnIndex(SongMyOpener.COL_ARTISTID);
        int idColIndex = results.getColumnIndex(SongMyOpener.COL_ID);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            String artist = results.getString(artistIdColumnIndex);
            String title = results.getString(titleIdColumnIndex);
            String songid = results.getString(songIdColumnIndex);
            String artistid = results.getString(artistIdColIndex);
            long id = results.getLong(idColIndex);

            favList.add(new SongFound(title, songid, artistid, artist, id));

        }

        //At this point, the elementsDB array has loaded every row from the cursor.
    }
    protected void showSong(int position)
    {

        selectedSong = favList.get(position);
        View song_view;
        TextView frag_id;
        TextView frag_artistName;
        TextView frag_title;
        TextView frag_singId;
        TextView frag_artistId;
        Button button_frag_save_fav;
        Button button_frag_remo_fav;


        song_view = getLayoutInflater().inflate(R.layout.song_fragment_details, null);

        //get the TextViews
        frag_id = song_view.findViewById(R.id.fragment_id);
        frag_artistName = song_view.findViewById(R.id.fragment_artistName);
        frag_title = song_view.findViewById(R.id.fragment_title);
        frag_singId = song_view.findViewById(R.id.fragment_songId);
        frag_artistId = song_view.findViewById(R.id.fragment_artistId);

        //set the fields for the alert dialog
        frag_id.setText(String.valueOf(selectedSong.getId()));
        frag_artistName.setText(selectedSong.getArtist());
        frag_title.setText(selectedSong.getTitle());
        frag_singId.setText(selectedSong.getSongId());
        frag_artistId.setText(selectedSong.getArtistId());

    }

}

