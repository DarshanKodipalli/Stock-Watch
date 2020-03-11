package com.example.stockwatch;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView StockCompanyName;
    public TextView StockSymbol;
    public TextView StockPriceChange;
    public TextView StockLatestPrice;
    public TextView StockPriceChangePercentage;
    public ImageView PriceChangeStatus;
    public View StockDivider;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        StockCompanyName = itemView.findViewById(R.id.companyStockName);
        StockSymbol = itemView.findViewById(R.id.stockSymbol);
        StockPriceChange = itemView.findViewById(R.id.priceChange);
        StockLatestPrice = itemView.findViewById(R.id.latestPrice);
        StockPriceChange= itemView.findViewById(R.id.priceChange);
        StockPriceChangePercentage= itemView.findViewById(R.id.priceChangePercentage);
        PriceChangeStatus = itemView.findViewById(R.id.priceChangeStatus);
        StockDivider = itemView.findViewById(R.id.divider);
    }
}
