package com.example.androidpushnotification.data;

public class PushNotification {

    private NotificationData notificationData;
    private String to;

    public PushNotification() {
    }

    public PushNotification(NotificationData notificationData, String to) {
        this.notificationData = notificationData;
        this.to = to;
    }

    public NotificationData getNotificationData() {
        return notificationData;
    }

    public void setNotificationData(NotificationData notificationData) {
        this.notificationData = notificationData;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "PushNotification{" +
                "notificationData=" + notificationData +
                ", to='" + to + '\'' +
                '}';
    }
}
