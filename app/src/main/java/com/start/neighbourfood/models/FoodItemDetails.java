package com.start.neighbourfood.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FoodItemDetails {

    private String itemID;
    private String itemName;
    private String servedFor;
    private String price;
    private String sellerID;
    private String quantity;
    private String itemDesc;
    private String isAvailable;

    public FoodItemDetails() {

    }

    public FoodItemDetails(String itemName, String price) {
        this.itemName = itemName;
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getServedFor() {
        return servedFor;
    }

    public void setServedFor(String servedFor) {
        this.servedFor = servedFor;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }

    public String isAvailable() {
        return isAvailable;
    }

    public void setAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }
}
