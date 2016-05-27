package com.example.matias.tprof.data;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * Created by Mati on 5/26/2016.
 */
public class SuggestionProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder suggestionQueryBuilder;
    private QuotesDbHelper mOpenHelper;
    private HashMap<String, String> mAliasMap;

    private static final int SUGGESTIONS_ASSET = 1;

    static {
        suggestionQueryBuilder = new SQLiteQueryBuilder();
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String AUTHORITY = QuotesContract.CONTENT_AUTHORITY;

        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SUGGESTIONS_ASSET);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new QuotesDbHelper(getContext());
        mAliasMap = new HashMap<String, String>();
        mAliasMap.put("_ID",  QuotesContract.StockEntry._ID + " as " + "_id" );
        mAliasMap.put(SearchManager.SUGGEST_COLUMN_TEXT_1, QuotesContract.StockEntry.COLUMN_FULLNAME + " as " + SearchManager.SUGGEST_COLUMN_TEXT_1);
        suggestionQueryBuilder.setProjectionMap(mAliasMap);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case SUGGESTIONS_ASSET: {
                suggestionQueryBuilder.setTables(QuotesContract.StockEntry.TABLE_NAME);
                String query = uri.getLastPathSegment().toLowerCase();
                if(query != null && !query.isEmpty()){
                    query = "%"+query + "%";
                    retCursor = suggestionQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                            new String[] { "_ID",
                                    SearchManager.SUGGEST_COLUMN_TEXT_1},
                            QuotesContract.StockQuotesEntry.TABLE_NAME +
                                    "." + QuotesContract.StockEntry.COLUMN_FULLNAME + " like ?",
                            new String[]{query},
                            null,
                            null,
                            sortOrder
                    );
                }else {

                    retCursor = suggestionQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                            new String[]{"_ID",
                                    SearchManager.SUGGEST_COLUMN_TEXT_1},
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder
                    );
                }
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
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }
}
