package com.example.matias.tprof.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Mati on 12/8/2015.
 */
public class QuotesContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.matias.tprof";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_STOCKS = "stocks";
    public static final String PATH_BONDS = "bonds";
    public static final String PATH_STOCK_QUOTES = "stock_quotes";
    public static final String PATH_BOND_QUOTES = "bond_quotes";
    public static final String PATH_STOCK_INTRADAY_PRICES = "stock_intraday_prices";
    public static final String PATH_BOND_INTRADAY_PRICES = "bond_intraday_prices";
    public static final String PATH_STOCK_DETAILS = "stock_details";
    public static final String PATH_BOND_DETAILS = "bond_details";
    public static final String PATH_HISTORICAL_QUOTES = "historical_quotes";
    public static final String PATH_SUGGESTIONS = "suggestions";
    public static final String PATH_SEARCH_RESULTS = "search_results";

    public static final class StockEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STOCKS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCKS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCKS;

        public static final String TABLE_NAME = "Stocks";
        public static final String COLUMN_SYMBOL = "Ticker_Symbol";
        public static final String COLUMN_FULLNAME = "Fullname";

        public static Uri buildStockUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class BondEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BONDS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BONDS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BONDS;

        public static final String TABLE_NAME = "Bonds";
        public static final String COLUMN_SYMBOL = "Ticker_Symbol";
        public static final String COLUMN_FULLNAME = "Fullname";

        public static Uri buildBondUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class StockQuotesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STOCK_QUOTES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCK_QUOTES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCK_QUOTES;

        public static final String TABLE_NAME = "Stock_Quotes";
        public static final String COLUMN_LAST_TRADE_PRICE = "LastTradedPrice";
        public static final String COLUMN_CURRENCY = "Currency";
        public static final String COLUMN_LAST_CHANGE_PERCENTAGE = "LastChangeInPricePercentage";
        public static final String COLUMN_LAST_CHANGE = "LastChangeInPrice";
        public static final String COLUMN_LAST_TRADE_DATE = "LastTradeDate";
        public static final String COLUMN_DAYS_LOW ="DaysLow";
        public static final String COLUMN_DAYS_HIGH = "DaysHigh";
        public static final String COLUMN_YEAR_LOW= "YearLow";
        public static final String COLUMN_YEAR_HIGH = "YearHigh";
        public static final String COLUMN_PREVIOUS_CLOSE = "PreviousClose";
        public static final String COLUMN_OPEN = "Open";
        public static final String COLUMN_VOLUME = "Volume";
        public static final String COLUMN_AVG_VOLUME = "AverageDailyVolume";
        public static final String COLUMN_MKT_CAP= "MarketCapitalization";
        public static final String COLUMN_PRICE_SALES = "PriceOverSales";
        public static final String COLUMN_PRICE_BOOK = "PriceOverBookValue";
        public static final String COLUMN_PRICE_EARNINGS=  "PriceEarningsRatio";
        public static final String COLUMN_STOCK_ID = "StockId";

        public static Uri buildStockQuoteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getDetailIdFrom(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class BondQuotesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOND_QUOTES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOND_QUOTES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOND_QUOTES;

        public static final String TABLE_NAME = "Bond_Quotes";
        public static final String COLUMN_LAST_TRADE_PRICE = "LastTradedPrice";
        public static final String COLUMN_CURRENCY = "Currency";
        public static final String COLUMN_LAST_CHANGE_PERCENTAGE = "LastChangeInPricePercentage";
        public static final String COLUMN_LAST_CHANGE = "LastChangeInPrice";
        public static final String COLUMN_LAST_TRADE_DATE = "LastTradeDate";
        public static final String COLUMN_DAYS_LOW ="DaysLow";
        public static final String COLUMN_DAYS_HIGH = "DaysHigh";
        public static final String COLUMN_YEAR_LOW= "YearLow";
        public static final String COLUMN_YEAR_HIGH = "YearHigh";
        public static final String COLUMN_PREVIOUS_CLOSE = "PreviousClose";
        public static final String COLUMN_OPEN = "Open";
        public static final String COLUMN_VOLUME = "Volume";
        public static final String COLUMN_AVG_VOLUME = "AverageDailyVolume";
        public static final String COLUMN_IIR = "IRR";
        public static final String COLUMN_PARITY = "Parity";
        public static final String COLUMN_BOND_ID = "BondId";

        public static Uri buildBondQuoteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getDetailIdFrom(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class StockIntradayPriceEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STOCK_INTRADAY_PRICES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCK_INTRADAY_PRICES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCK_INTRADAY_PRICES;

        public static final String TABLE_NAME = "Stock_Intraday_Price";
        public static final String COLUMN_TRADE_PRICE = "TradePrice";
        public static final String COLUMN_TRADE_TIME = "TradeTime";
        public static final String COLUMN_STOCK_ID = "StockId";

        public static Uri buildStockIntradayPriceUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getStockIdFrom(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class BondIntradayPriceEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOND_INTRADAY_PRICES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOND_INTRADAY_PRICES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOND_INTRADAY_PRICES;

        public static final String TABLE_NAME = "Bonds_Intraday_Price";
        public static final String COLUMN_TRADE_PRICE = "TradePrice";
        public static final String COLUMN_TRADE_TIME = "TradeTime";
        public static final String COLUMN_BOND_ID = "BondId";

        public static Uri buildBondIntradayPriceUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getBondIdFrom(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class HistoricalQuoteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HISTORICAL_QUOTES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORICAL_QUOTES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORICAL_QUOTES;

        public static final String TABLE_NAME = "Historical_Quotes";
        public static final String COLUMN_TICKER_SYMBOL = "TickerSymbol";
        public static final String COLUMN_CLOSE_PRICE = "ClosePrice";
        public static final String COLUMN_VOLUME = "Volume";
        public static final String COLUMN_DATE = "Date";

        public static Uri buildHistoricalQuoteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class SuggestionViewEntry {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUGGESTIONS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUGGESTIONS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUGGESTIONS;

        public static final String VIEW_NAME = "SuggestionsView";
        public static final String COLUMN_SYMBOL = "Ticker_Symbol";
        public static final String COLUMN_FULLNAME = "Fullname";
        public static final String COLUMN_TYPE = "Type";
        public static final String COLUMN_Id = "Id";
    }

    public static final class SearchResultsEntry {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEARCH_RESULTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SEARCH_RESULTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SEARCH_RESULTS;

        public static final String VIEW_NAME = "SearchResultsView";
        public static final String COLUMN_SYMBOL = "Ticker_Symbol";
        public static final String COLUMN_FULLNAME = "Fullname";
        public static final String COLUMN_LAST_CHANGE_PERCENTAGE = "LastChangeInPricePercentage";
        public static final String COLUMN_LAST_CHANGE = "LastChangeInPrice";
        public static final String COLUMN_LAST_TRADE_DATE = "LastTradeDate";
        public static final String COLUMN_LAST_TRADE_PRICE = "LastTradedPrice";
        public static final String COLUMN_TYPE = "Type";
        public static final String COLUMN_Id = "Id";
    }
}
