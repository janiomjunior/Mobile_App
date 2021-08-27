package com.cst2335.cst2335_finalproject.trivia.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class manipulates the database data to insert delete and update players name and scores
 */
public class TriviaDatabase extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "TriviaDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "TOPPLAYERS";
    public final static String COL_NAME = "NAME";
    public final static String COL_SCORE = "SCORE";
    public final static String COL_ID = "_id";

    /**
     * This constructor receives the context and call supper class
     * @param ctx
     */
    public TriviaDatabase(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }


    /**
     * This function gets called if no database file exists.
     * Look on your device in the /data/data/package-name/database directory.
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_SCORE + " integer,"
                + COL_NAME + " text);");  // add or remove columns
    }


    /**
     * This function gets called if the database version on your device is lower than VERSION_NUM
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }

    /**
     * This function gets called if the database version on your device is higher than VERSION_NUM
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }
}
