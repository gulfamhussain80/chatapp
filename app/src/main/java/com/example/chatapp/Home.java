package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Models.Dialogs;
import com.example.chatapp.Models.Message;
import com.example.chatapp.Models.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    private TextView textViewResults;
    DialogsList dialogsList;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    ArrayList<Dialogs> dialogsArrayList = new ArrayList<Dialogs>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textViewResults = findViewById(R.id.text_view_result);
        dialogsList = (DialogsList)findViewById(R.id.chat_dialog);

        DialogsListAdapter dialogsListAdapter = new DialogsListAdapter<Dialogs>(new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView,@Nullable String url, @Nullable Object payload) {
                if(!url.equals(""))
                    Picasso.get().load(url).into(imageView);
            }
        });
        dialogsList.setAdapter(dialogsListAdapter);


     //AddDialog();
     getDialogsList();
     //dialogsListAdapter.setItems(dialogsArrayList);


    }
    public void AddDialog(){
       User user = User.UserfromFirebaseUser(FirebaseAuth.getInstance().getCurrentUser());
       Message message = new Message("Hi there!",user);
       Dialogs dialogs = new Dialogs(message);
       dialogsArrayList.add(dialogs);
        firestore.collection("Dialogs")
                .add(dialogs.hashMap())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Dialog list", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Dialog list", "Error adding document", e);
                    }
                });

    }
    private void getDialogsList() {

        firestore.collection("Dialogs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Chat List", document.getId() + " => " + document.getData());
                               Dialogs  dialogs= document.toObject(Dialogs.class);
                                DialogsListAdapter dialogsListAdapter = (DialogsListAdapter) dialogsList.getAdapter();
                                dialogsListAdapter.addItem(dialogs);

                            }
                        } else {
                            Log.d("Chat List", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.idBtnLogout:
                AuthUI.getInstance()
                        .signOut(Home.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Home.this, "User Signed Out successfully", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(Home.this, com.example.chatapp.MainActivity.class);
                                startActivity(i);
                            }});
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

