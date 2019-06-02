package com.techdoom.yuppu;


public class RoomMessages {

    public String messages, type;
    public String time;

    public String from;
    public String pvt;

    public RoomMessages(String messages, String time, String type, String from,String pvt) {
        this.messages = messages;

        this.time = time;
        this.type = type;
        this.from = from;
        this.pvt=pvt;

    }

    public RoomMessages() {
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
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

    public String getPvt() {
        return pvt;
    }

    public void setPvt(String pvt) {
        this.pvt = pvt;
    }


}