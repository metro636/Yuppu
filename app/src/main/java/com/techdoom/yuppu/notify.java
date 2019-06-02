package com.techdoom.yuppu;

public class notify {
    public String type;
    public String from;
    public long time;


    public notify() {
    }

    public notify(String type, String from, long time) {
        this.type = type;
        this.from = from;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


}