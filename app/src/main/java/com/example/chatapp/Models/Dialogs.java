package com.example.chatapp.Models;

import com.google.firebase.firestore.Exclude;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;

import java.util.ArrayList;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Dialogs implements IDialog {

    private String id;
    private String dialogPhoto;
    private String dialogName;
    private ArrayList<IUser> users = new ArrayList<IUser>();

    private Message lastMessage;

    private int unreadCount;

    //public Dialogs(String id, String name, String photo,
                  //ArrayList<User> users, Message lastMessage, int unreadCount) {

       // this.id = id;
       // this.dialogName = name;
       // this.dialogPhoto = photo;
       // this.users = users;
       // this.lastMessage = lastMessage;
       // this.unreadCount = unreadCount;
    //}
    public Dialogs(){

    }
    public  Dialogs(Message message){
        this.dialogPhoto = "";
        this.users.add(message.user);
        this.lastMessage=message;
        this.unreadCount=0;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDialogPhoto() {
        return dialogPhoto;
    }

    @Override
    public String getDialogName() {
        return dialogName;
    }

    @Override
    //@Exclude
    public ArrayList<IUser> getUsers() {
        return users;
    }

    @Override
    @Exclude
    public Message getLastMessage() {
        return lastMessage;
    }

    @Override
    public void setLastMessage(IMessage message) {
           lastMessage = (Message) message;
    }


    @Override
    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
    public Map<String,Object> hashMap(){
        Map<String,Object> hashMap = new HashMap<>();
        hashMap.put("dialogPhoto",dialogPhoto);
        hashMap.put("dialogName",dialogName);
        hashMap.put("users",users);
        hashMap.put("lastMessage",lastMessage);
        hashMap.put("unreadCount",unreadCount);
        return  hashMap;


    }
}