package com.techdoom.yuppu;


public class Messages {

    public String messages, type;
    public long time;

    public String from;

    public Messages(String messages, long time, String type, String from) {
        this.messages = messages;

        this.time = time;
        this.type = type;
        this.from = from;

    }

    public Messages() {
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


}