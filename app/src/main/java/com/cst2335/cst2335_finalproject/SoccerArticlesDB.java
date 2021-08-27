package com.cst2335.cst2335_finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SoccerArticlesDB extends SQLiteOpenHelper {
    public final static String DATABASE_NAME = "soccer";
    public final static int VERSION_NUMBER = 2;
    public final static String TABLE_NAME = "soccer_articles";
    public final static String ID_COL = "id";
    public final static String TITLE_COL = "title";
    public final static String DATE_COL = "date";
    public final static String DESCRIPTION_COL = "description";
    public final static String LINK_COL = "link";

    public SoccerArticlesDB(Context ctx){
        super(ctx, DATABASE_NAME, null, VERSION_NUMBER);
    }

    /**
     * function gets called if no database file exists under the
     * data/data/package-name/database directory
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //make table
        db.execSQL("CREATE TABLE "+ TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                //+ ID_COL + " TEXT, "
                + DATE_COL + "TEXT, "
                + DESCRIPTION_COL + "TEXT, "
                + TITLE_COL + "TEXT, "
                + LINK_COL + "TEXT);"); //add or remove columns
    }


    /**
     * this function gets called if the database version on your device is higher than VERSION_NUMBER
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drops the old table
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);

        //creates the new table:
        onCreate(db);
    }
}
