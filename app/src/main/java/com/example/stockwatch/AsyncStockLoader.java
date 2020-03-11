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

public class AsyncStockLoader extends AsyncTask<String, Integer, String> {

    private MainActivity mainActivity;
    private static final String data_url = "https://api.iextrading.com/1.0/ref-data/symbols";

    AsyncStockLoader(MainActivity ma){
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... strings) {
        Uri uri = Uri.parse(data_url);
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
        ArrayList<Stocks> allStocks = parseToJSON(string);
        mainActivity.updateStocksData(allStocks);
    }

    private ArrayList<Stocks> parseToJSON(String string) {
        ArrayList<Stocks> allStocks= new ArrayList<>();
        try {
            JSONArray stockObject = new JSONArray(string);
            for (int i=0;i<stockObject.length();i++){
                JSONObject individualStockObject = (JSONObject) stockObject.get(i);
                String name = individualStockObject.getString("name");
                String symbol = individualStockObject.getString("symbol");
                String date = individualStockObject.getString("date");
                String type = individualStockObject.getString("type");
                String id = individualStockObject.getString("iexId");
                Stocks newItem = new Stocks(name,symbol,date,id,type);
                if(name.length()==0){

                }else {
                    allStocks.add(newItem);
                }
            }
            return allStocks;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
