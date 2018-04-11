package com.example.david.barcodecart.Adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by David on 9/3/2017.
 */

public class Product {
    private String name;
    private String code;
    private double qty;
    private double price;
    private String date;
    private String time;

    public Product() {
        this.name = "";
        this.code = "";
        this.qty = 0;
        this.price = 0.0;
        this.date = getCurrentDate();
        this.time = getCurrentTime();
    }

    public Product(String name, String code, double qty, double price, String date, String time) {
        this.name = name;
        this.code = code;
        this.qty = qty;
        this.price = price;
        this.date = date;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public void addQty(double newQty){
        this.qty += newQty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void addPrice(double newPrice) {
        this.price += newPrice;
    }

    private String getCurrentDate(){
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c.getTime());
    }

    private String getCurrentTime(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        return df.format(c.getTime());
    }
}
