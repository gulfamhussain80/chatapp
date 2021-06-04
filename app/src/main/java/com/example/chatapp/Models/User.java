
package com.example.chatapp.Models;

import com.google.firebase.auth.FirebaseUser;
import com.stfalcon.chatkit.commons.models.IUser;

public class User implements IUser {

    String id;
    String name;
    String avatar;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }
    public static  User UserfromFirebaseUser(FirebaseUser firebaseUser){
        User user = new User();
        user.id=firebaseUser.getUid();
        user.name=firebaseUser.getDisplayName();
        if(firebaseUser.getPhotoUrl()!=null){
            user.avatar=firebaseUser.getPhotoUrl().getPath();

        }
        else{
            user.avatar="";
        }

        return user;
    }

}

