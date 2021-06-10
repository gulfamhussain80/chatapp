package com.example.chatproj2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatproj2.Adapters.MessageAdapter;
import com.example.chatproj2.Models.APIService;
import com.example.chatproj2.Models.Chat;
import com.example.chatproj2.Models.User;
import com.example.chatproj2.Notifications.Client;
import com.example.chatproj2.Notifications.Data;
import com.example.chatproj2.Notifications.MyResponse;
import com.example.chatproj2.Notifications.Sender;
import com.example.chatproj2.Notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    private CircleImageView message_profile_image;
    private TextView message_profile_username;
    private ImageButton send_msg_btn;
    private EditText msg_editText;
    FirebaseUser firebaseUser;
    DatabaseReference dbReference;
    Intent intent;
    MessageAdapter messageAdapter;
    List<Chat> chats;
    RecyclerView messageRecyclerView;
    String userId;
    ValueEventListener seenListener;
    APIService apiService;
    boolean notify=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar=findViewById(R.id.msgtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MessageActivity.this,MainActivity.class));//.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        apiService= Client.getClient("https://fcm.googleapis.com").create(APIService.class);
        messageRecyclerView=findViewById(R.id.msgs_display_recycler_view);
        messageRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(linearLayoutManager);

        intent=getIntent();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        userId=intent.getStringExtra("userId");
        message_profile_image=findViewById(R.id.message_profile_image);
        message_profile_username=findViewById(R.id.message_profile_username);
        send_msg_btn=findViewById(R.id.msg_send_btn);
        msg_editText=findViewById(R.id.msg_edit_text);
        dbReference= FirebaseDatabase.getInstance().getReference("Users").child(userId);
        send_msg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify=true;
                String message=msg_editText.getText().toString();
                if(! message.equals("")){
                    sendMessage(firebaseUser.getUid(),userId,message);
                }else{
                    Toast.makeText(MessageActivity.this,"Unable to send Message",Toast.LENGTH_LONG).show();
                }
                msg_editText.setText("");
            }
        });
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                message_profile_username.setText(user.getUsername());
                if(user.getImageUrl().equals("imgurl")){
                    message_profile_image.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(message_profile_image);
                }
                readMessages(firebaseUser.getUid(),userId,user.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        seenMessage(userId);

    }

    private void seenMessage(String userid){
        dbReference=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Chat chat=dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)){
                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put("isseen",true);
                            dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver,String message){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);
        ref.child("Chats").push().setValue(hashMap);

        final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(receiver);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (! snapshot.exists()){
                    chatRef.child("id").setValue(receiver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final String msg=message;
        dbReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if (notify){
                    sendNotifications(receiver,user.getUsername(),msg);
                }

                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void  sendNotifications(String receiver,String username,String message){
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Token token=dataSnapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(),R.mipmap.ic_launcher,username+" : "+message,"New Message",userId);
                    Sender sender = new Sender(data,token.getToken());
                    apiService.setNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success!= 1){
                                            Toast.makeText(MessageActivity.this,"Failed to send Notification",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessages(String myId,String userId,String imageUrl){
        chats=new ArrayList<>();
        dbReference=FirebaseDatabase.getInstance().getReference("Chats");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat=dataSnapshot.getValue(Chat.class);
                    if((chat.getSender().equals(myId) && chat.getReceiver().equals(userId))|| (chat.getSender().equals(userId) && chat.getReceiver().equals(myId))){
                        chats.add(chat);
                    }
                }
                messageAdapter=new MessageAdapter(MessageActivity.this,chats,imageUrl);
                messageRecyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
        editor.putString("currentuser",userid);
        editor.apply();
    }

    private void userStatus(String status){
        dbReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        dbReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userStatus("online");
        currentUser(userId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dbReference.removeEventListener(seenListener);
        userStatus("offline");
        currentUser("none");
    }
}