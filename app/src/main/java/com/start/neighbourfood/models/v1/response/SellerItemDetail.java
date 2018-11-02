package com.start.neighbourfood.models.v1.response;

import java.util.List;

public class SellerItemDetail {

    private String fName ;
    private String lName ;
    private String sellerId ;
    private String flatNumber ;
    private List<FoodItem> foodItemDetail;

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public List<FoodItem> getFoodItemDetail() {
        return foodItemDetail;
    }

    public void setFoodItemDetail(List<FoodItem> foodItemDetail) {
        this.foodItemDetail = foodItemDetail;
    }
}
