package com.cst2335.cst2335_finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/**
 * this class is used to manipulate the fragment
 * They also have additional callback functions
 */
public class SongDetailFragment extends Fragment {

    private Bundle dataFromActivity;
    private long id;
    private String ArtistBundle;
    private String TitleBundle;
    private String SongIdBundle;
    private String ArtistIdBundle;
    private String RemoveButton;
    private Integer PositionList;
    private long id_fav;
    private AppCompatActivity parentActivity;
    public static SQLiteDatabase db;
    SongMyOpener dbOpener;
    SongFavorites.FavListAdapter favAdapter;
    public static ArrayList<SongFound> elementsDb = new ArrayList<>();


    /**
     * This is where to inflate the GUI. No longer use setContentView() in onCreate()
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // will get the variables which was passed from bundle on SongActivity
        dataFromActivity = getArguments();
        id = dataFromActivity.getLong(SongActivity.ITEM_ID );
        ArtistBundle = dataFromActivity.getString(SongActivity.ITEM_ARTIST);
        TitleBundle = dataFromActivity.getString(SongActivity.ITEM_TITLE);
        SongIdBundle = dataFromActivity.getString(SongActivity.ITEM_SONGID);
        ArtistIdBundle = dataFromActivity.getString(SongActivity.ITEM_ARITSTID);
        RemoveButton = dataFromActivity.getString((SongFavorites.REMOVEBUTTON));
        PositionList = dataFromActivity.getInt(SongFavorites.ITEM_POSITION);
        id_fav = dataFromActivity.getLong(SongFavorites.ITEM_ID);

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.song_fragment_details, container, false);

        //retrieve the buttons save favourites and remove to make it VISIBLE AND INVISIBLE
        //according to from where it is coming
        if (RemoveButton.equals("Y")){
            Button button_frag_remo_fav = result.findViewById(R.id.remove_favorites);
            button_frag_remo_fav.setVisibility(View.VISIBLE);
            Button button_frag_save_fav = result.findViewById(R.id.save_favorites);
            button_frag_save_fav.setVisibility(View.INVISIBLE);
            Button frag_button = result.findViewById(R.id.fragment_button);
            frag_button.setVisibility(View.VISIBLE);
        }else {
            Button button_frag_remo_fav = result.findViewById(R.id.remove_favorites);
            button_frag_remo_fav.setVisibility(View.INVISIBLE);
            Button button_frag_save_fav = result.findViewById(R.id.save_favorites);
            button_frag_save_fav.setVisibility(View.VISIBLE);
        }

        //will retrieve the ImageView iconSongId to be set when clicked
        ImageView iconSongId = result.findViewById(R.id.iconSongid);

        //listener to load the webpage with the songId concatenated
        iconSongId.setOnClickListener(click ->
        {
            String url = "http://www.songsterr.com/a/wa/song?id=" + SongIdBundle;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData( Uri.parse(url) );
            startActivity(i);

        });

        //will retrieve the ImageView iconArtistId to be set when clicked
        ImageView iconArtistId = result.findViewById(R.id.iconArtistid);

        //listener to load the webpage with the artistId concatenated
        iconArtistId.setOnClickListener(click ->
        {
            String url = "http://www.songsterr.com/a/wa/artist?id=" + ArtistIdBundle;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData( Uri.parse(url) );
            startActivity(i);

        });

        //will retrieve the Button Save Favorites to save the song at database
        Button buttonHide = result.findViewById(R.id.fragment_button);

        //listener to save the songs at database table
        buttonHide.setOnClickListener(click ->
        {
            Intent listOfFavourites = new Intent (this.parentActivity, SongFavorites.class);
            startActivity(listOfFavourites);
        });
        //will retrieve the Button Save Favorites to save the song at database
        Button buttonSaveFavorites = result.findViewById(R.id.save_favorites);

        //listener to save the songs at database table
        buttonSaveFavorites.setOnClickListener(click ->
        {

            //will retrieve the Button Favorites to save the song at database
            //Button buttonFavorites = result.findViewById(R.id.favorites);
            //buttonFavorites.setVisibility(View.VISIBLE);

            //get a database connection:
            dbOpener = new SongMyOpener(this.parentActivity);
            db = dbOpener.getWritableDatabase();//This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer

            //add to the database and get the new ID

            ContentValues newRowValues = new ContentValues();

            //Now provide a value for every database column defined in MyOpener.java:
            //put string title in the column TITLE:
            newRowValues.put(SongMyOpener.COL_ARTIST, ArtistBundle);
            //put string title in the column TITLE:
            newRowValues.put(SongMyOpener.COL_TITLE, TitleBundle);
            //put string SongId in the column SONGID:
            newRowValues.put(SongMyOpener.COL_SONGID, SongIdBundle);
            //put string ArtisId in the ARTISTID column:
            newRowValues.put(SongMyOpener.COL_ARTISTID, ArtistIdBundle);
            //Now insert in the database:
            long newId = db.insert(SongMyOpener.TABLE_NAME, null, newRowValues);

            //Added the new contact to the list
            elementsDb.add(new SongFound(TitleBundle, SongIdBundle, ArtistIdBundle, ArtistBundle, newId));

            //toast to advice the song was saved
            Toast.makeText(this.parentActivity, R.string.song_saved,Toast.LENGTH_LONG).show();

            //db.close();
        });

        //will retrieve the Button Remove from database remove the song at database
        Button buttonRemFavorites = result.findViewById(R.id.remove_favorites);

        //listener to save the songs at database table
        buttonRemFavorites.setOnClickListener(click ->
            {
                //int pos = (int) id_fav;
                SongFound selectedFav = elementsDb.get(PositionList);


                AlertDialog.Builder builder = new AlertDialog.Builder(this.parentActivity);
                builder.setTitle("You clicked on item: " + TitleBundle)
                .setMessage("You can delete")
                //.setView(song_view) //add the 3 edit texts showing the contact information
                .setNegativeButton("Delete", (cb, b) -> {
                    deleteContact(selectedFav); //remove the contact from database
                    elementsDb.remove(PositionList); //remove the contact from contact list
                    //favAdapter.notifyDataSetChanged(); //there is one less item so update the list
                    Toast.makeText(this.parentActivity, R.string.removed_songToast, Toast.LENGTH_LONG).show();
                })
                .setNeutralButton("Cancel", (cb, b) -> { })
                .create().show();
            });


        //show the id:
        TextView idView = (TextView)result.findViewById(R.id.fragment_id);
        idView.setText("ID=" + PositionList);

        //show the artist name on fragment
        TextView artistName = (TextView)result.findViewById(R.id.fragment_artistName);
        artistName.setText(getString(R.string.artistName) + " " + ArtistBundle);

        //show the title song on fragment
        TextView title = (TextView)result.findViewById(R.id.fragment_title);
        title.setText(getString(R.string.titleSong) + " " + TitleBundle);

        //show the song ID on fragment
        TextView songId = result.findViewById(R.id.fragment_songId);
        songId.setText(getString(R.string.songId) + " " + SongIdBundle);

        //show the artist ID on Fragment
        TextView artistId = result.findViewById(R.id.fragment_artistId);
        artistId.setText(getString(R.string.artistId) + " " + ArtistIdBundle);

        // get the delete button, and add a click listener:
//        Button hideButton = (Button)result.findViewById(R.id.fragment_button);
//        hideButton.setOnClickListener( clk -> {
//
//            //Tell the parent activity to remove
//            parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
//        });

        return result;
    }

    protected void deleteContact(SongFound m)
    {
        db.delete(SongMyOpener.TABLE_NAME, SongMyOpener.COL_ID + "= ?", new String[] {Long.toString(m.getId())});
    }

    /**
     * When the fragment has been added to the Activity which has the FrameLayout.
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }

}

