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
    private String flatID;
    private boolean isAvailable;
    private String sellerItemID;
    private boolean veg;

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

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getFlatID() {
        return flatID;
    }

    public void setFlatID(String flatID) {
        this.flatID = flatID;
    }

    public String getSellerItemID() {
        return sellerItemID;
    }

    public void setSellerItemID(String sellerItemID) {
        this.sellerItemID = sellerItemID;
    }

    public boolean isVeg() {
        return veg;
    }

    public void setVeg(boolean veg) {
        this.veg = veg;
    }
}
