package com.start.neighbourfood.Utils;

import com.start.neighbourfood.models.FoodItemDetails;

import java.util.List;

public class NFUtils {



    public static String constructOrderAcceptedMessage(){
        return "Confirmed: have a new notification from";
    }

    public static String constructFoodPreparedMessage(){
        return "Food Prepared: have a new notification from";
    }

    public static String getTotalPrice(List<FoodItemDetails> foodItemDetailsList) {
        int sum = 0;
        for (FoodItemDetails itemDetails : foodItemDetailsList) {
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
}
