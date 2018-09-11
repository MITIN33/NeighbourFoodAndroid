package com.start.neighbourfood.models;

public class NfMessageNotification {
    private String to;
    private NotificationData notification;

    public NfMessageNotification(String token, String message) {
        this.to = token;
        notification = new NotificationData(message);
    }

    public NotificationData getData() {
        return notification;
    }

    public void setData(NotificationData data) {
        this.notification = data;
    }


    public class NotificationData {
        private String title;
        private String text;

        public NotificationData(String mess) {
            text = mess;
            title = "NeighbourFood";
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
