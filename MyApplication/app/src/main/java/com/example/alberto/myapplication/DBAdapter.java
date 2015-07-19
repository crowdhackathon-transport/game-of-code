package com.example.alberto.myapplication;

/**
 * Created by andrewchron on 14/07/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Random;


// TO USE:
// Change the package (at top) to match your project.
// Search for "TODO", and make the appropriate changes.
public class DBAdapter {

    /////////////////////////////////////////////////////////////////////
    //	Constants & Data
    /////////////////////////////////////////////////////////////////////
    // For logging:
    private static final String TAG = "DBAdapter";

    // DB Fields
    public static final String KEY_ROWID = "_id";
    public static final int COL_ROWID = 0;
    /*
     * CHANGE 1:
     */
    // TODO: Setup your fields here:
    public static final String KEY_PLACE = "place";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_COORDINATES = "coordinates";
    public static final String KEY_TIME = "time";
    public static final String KEY_RATING = "rating";
    public static final String KEY_FOREAS = "foreas";


    // TODO: Setup your field numbers here (0 = KEY_ROWID, 1=...)
    public static final int COL_PLACE = 1;
    public static final int COL_IMAGE = 2;
    public static final int COL_COMMENT = 3;
    public static final int COL_COORDINATES = 4;
    public static final int COL_TIME = 5;
    public static final int COL_RATING=6;
    public static final int COL_FOREAS=7;


    public static final String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_PLACE, KEY_IMAGE, KEY_COMMENT, KEY_COORDINATES, KEY_TIME, KEY_RATING,KEY_FOREAS};

    // DB info: it's name, and the table we are using (just one).
    public static final String DATABASE_NAME = "MyDb";
    public static final String DATABASE_TABLE = "mainTable";
    // Track DB version if a new version of your app changes the format.
    public static final int DATABASE_VERSION = 6;

    private static final String DATABASE_CREATE_SQL =
            "create table " + DATABASE_TABLE
                    + " (" + KEY_ROWID + " integer primary key autoincrement, "

			/*
			 * CHANGE 2:
			 */
                    // TODO: Place your fields here!
                    // + KEY_{...} + " {type} not null"
                    //	- Key is the column name you created above.
                    //	- {type} is one of: text, integer, real, blob
                    //		(http://www.sqlite.org/datatype3.html)
                    //  - "not null" means it is a required field (must be given a value).
                    // NOTE: All must be comma separated (end of line!) Last one must have NO comma!!
                    + KEY_PLACE + " text not null, "
                    + KEY_IMAGE + " integer not null, "
                    + KEY_COMMENT + " string not null, "
                    + KEY_COORDINATES + " string not null, "
                    + KEY_TIME + " string not null, "
                    + KEY_RATING + " string not null, "
                    + KEY_FOREAS + " string not null "

                    // Rest  of creation:
                    + ");";

    // Context of application who uses us.
    private final Context context;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    /////////////////////////////////////////////////////////////////////
    //	Public methods:
    /////////////////////////////////////////////////////////////////////

    public DBAdapter( Context context) {
        this.context = context;

        myDBHelper = new DatabaseHelper(this.context);
    }

    // Open the database connection.
    public DBAdapter open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        myDBHelper.close();
    }

    // Add a new set of values to the database.
    public long insertRowDemo(String place, int image, String comment,String coordinates, String time, String foreas) {
		/*
		 * CHANGE 3:
		 */
        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_PLACE, place);
        initialValues.put(KEY_IMAGE, image);
        initialValues.put(KEY_COMMENT, comment);
        initialValues.put(KEY_COORDINATES, coordinates);
        initialValues.put(KEY_TIME, time);
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int min=12;
        int max=80;
        int randomNum = rand.nextInt((max - min) + 1) + min;
        initialValues.put(KEY_RATING, randomNum);

        initialValues.put(KEY_FOREAS, foreas);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public long insertRow(String place, int image, String comment,String coordinates, String time, String foreas) {
		/*
		 * CHANGE 3:
		 */
        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_PLACE, place);
        initialValues.put(KEY_IMAGE, image);
        initialValues.put(KEY_COMMENT, comment);
        initialValues.put(KEY_COORDINATES, coordinates);
        initialValues.put(KEY_TIME, time);

        initialValues.put(KEY_RATING,"0");

        initialValues.put(KEY_FOREAS, foreas);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }
 public Cursor showlatlng(int id)
 {
     String where = "_id=?";
     String[] tables= {"_id","coordinates"};
     String arg=Integer.toString(id);
     String[] args={arg};
     Cursor c = 	db.query(true, DATABASE_TABLE,tables ,
             where, args, null, null, null, null);
     if (c != null) {
         c.moveToFirst();
     }
     return c;
 }
    public void deleteAll() {
        Cursor c = getAllRows();
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
        if (c.moveToFirst()) {
            do {
                deleteRow(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }

    // Return all data in the database.
    public Cursor getAllRows() {
        String where = null;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;

    }

    public Cursor getbyupvote(){
        String where = null;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null,"rating DESC", null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;

    }

    // Get a specific row (by rowId)
    public Cursor getRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Change an existing row to be equal to new data.
    public boolean updateRow(long rowId, String place, int image, String comment,String coordinates,String time) {
        String where = KEY_ROWID + "=" + rowId;

		/*
		 * CHANGE 4:
		 */
        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_PLACE,place);
        newValues.put(KEY_IMAGE,image);
        newValues.put(KEY_COMMENT,comment);
        newValues.put(KEY_COORDINATES,coordinates);
        newValues.put(KEY_TIME,time);

        // Insert it into the database.
        return db.update(DATABASE_TABLE, newValues, where, null) != 0;
    }



    /////////////////////////////////////////////////////////////////////
    //	Private Helper Classes:
    /////////////////////////////////////////////////////////////////////

    /**
     * Private class which handles database creation and upgrading.
     * Used to handle low-level database access.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // Recreate new database:
            onCreate(_db);
        }
    }
}
