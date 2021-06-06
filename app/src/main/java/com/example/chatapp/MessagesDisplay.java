package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.Models.Dialogs;
import com.example.chatapp.Models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import android.os.Bundle;

public class MessagesDisplay extends AppCompatActivity {
    MessagesList messagesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_display);
        messagesList = (MessagesList) findViewById(R.id.messagesList);
        MessagesListAdapter<Message> adapter = new MessagesListAdapter<>(FirebaseAuth.getInstance().getCurrentUser().getUid(), null);
        messagesList.setAdapter(adapter);
        Dialogs dialogs = (Dialogs) getIntent().getSerializableExtra("Dialogs");
        adapter.addToStart((Message) dialogs.getLastMessage(), true);

    }
}