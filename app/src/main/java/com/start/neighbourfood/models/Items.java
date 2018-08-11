package com.start.neighbourfood.models;

public class Items {

    private String flatID;

    private String FlatNumber;

    private String apartmentID;

    public Items() {
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
