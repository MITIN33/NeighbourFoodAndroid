package com.start.neighbourfood.models;

public class SellerItemInfo {

    private String sellerItemID;
    private String foodName;
    private String itemID;
    private String sellerID;
    private int quantity;
    private String price;
    private int servedFor;
    private boolean isAvailable;

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

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getSellerItemID() {
        return sellerItemID;
    }

    public void setSellerItemID(String sellerItemID) {
        this.sellerItemID = sellerItemID;
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

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
