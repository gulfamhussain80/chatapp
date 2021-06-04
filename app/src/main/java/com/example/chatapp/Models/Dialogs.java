package com.example.chatapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Dialogs implements IDialog, Parcelable {

    private String id;
    private String dialogPhoto;
    private String dialogName;
    private ArrayList<User> users = new ArrayList<User>();

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
        this.dialogName= message.text;
        this.unreadCount=3;
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
    @Exclude
    public ArrayList<User> getUsers() {
        return this.users;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
