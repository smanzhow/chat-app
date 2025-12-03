package se.sprinto.hakan.chatapp.model;


import java.time.LocalDateTime;

public class Message {
    private int messageId;
    private int userId;
    private String text;
    private LocalDateTime timestamp;


    public Message(int messageId, int userId, String text, LocalDateTime timestamp) {
        this.messageId = messageId;
        this.userId = userId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public Message(int userId, String text, LocalDateTime timestamp){
        this.userId = userId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public int getMessageId(){
        return messageId;
    }



    public int getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

