package com.start.neighbourfood.models;

import java.security.PublicKey;
import java.util.List;

public class FlatsInfo {

    private String sellerName;
    private String SellerId;
    private String flatNumber;
    private String rating;
    private List<FoodItem> foodItems;

    public FlatsInfo(){

    }

    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    public void setFoodItems(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }

    public String getSellerId() {
        return SellerId;
    }

    public void setSellerId(String sellerId) {
        SellerId = sellerId;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

}
