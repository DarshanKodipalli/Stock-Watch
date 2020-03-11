package com.example.stockwatch;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class StockDisplayAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private MainActivity mnActivity;
    private ArrayList<StocksToDisplay> displayedStocks;

    public StockDisplayAdapter(MainActivity mainActivity,ArrayList<StocksToDisplay> stockList){
        this.displayedStocks = stockList;
        this.mnActivity = mainActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_layout, parent, false);
        itemView.setOnClickListener(mnActivity);
        itemView.setOnLongClickListener(mnActivity);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        StocksToDisplay stock = displayedStocks.get(position);
        if(stock.getChangePercentage()<0.0){
            holder.StockCompanyName.setText(stock.getName());
            holder.StockSymbol.setText(stock.getSymbol());
            holder.StockLatestPrice.setText(String.format(Locale.US, "%.2f", stock.getLatestPrice()));
            holder.StockPriceChange.setText(String.format(Locale.US, "%.2f", stock.getChange()));
            holder.StockPriceChangePercentage.setText(String.format(Locale.US, "(%.2f%%)", stock.getChangePercentage()));
            holder.StockCompanyName.setTextColor(Color.RED);
            holder.StockSymbol.setTextColor(Color.RED);
            holder.StockLatestPrice.setTextColor(Color.RED);
            holder.StockPriceChange.setTextColor(Color.RED);
            holder.StockPriceChangePercentage.setTextColor(Color.RED);
            holder.PriceChangeStatus.setImageResource(R.drawable.ic_arrow_down);
            holder.PriceChangeStatus.setColorFilter(Color.RED);
            holder.StockDivider.setBackgroundColor(Color.RED);
        }else {
            holder.StockCompanyName.setText(stock.getName());
            holder.StockSymbol.setText(stock.getSymbol());
            holder.StockLatestPrice.setText(String.format(Locale.US, "%.2f", stock.getLatestPrice()));
            holder.StockPriceChange.setText(String.format(Locale.US, "%.2f", stock.getChange()));
            holder.StockPriceChangePercentage.setText(String.format(Locale.US, "(%.2f%%)", stock.getChangePercentage()));
            holder.StockCompanyName.setTextColor(Color.GREEN);
            holder.StockSymbol.setTextColor(Color.GREEN);
            holder.StockLatestPrice.setTextColor(Color.GREEN);
            holder.StockPriceChange.setTextColor(Color.GREEN);
            holder.StockPriceChangePercentage.setTextColor(Color.GREEN);
            holder.PriceChangeStatus.setImageResource(R.drawable.ic_arrow_up);
            holder.PriceChangeStatus.setColorFilter(Color.GREEN);
            holder.StockDivider.setBackgroundColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return displayedStocks.size();
    }
}