package com.start.neighbourfood.models.v1;

public class FlatInfo {
    private String flatID;
    private String flatNumber;
    private String apartmentID;

    public FlatInfo() {
    }

    public String getApartmentID() {
        return apartmentID;
    }

    public void setApartmentID(String apartmentID) {
        this.apartmentID = apartmentID;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public String getFlatID() {
        return flatID;
    }

    public void setFlatID(String flatID) {
        this.flatID = flatID;
    }

    @Override
    public String toString() {
        return flatNumber;
    }
}
