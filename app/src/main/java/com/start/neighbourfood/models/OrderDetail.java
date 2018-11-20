package com.start.neighbourfood.models;

import com.start.neighbourfood.models.v1.response.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class OrderDetail {

    private String orderId;
    private String userPlacedBy;
    private String userPlacedTo;
    private List<FoodItem> foodItems;
    private int quantity;
    private String createTime;
    private String orderStatus;

    public OrderDetail() {
        foodItems = new ArrayList<>();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getUserPlacedBy() {
        return userPlacedBy;
    }

    public void setUserPlacedBy(String userPlacedBy) {
        this.userPlacedBy = userPlacedBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUserPlacedTo() {
        return userPlacedTo;
    }

    public void setUserPlacedTo(String userPlacedTo) {
        this.userPlacedTo = userPlacedTo;
    }

    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    public void setFoodItems(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }
}
