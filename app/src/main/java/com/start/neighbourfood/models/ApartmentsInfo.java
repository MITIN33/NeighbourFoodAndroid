package com.start.neighbourfood.models;

public class ApartmentsInfo {
    private String apartmentName;
    private String id;


    public String getApartmentName() {
        return apartmentName;
    }

    public void setApartmentName(String apartmentName) {
        this.apartmentName = apartmentName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ApartmentsInfo(String id, String name){
        this.id = id;
        this.apartmentName = name;
    }

    @Override
    public String toString() {
        return this.apartmentName;
    }
}
