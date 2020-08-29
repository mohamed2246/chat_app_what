package com.example.whatsapp;

public class Messages {
    private String from , message , type , to , message_id , time , date , name ;

    public Messages() {
    }

    public Messages(String from, String message, String type, String to, String message_id, String time, String date, String name) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.to = to;
        this.message_id = message_id;
        this.time = time;
        this.date = date;
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getTo() {
        return to;
    }

    public String getMessage_id() {
        return message_id;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }
}
