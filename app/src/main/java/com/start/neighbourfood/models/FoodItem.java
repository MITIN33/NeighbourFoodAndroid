package com.start.neighbourfood.models;

public class FoodItem{
    private String itemID;
    private String itemName;
    private String itemServing;
    private String itemPrice;
    private String itemDesc;

    public FoodItem() {

    }

    public FoodItem(String itemName, String itemPrice) {
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
        return itemServing;
    }

    public void setItemServing(String itemServing) {
        this.itemServing = itemServing;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }


    @Override
    public String toString() {
        return this.itemName;
    }
}
