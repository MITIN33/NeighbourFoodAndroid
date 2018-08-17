package com.start.neighbourfood.models;

import java.util.Date;

public class SellerItemInfo {

    private String sellerItemID;
    private String itemID;
    private String sellerID;
    private int quantity;
    private int servedFor;
    private Date startTime;
    private Date endTime;

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getServedFor() {
        return servedFor;
    }

    public void setServedFor(int servedFor) {
        this.servedFor = servedFor;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getSellerItemID() {
        return sellerItemID;
    }

    public void setSellerItemID(String sellerItemID) {
        this.sellerItemID = sellerItemID;
    }
}
