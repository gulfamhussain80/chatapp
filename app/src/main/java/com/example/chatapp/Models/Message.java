package com.example.chatapp.Models;

import com.google.firebase.firestore.Exclude;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Message implements IMessage, Serializable {

    String id;
    String text;
    User author;
    Date createdAt;
    public Message(){

    }
    public Message(String text, User user){
        this.text=text;
        this.author=user;
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
    @Exclude
    public IUser getUser() {
        return author;
    }

    public User getAuthor() {
        return author;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }
    public Map<String,Object> hashMap()
    {
        Map<String,Object> hashMap = new HashMap<>();
        hashMap.put("text",text);
        hashMap.put("author",author);
        hashMap.put("createdAt",createdAt);
        return hashMap;

    }
}

