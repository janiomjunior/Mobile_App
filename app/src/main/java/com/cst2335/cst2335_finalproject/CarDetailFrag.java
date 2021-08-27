package com.cst2335.cst2335_finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static java.lang.Boolean.getBoolean;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @param dataFromActivity a bundle of data passed here by CarActivity
 * @param name the item name passed by CarActivity
 * @param parentActivity use for onAttach to CarEmpty
 */
public class CarDetailFrag extends Fragment {
    private Bundle dataFromActivity;
    private String name;
    private String make;
    private String model;
    private String modelID;
    private AppCompatActivity parentActivity;


    SQLiteDatabase db;
    /**
     *
     * @param inflater use to inflate frag view
     * @param container use to contain frag vie3
     * @param savedInstanceState bundle of data saved
     * @return inflated view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        CarDBOpener dbOpener = new CarDBOpener(this.getActivity());
        db = dbOpener.getWritableDatabase();
        dataFromActivity = getArguments();
        name = dataFromActivity.getString(CarActivity.ITEM_NAME );
        make = dataFromActivity.getString(CarActivity.ITEM_MAKE );
        model = dataFromActivity.getString(CarActivity.ITEM_MODEL );
        modelID = Long.toString(dataFromActivity.getLong(CarActivity.ITEM_ID ));

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.activity_car_detail_frag, container, false);

        //show the message
        TextView nameText = (TextView)result.findViewById(R.id.carNameInFrag);
        TextView makeText = (TextView)result.findViewById(R.id.carMakeInFrag);
        nameText.setText(name);
        makeText.setText(make);

        Button carSave = (Button) result.findViewById(R.id.carSave);
        Button carDel = (Button) result.findViewById(R.id.carDel);

        if(dataFromActivity.getBoolean(CarActivity.ITEM_SAVED)){
           carSave.setVisibility(View.GONE);
        } else {
            carDel.setVisibility(View.GONE);
        }
        carSave.setOnClickListener( click ->
        {
            String noModelText = getResources().getString(R.string.noModelText);
            String selectModelText = getResources().getString(R.string.selectModelText);

            //selectedCar
            ContentValues newRowValues = new ContentValues();
            newRowValues.put(CarDBOpener.COL_MAKE, make);
            newRowValues.put(CarDBOpener.COL_MDL, model);
            newRowValues.put(CarDBOpener.COL_NAME, name);

            //Now insert in the database:
            db.insert(CarDBOpener.TABLE_NAME, null, newRowValues);


        });
        carDel.setOnClickListener((p)  ->
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());
            alertDialogBuilder.setTitle("Want to delete?")

                    //What is the message:
                    .setMessage("Do you want to delete this row?")

                    //what the Yes button does:
                    .setPositiveButton("Yes", (click, arg) -> {
                        db.delete(CarDBOpener.TABLE_NAME, CarDBOpener.COL_ID + "= ?", new String[] {modelID});
                    })
                    //What the No button does:
                    .setNegativeButton("No", (click, arg) -> { })

                    //Show the dialog
                    .create().show();
        });


        //Declare the details button and give it function to open url
        Button carDeet = (Button) result.findViewById(R.id.carDeet);
        carDeet.setOnClickListener( click ->
        {
            //StringBuilder to build url
            StringBuilder sbCarDB = new StringBuilder("https://www.google.com/search?q=");
            sbCarDB.append(make);
            sbCarDB.append("+");
            sbCarDB.append(name);

            //open browser using built url
            Intent detailsBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(sbCarDB.toString()));
            startActivity(detailsBrowser);
        });

        //Declare the details button and give it function to open url
        Button carBuy = (Button) result.findViewById(R.id.carBuy);
        carBuy.setOnClickListener( click ->
        {

            //StringBuilder to build url
            StringBuilder sbCarDB = new StringBuilder("https://www.autotrader.ca/cars/?mdl=");
            sbCarDB.append(name);
            sbCarDB.append("&make=");
            sbCarDB.append(make);
            sbCarDB.append("&loc=K2G1V8");

            //open browser using built url
            Intent detailsBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(sbCarDB.toString()));
            startActivity(detailsBrowser);
        });
        return result;
    }

    /**
     * @param  context CarEmpty
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }
}