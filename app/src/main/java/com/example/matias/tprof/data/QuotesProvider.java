package com.example.matias.tprof.data;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * Created by Mati on 12/8/2015.
 */
public class QuotesProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private QuotesDbHelper mOpenHelper;
    private HashMap<String, String> mAliasMap;

    static final int STOCK = 100;
    static final int STOCK_QUOTE = 150;
    static final int BOND = 200;
    static final int BOND_QUOTE = 250;
    static final int BOND_DETAIL = 270;
    static final int STOCK_DETAIL = 290;
    static final int BOND_INTRADAY_FOR_BOND = 310;
    static final int STOCK_INTRADAY_FOR_STOCK = 330;
    static final int BOND_INTRADAY = 350;
    static final int STOCK_INTRADAY = 370;
    static final int HISTORICAL_QUOTE = 390;
    static final int ALL_SUGGESTIONS_ASSET = 490;
    static final int SUGGESTIONS_ASSET = 510;
    static final int SEARCH_RESULTS = 530;
    static final int NEWS = 550;
    static final int TRADES = 580;
    static final int HOLDINGS = 590;
    static final int VALUED_HOLDINGS = 610;
    static final int COMMENT = 630;

    private static final SQLiteQueryBuilder sStockQuotesQueryBuilder;
    private static final SQLiteQueryBuilder sBondQuotesQueryBuilder;
    private static final SQLiteQueryBuilder suggestionQueryBuilder;

    static{
        sStockQuotesQueryBuilder = new SQLiteQueryBuilder();
        sBondQuotesQueryBuilder = new SQLiteQueryBuilder();
        suggestionQueryBuilder = new SQLiteQueryBuilder();

        sStockQuotesQueryBuilder.setTables(
                QuotesContract.StockEntry.TABLE_NAME + " INNER JOIN " +
                        QuotesContract.StockQuotesEntry.TABLE_NAME +
                        " ON " + QuotesContract.StockEntry.TABLE_NAME +
                        "." + QuotesContract.StockEntry._ID +
                        " = " + QuotesContract.StockQuotesEntry.TABLE_NAME +
                        "." + QuotesContract.StockQuotesEntry.COLUMN_STOCK_ID);

        sBondQuotesQueryBuilder.setTables(
                QuotesContract.BondEntry.TABLE_NAME + " INNER JOIN " +
                        QuotesContract.BondQuotesEntry.TABLE_NAME +
                        " ON " + QuotesContract.BondEntry.TABLE_NAME +
                        "." + QuotesContract.BondEntry._ID +
                        " = " + QuotesContract.BondQuotesEntry.TABLE_NAME +
                        "." + QuotesContract.BondQuotesEntry.COLUMN_BOND_ID);
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = QuotesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, QuotesContract.PATH_BONDS, BOND);
        matcher.addURI(authority, QuotesContract.PATH_STOCKS, STOCK);
        matcher.addURI(authority, QuotesContract.PATH_STOCK_QUOTES, STOCK_QUOTE);
        matcher.addURI(authority, QuotesContract.PATH_BOND_QUOTES, BOND_QUOTE);
        matcher.addURI(authority, QuotesContract.PATH_STOCK_QUOTES + "/#", STOCK_DETAIL);
        matcher.addURI(authority, QuotesContract.PATH_BOND_QUOTES+ "/#", BOND_DETAIL);
        matcher.addURI(authority, QuotesContract.PATH_STOCK_INTRADAY_PRICES, STOCK_INTRADAY);
        matcher.addURI(authority, QuotesContract.PATH_BOND_INTRADAY_PRICES, BOND_INTRADAY);
        matcher.addURI(authority, QuotesContract.PATH_STOCK_INTRADAY_PRICES + "/#", STOCK_INTRADAY_FOR_STOCK);
        matcher.addURI(authority, QuotesContract.PATH_BOND_INTRADAY_PRICES+ "/#", BOND_INTRADAY_FOR_BOND);
        matcher.addURI(authority, QuotesContract.PATH_HISTORICAL_QUOTES, HISTORICAL_QUOTE);
        matcher.addURI(authority, SearchManager.SUGGEST_URI_PATH_QUERY , ALL_SUGGESTIONS_ASSET);
        matcher.addURI(authority, SearchManager.SUGGEST_URI_PATH_QUERY+ "/*", SUGGESTIONS_ASSET);
        matcher.addURI(authority, QuotesContract.PATH_SEARCH_RESULTS, SEARCH_RESULTS);
        matcher.addURI(authority, QuotesContract.PATH_NEWS, NEWS);
        matcher.addURI(authority, QuotesContract.PATH_TRADE, TRADES);
        matcher.addURI(authority, QuotesContract.PATH_HOLDINGS, HOLDINGS);
        matcher.addURI(authority, QuotesContract.PATH_VALUED_HOLDINGS, VALUED_HOLDINGS);
        matcher.addURI(authority, QuotesContract.PATH_COMMENTS, COMMENT);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new QuotesDbHelper(getContext());
        mAliasMap = new HashMap<String, String>();
        mAliasMap.put("_ID",  QuotesContract.SuggestionViewEntry.COLUMN_Id + " as " + "_id" );
        mAliasMap.put(SearchManager.SUGGEST_COLUMN_TEXT_1, QuotesContract.SuggestionViewEntry.COLUMN_FULLNAME + " as " + SearchManager.SUGGEST_COLUMN_TEXT_1);
        mAliasMap.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA,
                QuotesContract.SuggestionViewEntry.COLUMN_TYPE + "||" + "'-'" +"||" +
                        QuotesContract.SuggestionViewEntry.COLUMN_QUOTE_ID + "||" + "'-'" +"||" +
                        QuotesContract.SuggestionViewEntry.COLUMN_Id + "||" + "'-'" +"||" +
                        QuotesContract.SuggestionViewEntry.COLUMN_SYMBOL +
                " as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA);
        suggestionQueryBuilder.setProjectionMap(mAliasMap);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case STOCK: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        QuotesContract.StockEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case BOND: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        QuotesContract.BondEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case STOCK_QUOTE: {
                retCursor = sStockQuotesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case BOND_QUOTE: {
                retCursor = sBondQuotesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case STOCK_DETAIL: {
                String stockDetailId = QuotesContract.StockQuotesEntry.getDetailIdFrom(uri);
                retCursor = sStockQuotesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        QuotesContract.StockQuotesEntry.TABLE_NAME +
                                "." + QuotesContract.StockQuotesEntry._ID + " = ?",
                        new String[]{stockDetailId},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case BOND_DETAIL: {
                String bondDetailUri = QuotesContract.BondQuotesEntry.getDetailIdFrom(uri);
                retCursor = sBondQuotesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        QuotesContract.BondQuotesEntry.TABLE_NAME +
                                "." + QuotesContract.BondQuotesEntry._ID + " = ?",
                        new String[]{bondDetailUri},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case STOCK_INTRADAY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        QuotesContract.StockIntradayPriceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case BOND_INTRADAY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        QuotesContract.BondIntradayPriceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case STOCK_INTRADAY_FOR_STOCK: {
                String stockId = QuotesContract.StockIntradayPriceEntry.getStockIdFrom(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        QuotesContract.StockIntradayPriceEntry.TABLE_NAME,
                        projection,
                        QuotesContract.StockIntradayPriceEntry.TABLE_NAME + "." +
                        QuotesContract.StockIntradayPriceEntry.COLUMN_STOCK_ID + "= ?",
                        new String[]{stockId},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case BOND_INTRADAY_FOR_BOND: {
                String bondId = QuotesContract.BondIntradayPriceEntry.getBondIdFrom(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        QuotesContract.BondIntradayPriceEntry.TABLE_NAME,
                        projection,
                        QuotesContract.BondIntradayPriceEntry.TABLE_NAME + "." +
                                QuotesContract.BondIntradayPriceEntry.COLUMN_BOND_ID + "= ?",
                        new String[]{bondId},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case HISTORICAL_QUOTE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        QuotesContract.HistoricalQuoteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case SUGGESTIONS_ASSET: {
                suggestionQueryBuilder.setTables(QuotesContract.SuggestionViewEntry.VIEW_NAME);
                String query = uri.getLastPathSegment().toLowerCase();
                    query = "%" + query + "%";
                    retCursor = suggestionQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                            new String[]{"_ID",
                                    SearchManager.SUGGEST_COLUMN_TEXT_1,
                                    SearchManager.SUGGEST_COLUMN_INTENT_DATA},
                            QuotesContract.SuggestionViewEntry.VIEW_NAME +
                                    "." + QuotesContract.SuggestionViewEntry.COLUMN_FULLNAME + " like ?",
                            new String[]{query},
                            null,
                            null,
                            sortOrder
                    );

                    break;
                }
            case ALL_SUGGESTIONS_ASSET: {
                suggestionQueryBuilder.setTables(QuotesContract.SuggestionViewEntry.VIEW_NAME);
                    retCursor = suggestionQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                            new String[]{"_ID",
                                    SearchManager.SUGGEST_COLUMN_TEXT_1,
                                    SearchManager.SUGGEST_COLUMN_INTENT_DATA},
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder
                    );
                break;
            }
            case SEARCH_RESULTS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        QuotesContract.SearchResultsEntry.VIEW_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case NEWS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        QuotesContract.NewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TRADES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        QuotesContract.TradesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case HOLDINGS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        QuotesContract.HoldingsViewEntry.VIEW_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case VALUED_HOLDINGS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        QuotesContract.ValuedHoldingsViewEntry.VIEW_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case COMMENT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        QuotesContract.CommentsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case STOCK:
                return QuotesContract.StockEntry.CONTENT_TYPE;
            case BOND:
                return QuotesContract.BondEntry.CONTENT_TYPE;
            case STOCK_QUOTE:
                return QuotesContract.StockQuotesEntry.CONTENT_TYPE;
            case BOND_QUOTE:
                return QuotesContract.BondQuotesEntry.CONTENT_TYPE;
            case STOCK_DETAIL:
                return QuotesContract.StockQuotesEntry.CONTENT_ITEM_TYPE;
            case BOND_DETAIL:
                return QuotesContract.BondQuotesEntry.CONTENT_ITEM_TYPE;
            case STOCK_INTRADAY:
                return QuotesContract.StockIntradayPriceEntry.CONTENT_TYPE;
            case BOND_INTRADAY:
                return QuotesContract.BondIntradayPriceEntry.CONTENT_TYPE;
            case STOCK_INTRADAY_FOR_STOCK:
                return QuotesContract.StockIntradayPriceEntry.CONTENT_TYPE;
            case BOND_INTRADAY_FOR_BOND:
                return QuotesContract.BondIntradayPriceEntry.CONTENT_TYPE;
            case HISTORICAL_QUOTE:
                return QuotesContract.HistoricalQuoteEntry.CONTENT_TYPE;
            case SUGGESTIONS_ASSET:
                return QuotesContract.SuggestionViewEntry.CONTENT_TYPE;
            case NEWS:
                return QuotesContract.NewsEntry.CONTENT_TYPE;
            case TRADES:
                return QuotesContract.TradesEntry.CONTENT_TYPE;
            case HOLDINGS:
                return QuotesContract.HoldingsViewEntry.CONTENT_TYPE;
            case VALUED_HOLDINGS:
                return QuotesContract.ValuedHoldingsViewEntry.CONTENT_TYPE;
            case COMMENT:
                return QuotesContract.CommentsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case STOCK: {
                long _id = db.insert(QuotesContract.StockEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = QuotesContract.StockEntry.buildStockUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case BOND: {
                long _id = db.insert(QuotesContract.BondEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = QuotesContract.BondEntry.buildBondUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case STOCK_QUOTE: {
                long _id = db.insert(QuotesContract.StockQuotesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = QuotesContract.StockQuotesEntry.buildStockQuoteUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case BOND_QUOTE: {
                long _id = db.insert(QuotesContract.BondQuotesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = QuotesContract.BondQuotesEntry.buildBondQuoteUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case STOCK_INTRADAY: {
                long _id = db.insert(QuotesContract.StockIntradayPriceEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = QuotesContract.StockIntradayPriceEntry.buildStockIntradayPriceUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case BOND_INTRADAY: {
                long _id = db.insert(QuotesContract.BondIntradayPriceEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = QuotesContract.BondIntradayPriceEntry.buildBondIntradayPriceUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case HISTORICAL_QUOTE: {
                long _id = db.insert(QuotesContract.HistoricalQuoteEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = QuotesContract.HistoricalQuoteEntry.buildHistoricalQuoteUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case NEWS: {
                long _id = db.insert(QuotesContract.NewsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = QuotesContract.NewsEntry.buildNewsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRADES: {
                long _id = db.insert(QuotesContract.TradesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = QuotesContract.TradesEntry.buildTradeUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case COMMENT: {
                long _id = db.insert(QuotesContract.CommentsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = QuotesContract.CommentsEntry.buildCommentUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case BOND:
                rowsDeleted = db.delete(
                        QuotesContract.BondEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STOCK:
                rowsDeleted = db.delete(
                        QuotesContract.StockEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STOCK_QUOTE:
                rowsDeleted = db.delete(
                        QuotesContract.StockQuotesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOND_QUOTE:
                rowsDeleted = db.delete(
                        QuotesContract.BondQuotesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STOCK_INTRADAY:
                rowsDeleted = db.delete(
                        QuotesContract.StockIntradayPriceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOND_INTRADAY:
                rowsDeleted = db.delete(
                        QuotesContract.BondIntradayPriceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case HISTORICAL_QUOTE:
                rowsDeleted = db.delete(
                        QuotesContract.HistoricalQuoteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case NEWS:
                rowsDeleted = db.delete(
                        QuotesContract.NewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRADES:
                rowsDeleted = db.delete(
                        QuotesContract.TradesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COMMENT:
                rowsDeleted = db.delete(
                        QuotesContract.CommentsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case STOCK:
                rowsUpdated = db.update(QuotesContract.StockEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case BOND:
                rowsUpdated = db.update(QuotesContract.BondEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case STOCK_QUOTE:
                rowsUpdated = db.update(QuotesContract.StockQuotesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case BOND_QUOTE:
                rowsUpdated = db.update(QuotesContract.BondQuotesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case STOCK_INTRADAY:
                rowsUpdated = db.update(QuotesContract.StockIntradayPriceEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case BOND_INTRADAY:
                rowsUpdated = db.update(QuotesContract.BondIntradayPriceEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case HISTORICAL_QUOTE:
                rowsUpdated = db.update(QuotesContract.HistoricalQuoteEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case NEWS:
                rowsUpdated = db.update(QuotesContract.NewsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TRADES:
                rowsUpdated = db.update(QuotesContract.TradesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case COMMENT:
                rowsUpdated = db.update(QuotesContract.CommentsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case STOCK:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(QuotesContract.StockEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case BOND:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(QuotesContract.BondEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case STOCK_QUOTE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(QuotesContract.StockQuotesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case BOND_QUOTE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(QuotesContract.BondQuotesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case STOCK_INTRADAY:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(QuotesContract.StockIntradayPriceEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case BOND_INTRADAY:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(QuotesContract.BondIntradayPriceEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case HISTORICAL_QUOTE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(QuotesContract.HistoricalQuoteEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case NEWS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(QuotesContract.NewsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case TRADES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(QuotesContract.TradesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case COMMENT:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(QuotesContract.CommentsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
