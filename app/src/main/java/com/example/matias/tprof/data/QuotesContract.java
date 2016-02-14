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
        public static final String COLUMN_BOND_ID = "BondId";

        public static Uri buildBondQuoteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getDetailIdFrom(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
