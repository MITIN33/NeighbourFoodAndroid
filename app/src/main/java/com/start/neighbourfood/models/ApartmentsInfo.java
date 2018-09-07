package com.start.neighbourfood.models;

public class ApartmentsInfo {
    private String apartmentName;
    private String apartmentID;

    public ApartmentsInfo() {
    }


    @Override
    public String toString() {
        return this.apartmentName;
    }

    public String getApartmentName() {
        return apartmentName;
    }

    public void setApartmentName(String apartmentName) {
        this.apartmentName = apartmentName;
    }

    public String getApartmentID() {
        return apartmentID;
    }

    public void setApartmentID(String apartmentID) {
        this.apartmentID = apartmentID;
    }
}
