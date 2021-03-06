package com.example.matias.tprof.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mati on 12/8/2015.
 */
public class QuotesDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 35;

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
                QuotesContract.StockEntry.TABLE_NAME + "." +QuotesContract.StockEntry._ID + " AS " + QuotesContract.SuggestionViewEntry.COLUMN_Id + "," +
                QuotesContract.StockEntry.COLUMN_FULLNAME + " AS " + QuotesContract.SuggestionViewEntry.COLUMN_FULLNAME + "," +
                QuotesContract.StockEntry.COLUMN_SYMBOL + " AS " + QuotesContract.SuggestionViewEntry.COLUMN_SYMBOL + "," +
                QuotesContract.StockQuotesEntry.TABLE_NAME + "." +QuotesContract.StockQuotesEntry._ID + " AS " + QuotesContract.SuggestionViewEntry.COLUMN_QUOTE_ID + "," +
                "'Stock'  AS " + QuotesContract.SuggestionViewEntry.COLUMN_TYPE  +
                " FROM " + QuotesContract.StockEntry.TABLE_NAME + " INNER JOIN "+ QuotesContract.StockQuotesEntry.TABLE_NAME + " ON " +
                QuotesContract.StockQuotesEntry.TABLE_NAME + "." + QuotesContract.StockQuotesEntry.COLUMN_STOCK_ID +
                "=" + QuotesContract.StockEntry.TABLE_NAME + "." + QuotesContract.StockEntry._ID +
                " UNION " +
                "SELECT " +
                QuotesContract.BondEntry.TABLE_NAME + "." +QuotesContract.BondEntry._ID + " AS " + QuotesContract.SuggestionViewEntry.COLUMN_Id + "," +
                QuotesContract.BondEntry.COLUMN_FULLNAME + " AS " + QuotesContract.SuggestionViewEntry.COLUMN_FULLNAME + "," +
                QuotesContract.BondEntry.COLUMN_SYMBOL + " AS " + QuotesContract.SuggestionViewEntry.COLUMN_SYMBOL + "," +
                QuotesContract.BondQuotesEntry.TABLE_NAME + "." +QuotesContract.BondQuotesEntry._ID + " AS " + QuotesContract.SuggestionViewEntry.COLUMN_QUOTE_ID + "," +
                "'Bond'  AS " + QuotesContract.SuggestionViewEntry.COLUMN_TYPE +
                 " FROM " + QuotesContract.BondEntry.TABLE_NAME + " INNER JOIN "+ QuotesContract.BondQuotesEntry.TABLE_NAME + " ON " +
                QuotesContract.BondQuotesEntry.TABLE_NAME + "." + QuotesContract.BondQuotesEntry.COLUMN_BOND_ID +
                "=" + QuotesContract.BondEntry.TABLE_NAME + "." + QuotesContract.BondEntry._ID ;

        final String SQL_CREATE_SEARCH_RESULTS_VIEW = "CREATE VIEW " + QuotesContract.SearchResultsEntry.VIEW_NAME + " AS " +
                "SELECT " +
                QuotesContract.StockEntry.TABLE_NAME + "." + QuotesContract.StockEntry._ID + " AS " + QuotesContract.SearchResultsEntry.COLUMN_Id + "," +
                QuotesContract.StockEntry.COLUMN_FULLNAME + " AS " + QuotesContract.SearchResultsEntry.COLUMN_FULLNAME + "," +
                QuotesContract.StockEntry.COLUMN_SYMBOL + " AS " + QuotesContract.SearchResultsEntry.COLUMN_SYMBOL + "," +
                QuotesContract.StockQuotesEntry.COLUMN_LAST_TRADE_DATE + " AS " + QuotesContract.SearchResultsEntry.COLUMN_LAST_TRADE_DATE + "," +
                QuotesContract.StockQuotesEntry.COLUMN_LAST_TRADE_PRICE + " AS " + QuotesContract.SearchResultsEntry.COLUMN_LAST_TRADE_PRICE + "," +
                QuotesContract.StockQuotesEntry.COLUMN_LAST_CHANGE + " AS " + QuotesContract.SearchResultsEntry.COLUMN_LAST_CHANGE + "," +
                QuotesContract.StockQuotesEntry.COLUMN_LAST_CHANGE_PERCENTAGE + " AS " + QuotesContract.SearchResultsEntry.COLUMN_LAST_CHANGE_PERCENTAGE + "," +
                QuotesContract.StockQuotesEntry.COLUMN_CURRENCY + " AS " + QuotesContract.SearchResultsEntry.COLUMN_CURRENCY + "," +
                QuotesContract.StockQuotesEntry.TABLE_NAME + "." + QuotesContract.StockQuotesEntry._ID + " AS " + QuotesContract.SearchResultsEntry.COLUMN_QUOTE_ID + "," +
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
                QuotesContract.BondQuotesEntry.COLUMN_CURRENCY + " AS " + QuotesContract.SearchResultsEntry.COLUMN_CURRENCY + "," +
                QuotesContract.BondQuotesEntry.TABLE_NAME + "." + QuotesContract.BondQuotesEntry._ID + " AS " + QuotesContract.SearchResultsEntry.COLUMN_QUOTE_ID + "," +
                "'Bond'  AS " + QuotesContract.SuggestionViewEntry.COLUMN_TYPE +
                " FROM " + QuotesContract.BondEntry.TABLE_NAME + " INNER JOIN "+ QuotesContract.BondQuotesEntry.TABLE_NAME + " ON " +
                QuotesContract.BondQuotesEntry.TABLE_NAME + "." + QuotesContract.BondQuotesEntry.COLUMN_BOND_ID +
                "=" + QuotesContract.BondEntry.TABLE_NAME + "." + QuotesContract.BondEntry._ID ;

        final String SQL_CREATE_NEWS_TABLE = "CREATE TABLE " + QuotesContract.NewsEntry.TABLE_NAME + " (" +
                QuotesContract.NewsEntry._ID + " INTEGER PRIMARY KEY," +
                QuotesContract.NewsEntry.COLUMN_DATE +" DATETIME NOT NULL, " +
                QuotesContract.NewsEntry.COLUMN_HEADLINE + " TEXT NOT NULL, " +
                QuotesContract.NewsEntry.COLUMN_SOURCE + " TEXT NOT NULL, " +
                QuotesContract.NewsEntry.COLUMN_TAGS + " TEXT NOT NULL, " +
                QuotesContract.NewsEntry.COLUMN_URL + " TEXT NOT NULL " +
                " );";


        final String SQL_CREATE_TRADES_TABLE = "CREATE TABLE " + QuotesContract.TradesEntry.TABLE_NAME + " (" +
                QuotesContract.TradesEntry._ID + " INTEGER PRIMARY KEY," +
                QuotesContract.TradesEntry.COLUMN_DATE +" DATETIME NOT NULL, " +
                QuotesContract.TradesEntry.COLUMN_OPERATION + " TEXT NOT NULL, " +
                QuotesContract.TradesEntry.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                QuotesContract.TradesEntry.COLUMN_QUANTITY + " INT NOT NULL, " +
                QuotesContract.TradesEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                QuotesContract.TradesEntry.COLUMN_FULLNAME + " TEXT NOT NULL, " +
                QuotesContract.TradesEntry.COLUMN_PRICE + " DECIMAL(6,2) NOT NULL " +
                " );";

        final String SQL_CREATE_HOLDINGS_VIEW = "CREATE VIEW " + QuotesContract.HoldingsViewEntry.VIEW_NAME + " AS " +
                "SELECT " + QuotesContract.TradesEntry.COLUMN_SYMBOL + " as " + QuotesContract.HoldingsViewEntry.COLUMN_SYMBOL + " , " +
                "SUM( CASE WHEN " + QuotesContract.TradesEntry.COLUMN_OPERATION + " =  'Compra' THEN "
                + QuotesContract.TradesEntry.COLUMN_QUANTITY + " ELSE -1 * " + QuotesContract.TradesEntry.COLUMN_QUANTITY + " END) AS " + QuotesContract.HoldingsViewEntry.COLUMN_QUANTITY + " , " +
                QuotesContract.TradesEntry.COLUMN_TYPE + " AS " + QuotesContract.HoldingsViewEntry.COLUMN_TYPE + " " +
                "FROM " + QuotesContract.TradesEntry.TABLE_NAME + " " +
                "GROUP BY " + QuotesContract.TradesEntry.COLUMN_SYMBOL + " , " + QuotesContract.TradesEntry.COLUMN_TYPE + " " +
                "HAVING SUM( CASE WHEN " + QuotesContract.TradesEntry.COLUMN_OPERATION + " =  'Compra' THEN "
                + QuotesContract.TradesEntry.COLUMN_QUANTITY + " ELSE -1 * " + QuotesContract.TradesEntry.COLUMN_QUANTITY + " END) > 0";

        final String SQL_CREATE_VALUED_HOLDINGS_VIEW = "CREATE VIEW " + QuotesContract.ValuedHoldingsViewEntry.VIEW_NAME + " AS " +
                "SELECT " + QuotesContract.HoldingsViewEntry.VIEW_NAME + "." + QuotesContract.HoldingsViewEntry.COLUMN_SYMBOL + " AS " + QuotesContract.ValuedHoldingsViewEntry.COLUMN_SYMBOL + " , " +
                QuotesContract.HoldingsViewEntry.VIEW_NAME + "." + QuotesContract.HoldingsViewEntry.COLUMN_TYPE + " AS " + QuotesContract.ValuedHoldingsViewEntry.COLUMN_TYPE + " , " +
                QuotesContract.HoldingsViewEntry.VIEW_NAME + "." + QuotesContract.HoldingsViewEntry.COLUMN_QUANTITY + " AS " + QuotesContract.ValuedHoldingsViewEntry.COLUMN_QUANTITY + " , " +
                QuotesContract.SearchResultsEntry.VIEW_NAME + "." + QuotesContract.SearchResultsEntry.COLUMN_LAST_TRADE_PRICE + " AS " + QuotesContract.ValuedHoldingsViewEntry.COLUMN_CURRENT_PRICE + " , " +
                QuotesContract.SearchResultsEntry.VIEW_NAME + "." + QuotesContract.SearchResultsEntry.COLUMN_LAST_TRADE_PRICE + " * " +
                QuotesContract.HoldingsViewEntry.VIEW_NAME + "." + QuotesContract.HoldingsViewEntry.COLUMN_QUANTITY + " AS " + QuotesContract.ValuedHoldingsViewEntry.COLUMN_TOTAL_VALUE + "  " +
                " FROM " + QuotesContract.HoldingsViewEntry.VIEW_NAME + " INNER JOIN "+ QuotesContract.SearchResultsEntry.VIEW_NAME + " ON " +
                QuotesContract.HoldingsViewEntry.VIEW_NAME + "." + QuotesContract.HoldingsViewEntry.COLUMN_SYMBOL +
                "=" + QuotesContract.SearchResultsEntry.VIEW_NAME + "." + QuotesContract.SearchResultsEntry.COLUMN_SYMBOL;

        final String SQL_CREATE_COMMENTS_TABLE = "CREATE TABLE " + QuotesContract.CommentsEntry.TABLE_NAME + " (" +
                QuotesContract.CommentsEntry._ID + " INTEGER PRIMARY KEY," +
                QuotesContract.CommentsEntry.COLUMN_IDENTIFIER + " INTEGER NOT NULL," +
                QuotesContract.CommentsEntry.COLUMN_DATE +" DATETIME NOT NULL, " +
                QuotesContract.CommentsEntry.COLUMN_USERNAME + " TEXT NOT NULL, " +
                QuotesContract.CommentsEntry.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                QuotesContract.CommentsEntry.COLUMN_COMMENT + " TEXT NOT NULL " +
                " );";



        sqLiteDatabase.execSQL(SQL_CREATE_STOCKS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BONDS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STOCK_QUOTES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BOND_QUOTES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BOND_INTRADAY_PRICE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STOCK_INTRADAY_PRICE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_HISTORICAL_QUOTE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SUGGESTIONS_VIEW);
        sqLiteDatabase.execSQL(SQL_CREATE_SEARCH_RESULTS_VIEW);
        sqLiteDatabase.execSQL(SQL_CREATE_NEWS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRADES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_HOLDINGS_VIEW);
        sqLiteDatabase.execSQL(SQL_CREATE_VALUED_HOLDINGS_VIEW);
        sqLiteDatabase.execSQL(SQL_CREATE_COMMENTS_TABLE);
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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuotesContract.NewsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuotesContract.TradesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP VIEW IF EXISTS " + QuotesContract.HoldingsViewEntry.VIEW_NAME);
        sqLiteDatabase.execSQL("DROP VIEW IF EXISTS " + QuotesContract.ValuedHoldingsViewEntry.VIEW_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuotesContract.CommentsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
