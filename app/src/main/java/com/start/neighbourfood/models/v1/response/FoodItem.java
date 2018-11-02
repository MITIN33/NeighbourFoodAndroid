package com.start.neighbourfood.models.v1.response;

public class FoodItem{
    private String itemID;
    private String itemName;
    private String servedFor;
    private String price;
    private String itemDesc;
    private String quantity;
    private boolean available;

    public FoodItem() {

    }

    public FoodItem(String itemName, String itemPrice) {
        this.itemName = itemName;
        this.price = itemPrice;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
