package com.start.neighbourfood.models;

public class FoodItem{
    private String itemID;
    private String itemName;

    public FoodItem(){

    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Override
    public String toString() {
        return "\u2022 " + this.itemName;
    }
}