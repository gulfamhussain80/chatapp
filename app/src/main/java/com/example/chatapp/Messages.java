package com.example.chatapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.Models.Dialogs;
import com.example.chatapp.Models.Message;
import com.example.chatapp.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Messages#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Messages extends Fragment {
    MessagesList messagesList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Messages() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Messages.
     */
    // TODO: Rename and change types and number of parameters
    public static Messages newInstance(String param1, String param2) {
        Messages fragment = new Messages();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //Dialogs dialogs = requireArguments().getParcelable("dialogs");
        User user = User.UserfromFirebaseUser(FirebaseAuth.getInstance().getCurrentUser());
        Message message = new Message("Hi there!",user);
        View view= inflater.inflate(R.layout.fragment_messages, container, false);
        messagesList = (MessagesList) view.findViewById(R.id.messagesList);
        MessagesListAdapter<Message> adapter = new MessagesListAdapter<>(FirebaseAuth.getInstance().getCurrentUser().getUid(), null);
        messagesList.setAdapter(adapter);
        //boolean Scroll = true
        adapter.addToStart(message, true);
        return  view;
    }
}