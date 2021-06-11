package com.example.chatproj2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {
    Button loginActivityBtn,signupActivityBtn;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
//        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
//        if(firebaseUser != null){
//            startActivity(new Intent(StartActivity.this,MainActivity.class));
//            finish();
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        loginActivityBtn=findViewById(R.id.loginActivityButton);
        signupActivityBtn=findViewById(R.id.signupActivityButton);



        loginActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this,Login.class));
            }
        });

        signupActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this,Signup.class));

            }
        });
    }
}