package com.example.matias.tprof.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mati on 12/8/2015.
 */
public class QuotesDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "quotes.db";

    public QuotesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_STOCKS_TABLE = "CREATE TABLE " + QuotesContract.StockEntry.TABLE_NAME + " (" +
                QuotesContract.StockEntry._ID + " INTEGER PRIMARY KEY," +
                QuotesContract.StockEntry.COLUMN_FULLNAME + " TEXT NOT NULL, " +
                QuotesContract.StockEntry.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                " );";

        final String SQL_CREATE_BONDS_TABLE = "CREATE TABLE " + QuotesContract.BondEntry.TABLE_NAME + " (" +
                QuotesContract.BondEntry._ID + " INTEGER PRIMARY KEY," +

                // the ID of the location entry associated with this weather data
                QuotesContract.BondEntry.COLUMN_SYMBOL +  " TEXT NOT NULL, " +
                QuotesContract.BondEntry.COLUMN_FULLNAME +  " TEXT NOT NULL, " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_STOCKS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BONDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuotesContract.StockEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuotesContract.BondEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
