package com.example.chatapp.Models;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Message implements IMessage {

     String id;
    String text;
    User user;
    Date createdAt;
    public Message(String text, User user){
        this.text=text;
        this.user=user;
        this.createdAt =new Date();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }
    public Map<String,Object> hashMap()
    {
        Map<String,Object> hashMap = new HashMap<>();
        hashMap.put("text",text);
        hashMap.put("user",user);
        hashMap.put("createdAt",createdAt);
        return hashMap;

    }
}
