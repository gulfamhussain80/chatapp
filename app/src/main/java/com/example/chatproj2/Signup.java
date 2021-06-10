package com.example.chatproj2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Signup extends AppCompatActivity {
    EditText nameEditTxt,emailEditTxt,passwordEditTxt,usernameEditTxt;
    Button signupBtn;
    FirebaseAuth firebaseAuth;
    DatabaseReference dbReference;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        toolbar=findViewById(R.id.toolbarId);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nameEditTxt=findViewById(R.id.editTextNameSignup);
        emailEditTxt=findViewById(R.id.editTextEmailAddressSignup);
        usernameEditTxt=findViewById(R.id.editTextUsernameSignup);
        passwordEditTxt=findViewById(R.id.editTextNumberPasswordSignup);
        signupBtn=findViewById(R.id.signupButton);
        firebaseAuth=FirebaseAuth.getInstance();

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailEditTxt.getText().toString();
                String name=nameEditTxt.getText().toString();
                String username=usernameEditTxt.getText().toString();
                String password=passwordEditTxt.getText().toString();

                if(email.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty()){
                    Toast.makeText(Signup.this,"One or more Fields is Empty",Toast.LENGTH_LONG).show();
                }
                else{
                    signUpUser(email,name,username,password);
                }
            }
        });
    }

    private void signUpUser(String email,String name, String username,String password){
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String userId=firebaseUser.getUid();
                            dbReference= FirebaseDatabase.getInstance().getReference("Users").child(userId);
                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put("id",userId);
                            hashMap.put("username",username);
                            hashMap.put("name",name);
                            hashMap.put("imageUrl","imgurl");
                            hashMap.put("status","offline");
                            hashMap.put("search",username.toLowerCase());
                            dbReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent=new Intent(Signup.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            });
                        }
                        else{
                            Toast.makeText(Signup.this, "Sign Up Failed!", Toast.LENGTH_LONG).show();
                        }


                    }
                });
    }
}