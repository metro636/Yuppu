package com.techdoom.yuppu;
public class Conv {

    public String messages, type;
    public long time;
    public String read;
    public String from;
    public String under;

    public Conv(String messages, String read, long time, String type, String from, String under) {
        this.messages = messages;
        this.read = read;
        this.time = time;
        this.type = type;
        this.from = from;
        this.under = under;

    }

    public Conv() {
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
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

    public String getUnder() {
        return under;
    }

    public void setUnder(String under) {
        this.under = under;
    }


}