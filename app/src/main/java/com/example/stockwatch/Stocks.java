package com.example.stockwatch;

import java.io.Serializable;

public class Stocks implements Serializable {
    private String name;
    private String symbol;
    private String date;
    private String id;
    private String type;
    private Double latestPrice;
    private Double change;
    private Double changePercentage;

    Stocks(){

    }
    Stocks(String name, String symbol, String date, String id, String type){
        try{
            this.name = name;
            this.symbol = symbol;
            this.date = date;
            this.id = id;
            this.type = type;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

}
