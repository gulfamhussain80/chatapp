package com.example.chatproj2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText send_email;
    Button reset_password_btn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        send_email=findViewById(R.id.send_email_edit_text);
        reset_password_btn=findViewById(R.id.reset_pass_btn);
        firebaseAuth=FirebaseAuth.getInstance();

        reset_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=send_email.getText().toString();

                if (email.equals("")){
                    Toast.makeText(ForgotPasswordActivity.this,"Must provide email",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ForgotPasswordActivity.this,Login.class));

                }else{
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ForgotPasswordActivity.this,"Check Email, Link has been sent",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ForgotPasswordActivity.this, Login.class));
                            }else{
                                String error=task.getException().getMessage();
                                Toast.makeText(ForgotPasswordActivity.this,error,Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
            }
        });
    }
}