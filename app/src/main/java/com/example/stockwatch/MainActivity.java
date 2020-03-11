package com.example.stockwatch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private String extendedInfoAbooutStock="http://www.marketwatch.com/investing/stock/";
    private ArrayList<Stocks> allStocksList = new ArrayList<>();
    private ArrayList<Stocks> selectedStocksForDisplay = new ArrayList<>();
    private ArrayList<Stocks> finalStocksForDisplay = new ArrayList<>();
    private ArrayList<StocksToDisplay> displayedStocks = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseUtility sqlDatabase;
    private StockDisplayAdapter stockDisplayAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<StocksToDisplay> tempStocks = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        executeThis();
    }
    public void executeThis(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.baseline_insert_chart_outlined_24);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerViewID);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshsLayout);

        stockDisplayAdapter = new StockDisplayAdapter(this,displayedStocks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this  ));
        recyclerView.setAdapter(stockDisplayAdapter);

        swipeRefreshLayout.setProgressViewOffset(true,0,150);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isDeviceOnline()) {
                    swipeRefreshLayout.setRefreshing(false);
                    networkConnectionErrorDialog();
                } else {
                    if(displayedStocks.size() == 0){
                        displayNoDataToRefreshToast();
                    }else {
                        reloadDataOnRefresh();
                    }
                }
            }
        });
        sqlDatabase = new DatabaseUtility(this);
        new AsyncStockLoader(this).execute();
        ArrayList<StocksToDisplay> tempList = sqlDatabase.loadAllSavedStocks();
        for(int i=0;i<tempList.size();i++){
            Log.d("MainActivity", "onCreate: "+tempList.get(i).getSymbol()+" "+tempList.get(i).getLatestPrice());
        }
        if(tempList.size()==0){
            Toast.makeText(this,"No Stocks to Display. Add one to see on the list", Toast.LENGTH_LONG).show();
        }
        if(isDeviceOnline()){
            displayedStocks.clear();
            for (int i = 0; i < tempList.size(); i++) {
                String symbol = tempList.get(i).getSymbol();
                new StockDetailsLoader(MainActivity.this).execute(symbol);
            }
            Toast.makeText(this,"Connected to the Network!",Toast.LENGTH_LONG).show();
        }else {
            displayedStocks.addAll(tempList);
            Set<StocksToDisplay> set = new HashSet<>(displayedStocks);
            displayedStocks.clear();
            displayedStocks.addAll(set);
            Collections.sort(displayedStocks, new Comparator<StocksToDisplay>() {
                @Override
                public int compare(StocksToDisplay o1, StocksToDisplay o2) {
                    return o1.getSymbol().compareTo(o2.getSymbol());
                }
            });
            Toast.makeText(this,"App running in Offline mode!",Toast.LENGTH_LONG).show();
            stockDisplayAdapter = new StockDisplayAdapter(this,displayedStocks);
            recyclerView.setLayoutManager(new LinearLayoutManager(this  ));
            recyclerView.setAdapter(stockDisplayAdapter);
            stockDisplayAdapter.notifyDataSetChanged();
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        stockDisplayAdapter.notifyDataSetChanged();
    }

    public void displayNoDataToRefreshToast(){
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(this,"There's Nothing to Refresh",Toast.LENGTH_LONG).show();
    }
    public void reloadDataOnRefresh(){
        displayedStocks.clear();
        swipeRefreshLayout.setRefreshing(false);
        ArrayList<StocksToDisplay> tempList = sqlDatabase.loadAllSavedStocks();
        Collections.sort(tempList, new Comparator<StocksToDisplay>() {
            @Override
            public int compare(StocksToDisplay o1, StocksToDisplay o2) {
                return o1.getSymbol().compareTo(o2.getSymbol());
            }
        });
        for (int i = 0; i < tempList.size(); i++) {
            String symbol = tempList.get(i).getSymbol();
            new StockDetailsLoader(MainActivity.this).execute(symbol);
        }
        Toast.makeText(this,"Data Refreshed!",Toast.LENGTH_LONG).show();
    }

    public static boolean isDeviceOnline(){
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            Log.d("IsDeviceOnline", "isDeviceOnline: "+(reachable = (returnVal==0)));
            return reachable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void networkConnectionErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("OOPS !");
        builder.setMessage("Looks like you have no Active Internet Connection!");
        builder.setIcon(R.drawable.baseline_warning_24);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.stock_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.add_new_stock:
                if(!isDeviceOnline()){
                    networkConnectionErrorDialog();
                    return false;
                }else {
                    showSearchStockDialog();
                    return true;
                }
            case R.id.deleteAllRecords:
                selectedStocksForDisplay.removeAll(selectedStocksForDisplay);
                androidx.appcompat.app.AlertDialog.Builder deleteAlert= new androidx.appcompat.app.AlertDialog.Builder(this);
                deleteAlert.setTitle("Delete All?");
                deleteAlert.setMessage("Are you sure you want to delete all Selected Stocks in Display?");
                deleteAlert.setCancelable(true);
                deleteAlert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        selectedStocksForDisplay.removeAll(selectedStocksForDisplay);
                        displayedStocks.removeAll(displayedStocks);
                        ArrayList<StocksToDisplay> allRecords = sqlDatabase.loadAllSavedStocks();
                        for(int i=0;i<allRecords.size();i++){
                            sqlDatabase.deleteStock(allRecords.get(i).getSymbol());
                        }
                        stockDisplayAdapter.notifyDataSetChanged();
                        ItemsRemovedConfirmationDialog();
                    }
                });
                deleteAlert.setIcon(R.drawable.baseline_delete_24);
                deleteAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                deleteAlert.show();
                return true;
             default:
                 Log.d("MainActivity", "onOptionsItemSelected: Default Option");
                 return true;
        }

    }

    private void ItemsRemovedConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Removed!");
        builder.setMessage("All the Stocks in Display are removed!");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public void onClick(View v) {
        final int pos = recyclerView.getChildAdapterPosition(v);
        StocksToDisplay stock = displayedStocks.get(pos);
        String url = extendedInfoAbooutStock+stock.getSymbol();
        Intent i = new Intent((Intent.ACTION_VIEW));
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public boolean onLongClick(View v) {
        final int pos = recyclerView.getChildAdapterPosition(v);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure, you want to remove the stock from the List?");
        builder.setPositiveButton("Yes, Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("position: ", ""+pos);
                sqlDatabase.deleteStock(displayedStocks.get(pos).getSymbol());
                displayedStocks.remove(pos);
                stockDisplayAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setIcon(R.drawable.baseline_delete_24);
        AlertDialog alert = builder.create();
        alert.show();
        return false;
    }


    public void showSearchStockDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stock Selection");
        builder.setMessage("Enter a valid Stock symbol:");
        builder.setCancelable(true);
        final EditText editText = new EditText(this);
        editText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(editText);
        builder.setIcon(R.drawable.baseline_show_chart_24);
        editText.setTextColor(Color.BLACK);
        builder.setPositiveButton("Add/Look-up", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredString = editText.getText().toString();
                if(enteredString.length() == 0){
                    emptyStringEntered();
                }else {
                    selectedStocksForDisplay.removeAll(selectedStocksForDisplay);
                    for (int i=0;i<allStocksList.size();i++){
                        if(allStocksList.get(i).getName().toLowerCase().contains(enteredString.toLowerCase()) || allStocksList.get(i).getSymbol().toLowerCase().contains(enteredString.toLowerCase())){
                            selectedStocksForDisplay.add(allStocksList.get(i));
                        }
                    }
                    if(selectedStocksForDisplay.size() == 0){
                        noInformationRelatedToTheInputStringWasFound(enteredString);
                    }else if(selectedStocksForDisplay.size() == 1){
                        if(itemAlreadyPresentInTheStockList(enteredString)){
                            displayItemAlreadyPresentInTheStockListDialog(enteredString);
                        }else {
                            saveSingleMatchedRecord(selectedStocksForDisplay.get(0).getSymbol());
                        }
                    }else {
                        selectTheSearchResult(selectedStocksForDisplay,selectedStocksForDisplay.size());
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.show();
    }
    private boolean itemAlreadyPresentInTheStockList(String enteredString){
        for (int i=0;i<displayedStocks.size();i++){
            if(displayedStocks.get(i).getName().toLowerCase().contains(enteredString.toLowerCase()) || displayedStocks.get(i).getSymbol().toLowerCase().contains(enteredString.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    private void emptyStringEntered(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Invalid Input");
        builder.setMessage("An empty input received. Enter a valid one");
        builder.setIcon(R.drawable.baseline_warning_24);
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showSearchStockDialog();
                return;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void displayItemAlreadyPresentInTheStockListDialog(String enteredString){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Duplicate Record Found!");
        builder.setMessage("The Search item: "+enteredString+"'s record is already added to the Display list");
        builder.setIcon(R.drawable.baseline_warning_24);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void noInformationRelatedToTheInputStringWasFound(String enteredString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Record not Found!");
        builder.setMessage("No record matched your search: "+enteredString+" ,enter a valid one");
        builder.setPositiveButton("New Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showSearchStockDialog();
            }
        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.show();
        builder.setIcon(R.drawable.baseline_warning_24);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void selectTheSearchResult(ArrayList<Stocks> searcResult, int size){

        if(searcResult.size()==1){
            finalStocksForDisplay.add(searcResult.get(0));
        }else {
            final String[] searchStockNamesResult = new String[size];
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select one from the List");
            for(int i=0;i<size;i++){
                    String name = searcResult.get(i).getName()+"("+searcResult.get(i).getSymbol()+")";
                    searchStockNamesResult[i]=name;
            }

            builder.setItems(searchStockNamesResult, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    persistStockRecord(searchStockNamesResult,which);

                }
            });
            builder.setNegativeButton("Cancel, Make a New Search", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showSearchStockDialog();
                    return;
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    public void saveSingleMatchedRecord(String symbol){
        new StockDetailsLoader(this).execute(symbol);
    }
    public void persistStockRecord(String[] searchStockNamesResult,Integer recordId){
        String [] slicedParts = searchStockNamesResult[recordId].split("\\(");
        String [] actualSymbol = slicedParts[1].split("\\)");
        Toast.makeText(this,"Stock added! Refresh by Swiping down to Sort/Reload the data", Toast.LENGTH_LONG).show();
        new StockDetailsLoader(this).execute(actualSymbol[0]);
        stockDisplayAdapter.notifyDataSetChanged();
    }
    public void displayLoastMessageToRefresh(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Record Added!");
        builder.setMessage("A new Stock is added into the list");
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Collections.sort(displayedStocks, new Comparator<StocksToDisplay>() {
                    @Override
                    public int compare(StocksToDisplay o1, StocksToDisplay o2) {
                        return o1.getSymbol().compareTo(o2.getSymbol());
                    }
                });
                stockDisplayAdapter.notifyDataSetChanged();
            }
        });
        builder.setIcon(R.drawable.baseline_warning_24);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    protected void updateStocksData(ArrayList<Stocks> stocksData){
        this.allStocksList = stocksData;
    }

    public void addStockToTheDataSetToBeDisplayed(StocksToDisplay detailedStock){

        try{
            int index = displayedStocks.indexOf(detailedStock);
            if(index>-1){
                displayedStocks.remove(index);
            }
            Log.d("MainActivity", "addStockToTheDataSetToBeDisplayed: "+detailedStock);
            sqlDatabase.deleteStock(detailedStock.getSymbol());
            sqlDatabase.addStockToDB(detailedStock);
            displayedStocks.add(detailedStock);
            //sortAndDisplay(displayedStocks);
            stockDisplayAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
            displayUnexpectedErrorDialog();
        }
    }
    public void displayUnexpectedErrorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Failed to load Data!");
        builder.setMessage("An unexpected error occured in fetching the data for that symbol, try adding another stock");
        builder.setNegativeButton("Make a New Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showSearchStockDialog();
                return;
            }
        });
        builder.setIcon(R.drawable.baseline_warning_24);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy(){
        sqlDatabase.shutDown();
        super.onDestroy();
    }
}
