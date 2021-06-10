package com.example.chatproj2.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.chatproj2.Adapters.UserAdapter;
import com.example.chatproj2.Models.User;
import com.example.chatproj2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;


public class UsersFragment extends Fragment {
    private RecyclerView user_recycler_view;
    private UserAdapter userAdapter;
    private List<User> users;
    EditText search_user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_users,container,false);
        user_recycler_view=view.findViewById(R.id.users_recycler_view);
        user_recycler_view.setHasFixedSize(true);
        user_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        users=new ArrayList<>();
        search_user=view.findViewById(R.id.search_user);
        search_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        readUsers();
        return view;
    }

    private void searchUsers(String s){
        FirebaseUser fuser=FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user=dataSnapshot.getValue(User.class);
                    if (! user.getId().equals(fuser.getUid())){
                        users.add(user);
                    }
                }
                userAdapter=new UserAdapter(getContext(),users,false);
                user_recycler_view.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbReference= FirebaseDatabase.getInstance().getReference("Users");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (search_user.getText().toString().equals("")){
                    users.clear();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        User user=dataSnapshot.getValue(User.class);
                        if(! user.getId().equals(firebaseUser.getUid())){
                            users.add(user);
                        }
                    }
                    userAdapter=new UserAdapter(getContext(),users,false);
                    user_recycler_view.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}