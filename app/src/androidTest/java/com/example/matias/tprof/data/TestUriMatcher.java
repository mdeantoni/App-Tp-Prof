package com.example.matias.tprof.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Mati on 9/15/2016.
 */
public class TestUriMatcher extends AndroidTestCase {
    private static final long TEST_ID = 4L;

    // content://com.example.android.sunshine.app/weather"
    private static final Uri TEST_STOCK_DIR = QuotesContract.StockEntry.CONTENT_URI;
    private static final Uri TEST_BOND_DIR = QuotesContract.BondEntry.CONTENT_URI;
    private static final Uri TEST_STOCK_QUOTES_DIR = QuotesContract.StockQuotesEntry.CONTENT_URI;
    private static final Uri TEST_BOND_QUOTES_DIR = QuotesContract.BondQuotesEntry.CONTENT_URI;
    private static final Uri TEST_STOCK_DETAIL_URI = QuotesContract.StockQuotesEntry.buildStockQuoteUri(TEST_ID);
    private static final Uri TEST_BOND_DETAIL_URI = QuotesContract.BondQuotesEntry.buildBondQuoteUri(TEST_ID);
    // content://com.example.android.sunshine.app/location"


    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = QuotesProvider.buildUriMatcher();

        assertEquals("Error: The stock URI was matched incorrectly.",
                testMatcher.match(TEST_STOCK_DIR), QuotesProvider.STOCK);
        assertEquals("Error: The bond WITH LOCATION URI was matched incorrectly.",
                testMatcher.match(TEST_BOND_DIR), QuotesProvider.BOND);
        assertEquals("Error: The stock quote URI was matched incorrectly.",
                testMatcher.match(TEST_STOCK_QUOTES_DIR), QuotesProvider.STOCK_QUOTE);
        assertEquals("Error: The bond quote uURI was matched incorrectly.",
                testMatcher.match(TEST_BOND_QUOTES_DIR), QuotesProvider.BOND_QUOTE);
        assertEquals("Error: The stock detail URI was matched incorrectly.",
                testMatcher.match(TEST_STOCK_DETAIL_URI), QuotesProvider.STOCK_DETAIL);
        assertEquals("Error: The bond detail URI was matched incorrectly.",
                testMatcher.match(TEST_BOND_DETAIL_URI), QuotesProvider.BOND_DETAIL);
    }
}