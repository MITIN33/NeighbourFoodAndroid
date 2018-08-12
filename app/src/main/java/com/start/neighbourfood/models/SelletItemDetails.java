package com.start.neighbourfood.models;

public class SelletItemDetails {

    private String flatID;

    private String FlatNumber;

    private String apartmentID;

    private String itemId;

    private String itemName;

    private String userName;

    public SelletItemDetails() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFlatID() {
        return flatID;
    }

    public void setFlatID(String id) {
        this.flatID = id;
    }

    public String getFlatNumber() {
        return FlatNumber;
    }

    public void setFlatNumber(String name) {
        this.FlatNumber = name;
    }

    public String getApartmentID() {
        return apartmentID;
    }

    public void setApartmentID(String apartmentID) {
        this.apartmentID = apartmentID;
    }
}
