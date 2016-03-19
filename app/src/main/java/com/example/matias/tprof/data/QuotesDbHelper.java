package com.example.matias.tprof.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mati on 12/8/2015.
 */
public class QuotesDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 10;

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
                QuotesContract.StockEntry.COLUMN_SYMBOL + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_BONDS_TABLE = "CREATE TABLE " + QuotesContract.BondEntry.TABLE_NAME + " (" +
                QuotesContract.BondEntry._ID + " INTEGER PRIMARY KEY," +

                // the ID of the location entry associated with this weather data
                QuotesContract.BondEntry.COLUMN_FULLNAME +  " TEXT NOT NULL, " +
                QuotesContract.BondEntry.COLUMN_SYMBOL +  " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_STOCK_QUOTES_TABLE = "CREATE TABLE " + QuotesContract.StockQuotesEntry.TABLE_NAME + " (" +
                QuotesContract.StockQuotesEntry._ID + " INTEGER PRIMARY KEY," +

                // the ID of the location entry associated with this weather data
                QuotesContract.StockQuotesEntry.COLUMN_CURRENCY +  " TEXT NOT NULL, " +
                QuotesContract.StockQuotesEntry.COLUMN_LAST_CHANGE +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.StockQuotesEntry.COLUMN_LAST_CHANGE_PERCENTAGE +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.StockQuotesEntry.COLUMN_LAST_TRADE_DATE +  " DATETIME NOT NULL, " +
                QuotesContract.StockQuotesEntry.COLUMN_LAST_TRADE_PRICE +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.StockQuotesEntry.COLUMN_STOCK_ID +  " INT NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + QuotesContract.StockQuotesEntry.COLUMN_STOCK_ID + ") REFERENCES " +
                QuotesContract.StockEntry.TABLE_NAME + " (" + QuotesContract.StockEntry._ID + ") " +
                " );";

        final String SQL_CREATE_BOND_QUOTES_TABLE = "CREATE TABLE " + QuotesContract.BondQuotesEntry.TABLE_NAME + " (" +
                QuotesContract.BondQuotesEntry._ID + " INTEGER PRIMARY KEY," +

                // the ID of the location entry associated with this weather data
                QuotesContract.BondQuotesEntry.COLUMN_CURRENCY +  " TEXT NOT NULL, " +
                QuotesContract.BondQuotesEntry.COLUMN_LAST_CHANGE +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.BondQuotesEntry.COLUMN_LAST_CHANGE_PERCENTAGE +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.BondQuotesEntry.COLUMN_LAST_TRADE_DATE +  " DATETIME NOT NULL, " +
                QuotesContract.BondQuotesEntry.COLUMN_LAST_TRADE_PRICE +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.BondQuotesEntry.COLUMN_BOND_ID +  " INT NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + QuotesContract.BondQuotesEntry.COLUMN_BOND_ID + ") REFERENCES " +
                QuotesContract.BondEntry.TABLE_NAME + " (" + QuotesContract.BondEntry._ID + ") " +
                " );";

        final String SQL_CREATE_STOCK_INTRADAY_PRICE_TABLE = "CREATE TABLE " + QuotesContract.StockIntradayPriceEntry.TABLE_NAME + " (" +
                QuotesContract.StockIntradayPriceEntry._ID + " INTEGER PRIMARY KEY," +

                QuotesContract.StockIntradayPriceEntry.COLUMN_TRADE_PRICE +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.StockIntradayPriceEntry.COLUMN_TRADE_TIME +  " DATETIME NOT NULL, " +
                QuotesContract.StockIntradayPriceEntry.COLUMN_STOCK_ID +  " INT NOT NULL, " +

                " FOREIGN KEY (" + QuotesContract.StockIntradayPriceEntry.COLUMN_STOCK_ID + ") REFERENCES " +
                QuotesContract.StockEntry.TABLE_NAME + " (" + QuotesContract.StockEntry._ID + ") " +
                " );";

        final String SQL_CREATE_BOND_INTRADAY_PRICE_TABLE = "CREATE TABLE " + QuotesContract.BondIntradayPriceEntry.TABLE_NAME + " (" +
                QuotesContract.BondIntradayPriceEntry._ID + " INTEGER PRIMARY KEY," +

                QuotesContract.BondIntradayPriceEntry.COLUMN_TRADE_PRICE +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.BondIntradayPriceEntry.COLUMN_TRADE_TIME +  " DATETIME NOT NULL, " +
                QuotesContract.BondIntradayPriceEntry.COLUMN_BOND_ID +  " INT NOT NULL, " +

                " FOREIGN KEY (" + QuotesContract.BondIntradayPriceEntry.COLUMN_BOND_ID + ") REFERENCES " +
                QuotesContract.BondEntry.TABLE_NAME + " (" + QuotesContract.BondEntry._ID + ") " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_STOCKS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BONDS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STOCK_QUOTES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BOND_QUOTES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BOND_INTRADAY_PRICE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STOCK_INTRADAY_PRICE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuotesContract.StockEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuotesContract.BondEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuotesContract.StockQuotesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuotesContract.BondQuotesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuotesContract.StockIntradayPriceEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuotesContract.BondIntradayPriceEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
