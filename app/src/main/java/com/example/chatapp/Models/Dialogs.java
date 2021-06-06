package com.example.chatapp.Models;

import com.google.firebase.firestore.Exclude;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Dialogs implements IDialog, Serializable {

    private String id;
    private String dialogPhoto;
    private String dialogName;
    private ArrayList<User> Author = new ArrayList<User>();

    private Message LastMessage;
    private ArrayList<Message> MessageList= new ArrayList<Message>();
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
    public    Dialogs(Message message){
        this.dialogPhoto = "";
        this.Author.add(message.author);
        this.LastMessage=message;
        this.dialogName= message.text;
        this.unreadCount=3;
        this.id="firstDialog";
        this.MessageList.add(message);
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
        return Author;
    }

    public ArrayList<User> getAuthor() {
        return Author;
    }

    @Override
   // @Exclude
    public IMessage getLastMessage() {
        return  LastMessage;
    }

    @Override
    @Exclude
    public void setLastMessage(IMessage message) {
          this.LastMessage=(Message)message;
    }

    public ArrayList<Message> getMessageList() {
        return MessageList;
    }

    //@Override
    //public void setLastMessage(IMessage message) {
           //lastMessage = (Message) message;
    //}

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
        hashMap.put("Author",Author);
        hashMap.put("LastMessage",LastMessage);
        hashMap.put("unreadCount",unreadCount);
        hashMap.put("MessageList",MessageList);

        return  hashMap;

    }
}
