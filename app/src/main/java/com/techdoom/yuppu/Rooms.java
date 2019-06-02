package com.techdoom.yuppu;

public class Rooms {
    public String roomname;
    public String roomdes;
    public String roomid;
    public String hide;
    public Rooms() {
    }

    public Rooms(String roomname, String roomdes,String roomid,String hide) {
        this.roomname = roomname;
        this.roomdes = roomdes;
        this.roomid = roomid;
        this.hide=hide;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public String getRoomdes() {
        return roomdes;
    }

    public void setRoomdes(String roomdes) {
        this.roomdes = roomdes;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }


    public String getHide() {
        return hide;
    }

    public void setHide(String hide) {
        this.hide = hide;
    }




}
