package com.example.chatproj2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatproj2.Fragments.ChatsFragment;
import com.example.chatproj2.Fragments.ProfileFragment;
import com.example.chatproj2.Fragments.UsersFragment;
import com.example.chatproj2.Models.Chat;
import com.example.chatproj2.Models.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    CircleImageView profile_image;
    TextView profile_username;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    DatabaseReference dbReference;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        profile_image=findViewById(R.id.profile_image);
        profile_username=findViewById(R.id.profile_username);
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        dbReference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                profile_username.setText(user.getUsername());
                if(user.getImageUrl().equals("imgurl")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final TabLayout tabLayout=findViewById(R.id.tablayout_main);
        final ViewPager viewPager=findViewById(R.id.viewpager_main);
        dbReference=FirebaseDatabase.getInstance().getReference("Chats");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
                int unread=0;
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat=dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && ! chat.isIsseen()){
                        unread++;
                    }
                }
                if (unread == 0){
                    viewPagerAdapter.addFragment(new ChatsFragment(),"Chats");
                }else{
                    viewPagerAdapter.addFragment(new ChatsFragment(),"("+unread+") Chats");
                }


                viewPagerAdapter.addFragment(new UsersFragment(),"Users");
                viewPagerAdapter.addFragment(new ProfileFragment(),"Profile");

                viewPager.setAdapter(viewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
        }
        return false;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private  ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private void userStatus(String status){
        dbReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        dbReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        userStatus("offline");
    }
}