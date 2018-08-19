package com.start.neighbourfood.models;

public class FoodItemDetails {

    private String itemID;
    private String itemName;
    private String itemServing;
    private String itemPrice;

    public FoodItemDetails() {

    }

    public FoodItemDetails(String itemName, String itemPrice) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
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

    public String getItemServing() {
        return itemName;
    }

    public void setItemServing(String itemServing) {
        this.itemServing = itemServing;
    }

    public String getItemPrice() {
        return itemName;
    }

    public void setItemPrive(String itemPrice) {
        this.itemPrice = itemPrice;
    }
}
