package com.start.neighbourfood.models;

import com.start.neighbourfood.models.v1.UserBaseInfo;
import com.start.neighbourfood.models.v1.response.FoodItem;

import java.util.List;

public class OrderProgress {

    private String message;
    private OrderStatus orderStatus;
    private String orderId;
    private long endTime;
    private String orderType;
    private UserBaseInfo userPlacedTo;
    private UserBaseInfo userPlacedBy;
    private long createTime;
    private List<FoodItem> sellerItems;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public UserBaseInfo getUserPlacedTo() {
        return userPlacedTo;
    }

    public void setUserPlacedTo(UserBaseInfo userPlacedTo) {
        this.userPlacedTo = userPlacedTo;
    }

    public UserBaseInfo getUserPlacedBy() {
        return userPlacedBy;
    }

    public void setUserPlacedBy(UserBaseInfo userPlacedBy) {
        this.userPlacedBy = userPlacedBy;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public List<FoodItem> getSellerItems() {
        return sellerItems;
    }

    public void setSellerItems(List<FoodItem> sellerItems) {
        this.sellerItems = sellerItems;
    }


    public enum OrderStatus{
        PENDING_CONFIRMATION,
        PREPARING,
        PREPARED,
        COMPLETED
    }
}
