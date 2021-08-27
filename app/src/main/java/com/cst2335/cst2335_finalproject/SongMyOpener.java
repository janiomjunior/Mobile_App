package com.cst2335.cst2335_finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * for creating and opening databases. Must write a subclass for the application
 */

    public class SongMyOpener extends SQLiteOpenHelper {

        protected final static String DATABASE_NAME = "SongDB";
        protected final static int VERSION_NUM = 2;
        public final static String TABLE_NAME = "SONGTABLE";
        public final static String COL_ARTIST = "ARTIST";
        public final static String COL_TITLE = "TITLE";
        public final static String COL_SONGID = "SONGID";
        public final static String COL_ARTISTID = "ARTISTID";
        public final static String COL_ID = "_id";

        //public SongMyOpener(Context ctx)
        //

        public SongMyOpener(Context ctx) {
            super(ctx, DATABASE_NAME, null, VERSION_NUM); }


        /**This function gets called if no database file exists.
        * Look on your device in the /data/data/package-name/database directory.
        * @param db
        */

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_ARTIST + " text,"
                    + COL_TITLE + " text,"
                    + COL_SONGID + " text,"
                    + COL_ARTISTID + " text);");  // add or remove columns
        }

        /**
        * this function gets called if the database version on your device is lower than VERSION_NUM
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
        * this function gets called if the database version on your device is higher than VERSION_NUM
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

