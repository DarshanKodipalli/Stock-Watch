package com.example.stockwatch;

import java.io.Serializable;

public class StocksToDisplay implements Serializable {
    private String name;
    private String symbol;
    private Double latestPrice;
    private Double change;
    private Double changePercentage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getLatestPrice() {
        return latestPrice;
    }

    public void setLatestPrice(Double latestPrice) {
        this.latestPrice = latestPrice;
    }

    public Double getChange() {
        return change;
    }

    public void setChange(Double change) {
        this.change = change;
    }

    public Double getChangePercentage() {
        return changePercentage;
    }

    public void setChangePercentage(Double changePercentage) {
        this.changePercentage = changePercentage;
    }

    StocksToDisplay(){

    }
    StocksToDisplay(String name, String symbol, Double latestPrice, Double change, Double changePercentage){
        try{
            this.name = name;
            this.symbol = symbol;
            this.latestPrice= latestPrice;
            this.change= change;
            this.changePercentage= changePercentage;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
