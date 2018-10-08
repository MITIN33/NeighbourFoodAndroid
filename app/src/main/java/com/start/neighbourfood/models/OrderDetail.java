package com.start.neighbourfood.models;

import java.util.ArrayList;
import java.util.List;

public class OrderDetail {

    private String orderId;
    private String userPlacedBy;
    private String userPlacedTo;
    private List<FoodItemDetails> sellerItems;
    private int quantity;
    private String createTime;
    private String orderStatus;

    public OrderDetail() {
        sellerItems = new ArrayList<>();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<FoodItemDetails> getSellerItemId() {
        return sellerItems;
    }

    public void setSellerItemId(List<FoodItemDetails> sellerItemId) {
        this.sellerItems = sellerItemId;
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
}
