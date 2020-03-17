package com.ash.bookworm.helpers.models;

public class Message {
    public String message, sender, receiver, imageId;

    public Message() {

    }

    public Message(String message, String sender, String receiver, String imageId) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.imageId = imageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
