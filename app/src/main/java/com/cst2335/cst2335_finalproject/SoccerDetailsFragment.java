package com.cst2335.cst2335_finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SoccerDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoccerDetailsFragment extends Fragment {

    //variables
    private Bundle dataFromArticle;
    private long id;
    private Button urlButton;
    private Button saveButton;
    private AppCompatActivity parentActivity;

    //0411
    private Adapter myAdapter;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SoccerDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SoccerDetailsFragment newInstance(String param1, String param2) {
        SoccerDetailsFragment fragment = new SoccerDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromArticle = getArguments();
        //String idText = dataFromArticle.getString("ID");
        String titleText = dataFromArticle.getString("TITLE");
        String linkText = dataFromArticle.getString("LINK");
        String descriptionText = dataFromArticle.getString("DESCRIPTION");
        String dateText = dataFromArticle.getString("PUBDATE");

        // Inflate the layout for this fragment
        //dataFromArticles = getArguments();
        View view = inflater.inflate(R.layout.fragment_soccer_details, container, false);

        //inflate buttons
        urlButton = (Button) view.findViewById(R.id.urlButton);
        saveButton = (Button) view.findViewById(R.id.saveToFavButton);

        //goToURL
        urlButton.setOnClickListener(click->{
            //now to show the links that are on the feed in the link tag
            //makes a connection to the web page
            //linkText will get one string (one article at a time from the main activity)
            //onItemClick needs 4 things (parameters), that's only for listView
            //we don't need it for this, we are only grabbing one link at a time
            //setOnClick needed only for one thing
            Uri uri = Uri.parse(linkText);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        });

        //save to favourites database
        saveButton.setOnClickListener(click -> {


            //calling article database class
            SoccerArticlesDB articleDB = new SoccerArticlesDB(parentActivity);
            SQLiteDatabase db = articleDB.getWritableDatabase();

            //this will insert data into the database
            ContentValues cValues = new ContentValues();

            //providing the value for every database column defined in ArticleDB.java
            cValues.put(SoccerArticlesDB.TITLE_COL, titleText);
            cValues.put(SoccerArticlesDB.DATE_COL, dateText);
            cValues.put(SoccerArticlesDB.DESCRIPTION_COL, descriptionText);
            cValues.put(SoccerArticlesDB.LINK_COL, linkText);

            //inserting into database
            //FIX THIS ISSUE "SOCCER_ARTICLES HAS NO COLUMNS NAMED DATE
            long id = db.insert(SoccerArticlesDB.TABLE_NAME, null, cValues);
            //show id of inserted item:

        });



        //show the id: (view)
        //TextView idArticle = (TextView)  view.findViewById(R.id.articleID);
        //idArticle.setText("ID" + idText);

        //show the title: (view)
        TextView titleArticle = (TextView) view.findViewById(R.id.articleTitle);
        titleArticle.setText("Title: " + titleText);

        //show the link: (view)
        TextView linkArticle = (TextView) view.findViewById(R.id.articleLink);
        linkArticle.setText("Link: " + linkText);

        //show the description: (view)
        TextView descriptionArticle = (TextView) view.findViewById(R.id.articleDescription);
        descriptionArticle.setText("Description: " + descriptionText);

        //show the date: (view)
        TextView dateArticle = (TextView) view.findViewById(R.id.articleDate);
        dateArticle.setText("Date: " + dateText);

        return view;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be tablet or phone depending on what is used
        parentActivity = (AppCompatActivity) context;
    }
}