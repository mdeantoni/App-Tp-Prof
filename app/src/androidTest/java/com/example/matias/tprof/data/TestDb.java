package com.example.matias.tprof.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by Mati on 9/15/2016.
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(QuotesDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(QuotesContract.StockEntry.TABLE_NAME);
        tableNameHashSet.add(QuotesContract.BondEntry.TABLE_NAME);
        tableNameHashSet.add(QuotesContract.StockQuotesEntry.TABLE_NAME);
        tableNameHashSet.add(QuotesContract.BondQuotesEntry.TABLE_NAME);
        tableNameHashSet.add(QuotesContract.BondIntradayPriceEntry.TABLE_NAME);
        tableNameHashSet.add(QuotesContract.StockIntradayPriceEntry.TABLE_NAME);
        tableNameHashSet.add(QuotesContract.HistoricalQuoteEntry.TABLE_NAME);
        tableNameHashSet.add(QuotesContract.NewsEntry.TABLE_NAME);
        tableNameHashSet.add(QuotesContract.TradesEntry.TABLE_NAME);
        tableNameHashSet.add(QuotesContract.CommentsEntry.TABLE_NAME);

        mContext.deleteDatabase(QuotesDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new QuotesDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without some tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + QuotesContract.StockEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(QuotesContract.StockEntry._ID);
        locationColumnHashSet.add(QuotesContract.StockEntry.COLUMN_FULLNAME);
        locationColumnHashSet.add(QuotesContract.StockEntry.COLUMN_SYMBOL);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required stock entry entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    public void testStockEntryTable() {
            QuotesDbHelper dbHelper = new QuotesDbHelper(mContext);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Second Step: Create ContentValues of what you want to insert
            // (you can use the createNorthPoleLocationValues if you wish)
            ContentValues testValues = TestUtilities.createStockValues("APBR");

            // Third Step: Insert ContentValues into database and get a row ID back
            long rowId;
            rowId = db.insert(QuotesContract.StockEntry.TABLE_NAME, null, testValues);

            // Verify we got a row back.
            assertTrue(rowId != -1);

            // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
            // the round trip.

            // Fourth Step: Query the database and receive a Cursor back
            // A cursor is your primary interface to the query results.
            Cursor cursor = db.query(
                    QuotesContract.StockEntry.TABLE_NAME,  // Table to Query
                    null, // all columns
                    null, // Columns for the "where" clause
                    null, // Values for the "where" clause
                    null, // columns to group by
                    null, // columns to filter by row groups
                    null // sort order
            );

            // Move the cursor to a valid database row and check to see if we got any records back
            // from the query
            assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

            cursor.moveToFirst();

            assertEquals("Returned value for symbol does not equal expected value", "APBR", cursor.getString(cursor.getColumnIndex(QuotesContract.StockEntry.COLUMN_SYMBOL)));

            // Move the cursor to demonstrate that there is only one record in the database
            assertFalse( "Error: More than one record returned from location query",
                    cursor.moveToNext() );

            // Sixth Step: Close Cursor and Database
            cursor.close();
            db.close();
        }


}
