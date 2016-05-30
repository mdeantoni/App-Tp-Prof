package com.example.matias.tprof.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mati on 12/8/2015.
 */
public class QuotesDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 18;

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
                QuotesContract.StockQuotesEntry.COLUMN_DAYS_LOW +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.StockQuotesEntry.COLUMN_DAYS_HIGH +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.StockQuotesEntry.COLUMN_YEAR_HIGH +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.StockQuotesEntry.COLUMN_YEAR_LOW +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.StockQuotesEntry.COLUMN_VOLUME +  " INTEGER NOT NULL, " +
                QuotesContract.StockQuotesEntry.COLUMN_AVG_VOLUME +  " INTEGER DEFAULT NULL, " +
                QuotesContract.StockQuotesEntry.COLUMN_OPEN +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.StockQuotesEntry.COLUMN_PREVIOUS_CLOSE +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.StockQuotesEntry.COLUMN_MKT_CAP +  " TEXT , " +
                QuotesContract.StockQuotesEntry.COLUMN_PRICE_SALES +  " TEXT, " +
                QuotesContract.StockQuotesEntry.COLUMN_PRICE_EARNINGS +  " TEXT, " +
                QuotesContract.StockQuotesEntry.COLUMN_PRICE_BOOK +  " TEXT, " +
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
                QuotesContract.BondQuotesEntry.COLUMN_DAYS_LOW +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.BondQuotesEntry.COLUMN_DAYS_HIGH +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.BondQuotesEntry.COLUMN_YEAR_HIGH +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.BondQuotesEntry.COLUMN_YEAR_LOW +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.BondQuotesEntry.COLUMN_VOLUME +  " INTEGER NOT NULL, " +
                QuotesContract.BondQuotesEntry.COLUMN_AVG_VOLUME +  " INTEGER DEFAULT NULL, " +
                QuotesContract.BondQuotesEntry.COLUMN_OPEN +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.BondQuotesEntry.COLUMN_PREVIOUS_CLOSE +  " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.BondQuotesEntry.COLUMN_IIR +  " TEXT, " +
                QuotesContract.BondQuotesEntry.COLUMN_PARITY +  " TEXT, " +
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

        final String SQL_CREATE_HISTORICAL_QUOTE_TABLE = "CREATE TABLE " + QuotesContract.HistoricalQuoteEntry.TABLE_NAME + " (" +
                QuotesContract.HistoricalQuoteEntry._ID + " INTEGER PRIMARY KEY," +

                QuotesContract.HistoricalQuoteEntry.COLUMN_TICKER_SYMBOL +  " TEXT NOT NULL, " +
                QuotesContract.HistoricalQuoteEntry.COLUMN_VOLUME +  " INT NOT NULL, " +
                QuotesContract.HistoricalQuoteEntry.COLUMN_CLOSE_PRICE +   " DECIMAL(6,2) NOT NULL, " +
                QuotesContract.HistoricalQuoteEntry.COLUMN_DATE + " DATETIME NOT NULL " +
                " );";

        final String SQL_CREATE_SUGGESTIONS_VIEW = "CREATE VIEW " + QuotesContract.SuggestionViewEntry.VIEW_NAME + " AS " +
                "SELECT " +
                QuotesContract.StockEntry._ID + " AS " + QuotesContract.SuggestionViewEntry.COLUMN_Id + "," +
                QuotesContract.StockEntry.COLUMN_FULLNAME + " AS " + QuotesContract.SuggestionViewEntry.COLUMN_FULLNAME + "," +
                QuotesContract.StockEntry.COLUMN_SYMBOL + " AS " + QuotesContract.SuggestionViewEntry.COLUMN_SYMBOL + "," +
                "'Stock'  AS " + QuotesContract.SuggestionViewEntry.COLUMN_TYPE  +
                " FROM " + QuotesContract.StockEntry.TABLE_NAME +
                " UNION " +
                "SELECT " +
                QuotesContract.BondEntry._ID + " AS " + QuotesContract.SuggestionViewEntry.COLUMN_Id + "," +
                QuotesContract.BondEntry.COLUMN_FULLNAME + " AS " + QuotesContract.SuggestionViewEntry.COLUMN_FULLNAME + "," +
                QuotesContract.BondEntry.COLUMN_SYMBOL + " AS " + QuotesContract.SuggestionViewEntry.COLUMN_SYMBOL + "," +
                "'Bond'  AS " + QuotesContract.SuggestionViewEntry.COLUMN_TYPE +
                 " FROM " + QuotesContract.BondEntry.TABLE_NAME;

        final String SQL_CREATE_SEARCH_RESULTS_VIEW = "CREATE VIEW " + QuotesContract.SearchResultsEntry.VIEW_NAME + " AS " +
                "SELECT " +
                QuotesContract.StockEntry.TABLE_NAME + "." + QuotesContract.StockEntry._ID + " AS " + QuotesContract.SearchResultsEntry.COLUMN_Id + "," +
                QuotesContract.StockEntry.COLUMN_FULLNAME + " AS " + QuotesContract.SearchResultsEntry.COLUMN_FULLNAME + "," +
                QuotesContract.StockEntry.COLUMN_SYMBOL + " AS " + QuotesContract.SearchResultsEntry.COLUMN_SYMBOL + "," +
                QuotesContract.StockQuotesEntry.COLUMN_LAST_TRADE_DATE + " AS " + QuotesContract.SearchResultsEntry.COLUMN_LAST_TRADE_DATE + "," +
                QuotesContract.StockQuotesEntry.COLUMN_LAST_TRADE_PRICE + " AS " + QuotesContract.SearchResultsEntry.COLUMN_LAST_TRADE_PRICE + "," +
                QuotesContract.StockQuotesEntry.COLUMN_LAST_CHANGE + " AS " + QuotesContract.SearchResultsEntry.COLUMN_LAST_CHANGE + "," +
                QuotesContract.StockQuotesEntry.COLUMN_LAST_CHANGE_PERCENTAGE + " AS " + QuotesContract.SearchResultsEntry.COLUMN_LAST_CHANGE_PERCENTAGE + "," +
                "'Stock'  AS " + QuotesContract.SearchResultsEntry.COLUMN_TYPE  +
                " FROM " + QuotesContract.StockEntry.TABLE_NAME + " INNER JOIN "+ QuotesContract.StockQuotesEntry.TABLE_NAME + " ON " +
                QuotesContract.StockQuotesEntry.TABLE_NAME + "." + QuotesContract.StockQuotesEntry.COLUMN_STOCK_ID +
                "=" + QuotesContract.StockEntry.TABLE_NAME + "." + QuotesContract.StockEntry._ID +
                " UNION " +
                "SELECT " +
                QuotesContract.BondEntry.TABLE_NAME + "." +QuotesContract.BondEntry._ID + " AS " + QuotesContract.SuggestionViewEntry.COLUMN_Id + "," +
                QuotesContract.BondEntry.COLUMN_FULLNAME + " AS " + QuotesContract.SuggestionViewEntry.COLUMN_FULLNAME + "," +
                QuotesContract.BondEntry.COLUMN_SYMBOL + " AS " + QuotesContract.SuggestionViewEntry.COLUMN_SYMBOL + "," +
                QuotesContract.BondQuotesEntry.COLUMN_LAST_TRADE_DATE + " AS " + QuotesContract.SearchResultsEntry.COLUMN_LAST_TRADE_DATE + "," +
                QuotesContract.BondQuotesEntry.COLUMN_LAST_TRADE_PRICE + " AS " + QuotesContract.SearchResultsEntry.COLUMN_LAST_TRADE_PRICE + "," +
                QuotesContract.BondQuotesEntry.COLUMN_LAST_CHANGE + " AS " + QuotesContract.SearchResultsEntry.COLUMN_LAST_CHANGE + "," +
                QuotesContract.BondQuotesEntry.COLUMN_LAST_CHANGE_PERCENTAGE + " AS " + QuotesContract.SearchResultsEntry.COLUMN_LAST_CHANGE_PERCENTAGE + "," +
                "'Bond'  AS " + QuotesContract.SuggestionViewEntry.COLUMN_TYPE +
                " FROM " + QuotesContract.BondEntry.TABLE_NAME + " INNER JOIN "+ QuotesContract.BondQuotesEntry.TABLE_NAME + " ON " +
                QuotesContract.BondQuotesEntry.TABLE_NAME + "." + QuotesContract.BondQuotesEntry.COLUMN_BOND_ID +
                "=" + QuotesContract.BondEntry.TABLE_NAME + "." + QuotesContract.BondEntry._ID ;

        sqLiteDatabase.execSQL(SQL_CREATE_STOCKS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BONDS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STOCK_QUOTES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BOND_QUOTES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BOND_INTRADAY_PRICE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STOCK_INTRADAY_PRICE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_HISTORICAL_QUOTE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SUGGESTIONS_VIEW);
        sqLiteDatabase.execSQL(SQL_CREATE_SEARCH_RESULTS_VIEW);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuotesContract.StockEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuotesContract.BondEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuotesContract.StockQuotesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuotesContract.BondQuotesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuotesContract.StockIntradayPriceEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuotesContract.BondIntradayPriceEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuotesContract.HistoricalQuoteEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP VIEW IF EXISTS " + QuotesContract.SuggestionViewEntry.VIEW_NAME);
        sqLiteDatabase.execSQL("DROP VIEW IF EXISTS " + QuotesContract.SearchResultsEntry.VIEW_NAME);
        onCreate(sqLiteDatabase);
    }
}
