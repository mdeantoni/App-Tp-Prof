package com.example.matias.tprof.data;

import android.content.ContentValues;

/**
 * Created by Mati on 9/15/2016.
 */
public class TestUtilities {

    static ContentValues createStockValues(String symbol) {
        ContentValues stockValues = new ContentValues();
        stockValues.put(QuotesContract.StockEntry.COLUMN_FULLNAME, "RandomFullName");
        stockValues.put(QuotesContract.StockEntry.COLUMN_SYMBOL, symbol);
        return stockValues;
    }
}
