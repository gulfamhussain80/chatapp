package com.example.chatproj2.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatproj2.Adapters.UserAdapter;
import com.example.chatproj2.Models.Chat;
import com.example.chatproj2.Models.Chatlist;
import com.example.chatproj2.Models.User;
import com.example.chatproj2.Notifications.Token;
import com.example.chatproj2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ChatsFragment extends Fragment {
    private UserAdapter userAdapter;
    private RecyclerView chat_userRecyclerView;
    FirebaseUser firebaseUser;
    DatabaseReference dbReference;
    private List<User> users;
    private List<Chatlist> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         View view=inflater.inflate(R.layout.fragment_chats, container, false);
         chat_userRecyclerView=view.findViewById(R.id.chat_users_recycler_view);
         chat_userRecyclerView.setHasFixedSize(true);
         chat_userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
         firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
         usersList=new ArrayList<>();

        dbReference=FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chatlist chatlist=dataSnapshot.getValue(Chatlist.class);
                    usersList.add(chatlist);
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }

                // Get new FCM registration token
                String token = task.getResult();
                updateTokens(token);
            }
        });

        return view;
    }

    private void updateTokens(String token){
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1=new Token(token);
        dbRef.child(firebaseUser.getUid()).setValue(token1);
    }


    private void chatList(){
        users=new ArrayList<>();
        dbReference=FirebaseDatabase.getInstance().getReference("Users");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user=dataSnapshot.getValue(User.class);
                    for (Chatlist chatlist: usersList){
                        if (user.getId().equals(chatlist.getId())){
                            users.add(user);
                        }
                    }
                }

                userAdapter=new UserAdapter(getContext(),users,true);
                chat_userRecyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}