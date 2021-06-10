package com.example.chatproj2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Login extends AppCompatActivity {
    EditText loginEmailEditTxt, loginPassEditTxt;
    Button loginBtn;
    FirebaseAuth firebaseAuth;
    Toolbar toolbar;
    TextView forgetPasswordTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar=findViewById(R.id.toolbarId);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loginEmailEditTxt=findViewById(R.id.editTextEmailAddressLogin);
        loginPassEditTxt=findViewById(R.id.editTextNumberPasswordLogin);
        loginBtn=findViewById(R.id.loginButton);
        firebaseAuth=FirebaseAuth.getInstance();
        forgetPasswordTextview=findViewById(R.id.forgot_password_textview);

        forgetPasswordTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,ForgotPasswordActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail=loginEmailEditTxt.getText().toString();
                String userPass=loginPassEditTxt.getText().toString();
                if(userEmail.isEmpty() || userPass.isEmpty()){
                    Toast.makeText(Login.this,"One of the two fields is empty",Toast.LENGTH_LONG).show();
                }
                else{
                    firebaseAuth.signInWithEmailAndPassword(userEmail,userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent=new Intent(Login.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(Login.this,"Login Failed",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}