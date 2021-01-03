package com.android.socialmedia;

public class Notification {
    String Message, caption, date, imageDate, imageUrl, senderUsername, username, type;

    public Notification() {
    }

    public Notification(String message, String caption, String date, String imageDate, String imageUrl, String senderUsername, String username, String type) {
        Message = message;
        this.caption = caption;
        this.date = date;
        this.imageDate = imageDate;
        this.imageUrl = imageUrl;
        this.senderUsername = senderUsername;
        this.username = username;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageDate() {
        return imageDate;
    }

    public void setImageDate(String imageDate) {
        this.imageDate = imageDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
