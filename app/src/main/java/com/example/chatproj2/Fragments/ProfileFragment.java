package com.example.chatproj2.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatproj2.Models.User;
import com.example.chatproj2.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    CircleImageView user_profile_pic;
    TextView user_profile_username;
    FirebaseUser firebaseUser;
    DatabaseReference dbReference;
    StorageReference storageReference;
    private static final int IMAGE_REQ=1;
    private Uri imageUri;
    private StorageTask uploadTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        user_profile_username=view.findViewById(R.id.profile_user_username);
        user_profile_pic=view.findViewById(R.id.user_profile_pic);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference("uploads");

        dbReference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                user_profile_username.setText(user.getUsername());
                if(user.getImageUrl().equals("imgurl")){
                    user_profile_pic.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getContext()).load(user.getImageUrl()).into(user_profile_pic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        user_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
        return view;
    }

    private void openImage(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQ);

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){

        final ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if(imageUri != null){
            final StorageReference finalReference = storageReference.child(System.currentTimeMillis()+
                    "." + getFileExtension(imageUri));
            uploadTask=finalReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (! task.isSuccessful()){
                        throw task.getException();
                    }
                    return finalReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        String uri=downloadUri.toString();
                        dbReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> hashMap=new HashMap<>();
                        hashMap.put("imageUrl",uri);
                        dbReference.updateChildren(hashMap);
                        progressDialog.dismiss();
                    }
                    else{
                        Toast.makeText(getContext(),"Failed to Upload Image",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }else{
            Toast.makeText(getContext(),"No Image selected!",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==IMAGE_REQ && resultCode==RESULT_OK && data != null && data.getData() != null){
            imageUri=data.getData();
            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(),"Upload in Progress",Toast.LENGTH_LONG).show();
            }else{
                uploadImage();
            }
        }else{
            Log.d("TEST","herebro");
        }
    }
}