package com.example.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class StockDetailsLoader extends AsyncTask<String,Void,String>  {

    private MainActivity mainActivity;
    private StocksToDisplay stocksToDisplay;
    StockDetailsLoader(MainActivity ma){
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... strings) {
        String queryURL = "https://cloud.iexapis.com/stable/stock/" + strings[0] +"/quote?token=pk_7ebda2ec18cf4e07b5fb108085cf2b49";
        Uri uri = Uri.parse(queryURL);
        String actualURL = uri.toString();
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(actualURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            if(httpURLConnection.getResponseCode() == 200){
                httpURLConnection.setRequestMethod("GET");
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(inputStream)));
                String endOfLine;
                while ((endOfLine = bufferedReader.readLine()) != null){
                    sb.append(endOfLine).append("\n");
                }
            }
        }catch (Exception e){
            return null;
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String string){
        super.onPostExecute(string);
        StocksToDisplay detailedStock = parseToJSON(string);
        mainActivity.addStockToTheDataSetToBeDisplayed(detailedStock);
    }

    private StocksToDisplay parseToJSON(String string) {
        try {
            JSONObject individualStockObject = new JSONObject(string);
            String name = individualStockObject.getString("companyName");
            String symbol = individualStockObject.getString("symbol");
            Double latestPrice = individualStockObject.getDouble("latestPrice");
            Double change = individualStockObject.getDouble("change");
            Double percentChange = individualStockObject.getDouble("changePercent");
            StocksToDisplay newItem = new StocksToDisplay(name,symbol,latestPrice,change,percentChange);
            return newItem;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
