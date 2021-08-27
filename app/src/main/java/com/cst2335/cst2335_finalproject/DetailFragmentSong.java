package com.cst2335.cst2335_finalproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


public class DetailFragmentSong extends Fragment {

    private Bundle dataFromActivity;
    private long id;
    private AppCompatActivity parentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        id = dataFromActivity.getLong(SongActivity.ITEM_ID );

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.fragment_details_song, container, false);

        //show the id:
        TextView idView = (TextView)result.findViewById(R.id.fragment_id);
        idView.setText("ID=" + id);

//        //show the artist name on fragment
//        TextView name = (TextView)result.findViewById(R.id.fragment_title);
//        name.setText(getString(R.string.artistName) + dataFromActivity.getString(SongActivity.ITEM_NAME));

        //show the title song on fragment
        TextView title = (TextView)result.findViewById(R.id.fragment_title);
        title.setText(getString(R.string.titleSong) + " " + dataFromActivity.getString(SongActivity.ITEM_TITLE));


        //show the song ID on fragment
        TextView songId = result.findViewById(R.id.fragment_songId);
        songId.setText(getString(R.string.songId) + " " + dataFromActivity.getString(SongActivity.ITEM_SONGID));

        //show the artist ID on Fragment
        TextView artistId = result.findViewById(R.id.fragment_artistId);
        artistId.setText(getString(R.string.artistId) + " " + dataFromActivity.getString(SongActivity.ITEM_ARITSTID));

        // get the delete button, and add a click listener:
        Button hideButton = (Button)result.findViewById(R.id.fragment_button);
        hideButton.setOnClickListener( clk -> {

            //Tell the parent activity to remove
            parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
        });

        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }
}