package com.example.chatproj2.Notifications;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.chatproj2.MainActivity;
import com.example.chatproj2.MessageActivity;
import com.example.chatproj2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

import static android.content.ContentValues.TAG;

public class FirebaseIdService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        Log.e("NEW_TOKEN", s);
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            String token = task.getResult();
                            updateToken(token);
                        }

                    });
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String sented=remoteMessage.getData().get("sented");
        String user=remoteMessage.getData().get("user");
        SharedPreferences preferences=getSharedPreferences("PREFS",MODE_PRIVATE);
        String currentUser=preferences.getString("currentuser","none");
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && sented.equals(firebaseUser.getUid())){
            if (!currentUser.equals(user) ) {
                sendNotification(remoteMessage);
            }
        }

   }

   private void updateToken(String refToken){
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
       DatabaseReference dbReference= FirebaseDatabase.getInstance().getReference("Token");
       Token token=new Token(refToken);
       dbReference.child(firebaseUser.getUid()).setValue(token);
   }

   private void sendNotification(RemoteMessage remoteMessage){
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");

        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent = new Intent(this, MessageActivity.class);
       Bundle bundle=new Bundle();
       bundle.putString("userid",user);
       intent.putExtras(bundle);
       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       PendingIntent pendingIntent=PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);

       Uri defaultSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        notificationBuilder.setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);


       NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
       int i=0;
       if (j>0){
           i=j;
       }
       notificationManager.notify(i,notificationBuilder.build());

   }
}

