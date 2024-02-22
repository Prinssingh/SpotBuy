package com.s19.spotbuy.Models;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.Date;
@Keep
public class Chat  implements Serializable {
    String id;
    String uid;//Other Users
    String name;//Other Users
    String image;//Other Users
    String lastMessage;
    Date time;
    int unreadMessage;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getUnreadMessage() {
        return unreadMessage;
    }

    public void setUnreadMessage(int unreadMessage) {
        this.unreadMessage = unreadMessage;
    }
    public void increaseUnreads(){
        unreadMessage+=1;
    }
    public void decreaseUnreads(){
        unreadMessage-=1;
    }
}
