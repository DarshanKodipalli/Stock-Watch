package com.example.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseUtility extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SWDB";
    private static final String TABLE = "SWTable";
    private static final String STOCK_SYMBOL = "symbol";
    private static final String COMPANY_NAME = "name";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLECREATE = "CREATE TABLE " + TABLE + " (" +
                                                                            STOCK_SYMBOL + " TEXT not null unique," +
                                                                            COMPANY_NAME+ " TEXT not null"+
                                                                        ")";
    private SQLiteDatabase database;

    public DatabaseUtility(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLECREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<StocksToDisplay> loadAllSavedStocks() {
        ArrayList<StocksToDisplay> stocks = new ArrayList<>();
        Cursor cursor = database.query(
                TABLE, // The table to query
                new String[]{STOCK_SYMBOL, COMPANY_NAME}, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null,
                null
        ); // don't filter by row groups
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String name = cursor.getString(1);
                StocksToDisplay s = new StocksToDisplay();
                s.setSymbol(symbol);
                s.setName(name);
                s.setLatestPrice(0.0);
                s.setChange(0.0);
                s.setChangePercentage(0.0);
                stocks.add(s);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stocks;
    }

    void addStockToDB(StocksToDisplay detailedStock) {
        ContentValues values = new ContentValues();
        values.put(STOCK_SYMBOL, detailedStock.getSymbol());
        values.put(COMPANY_NAME, detailedStock.getName());
        try {
            database.insert(TABLE, null, values);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteStock(String symbol) {
        int cnt = database.delete(TABLE, STOCK_SYMBOL + " = ?", new String[]{symbol});
    }

    public void shutDown() {
        database.close();
    }
}
