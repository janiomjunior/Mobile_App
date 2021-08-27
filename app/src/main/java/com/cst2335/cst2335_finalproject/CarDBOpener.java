package com.cst2335.cst2335_finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CarDBOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "CarDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "SAVED";
    public final static String COL_NAME = "NAME";
    public final static String COL_MAKE = "MAKE";
    public final static String COL_MDL = "MODEL";
    public final static String COL_ID = "_id";

    public CarDBOpener(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_MAKE  + " text,"
                + COL_NAME + " text,"
                + COL_MDL + " text );" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }
}