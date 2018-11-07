package com.start.neighbourfood.Utils;

import com.start.neighbourfood.models.v1.response.FoodItem;

import java.util.List;

public class NFUtils {



    public static String constructOrderAcceptedMessage(){
        return "Confirmed: Your request has been accepted by the provider";
    }

    public static String constructFoodPreparedMessage(String flatNumber){
        return "Food Prepared: Your food is ready, you can go and collect from Flat-"+ flatNumber;
    }

    public static String getTotalPrice(List<FoodItem> foodItemDetailsList) {
        int sum = 0;
        for (FoodItem itemDetails : foodItemDetailsList) {
            int pr = Integer.parseInt(itemDetails.getPrice());
            int q = Integer.parseInt(itemDetails.getQuantity());
            sum += pr * q;
        }

        return String.valueOf(sum);
    }

    public static String getOrderID(long epochTime, String userUid) {
       return String.format("ORD%s%s", epochTime, userUid.substring(0, 4)).toUpperCase();
    }

    public static boolean isOrderAcceptanceNotification(String message){
        if (message == null)
            return false;
        return message.startsWith("Confirmed:");
    }

    public static boolean isFoodPrepared(String message) {
        if (message == null)
            return false;
        return message.startsWith("Food Prepared:");
    }

    public static String[] getBuyerDataForOrderPlaced(){
        return new String[]{"Waiting for order\n   confirmation", "" , ""};
    }

    public static String[] getDataForOrderConfirmed(){
        return new String[]{"Order accepeted", "Food being\n  prepared" , ""};
    }

    public static String[] getBuyerDataForFoodPrepared(){
        return new String[]{"Order accepeted", "Food prepared" , "Go collect\n your food"};
    }

    public static String[] getDataForCollected(){
        return new String[]{"Order accepeted", "Food prepared" , "Collected"};
    }

    //Seller List
    public static String[] getSellerDataForOrderPlaced(){
        return new String[]{"Accept the order", "" , ""};
    }

    public static String[] getSellerDataForFoodPrepared(){
        return new String[]{"Order accepeted", "Food prepared" , "Go collect\n your food"};
    }

    public static String constructFoodCollectedMessage() {
        return "Food was collected. Thanks for using NeighbourFood.";
    }
}
