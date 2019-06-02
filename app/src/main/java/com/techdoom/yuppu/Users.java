package com.techdoom.yuppu;

public class Users {

    public String name;
    public String image;
    public String status;
    public String online;
    public String chatusername;


    public Users(String name, String image, String status, String online,String chatusername) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.online = online;
        this.chatusername=chatusername;
    }

    public Users() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }
    public String getChatusername() {
        return chatusername;
    }

    public void setChatusername(String chatusername) {
        this.chatusername = chatusername;
    }

}
