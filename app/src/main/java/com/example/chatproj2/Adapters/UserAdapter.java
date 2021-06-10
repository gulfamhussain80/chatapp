package com.example.chatproj2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatproj2.MessageActivity;
import com.example.chatproj2.Models.Chat;
import com.example.chatproj2.Models.User;
import com.example.chatproj2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<User> users;
    private boolean isChat;
    String lastAvbMsg;

    public UserAdapter(Context context, List<User> users,boolean isChat) {
        this.context = context;
        this.users = users;
        this.isChat=isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_layout,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user=users.get(position);
        holder.user_Username.setText(user.getUsername());
        if(user.getImageUrl().equals("imgurl")){
            holder.user_profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(context).load(user.getImageUrl()).into(holder.user_profile_image);
        }

        if (isChat){
            lastMessage(user.getId(),holder.last_msg_textview);
        }else{
            holder.last_msg_textview.setVisibility(View.GONE);
        }

        if (isChat){
            if(user.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }else{
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        }else{

            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, MessageActivity.class);
                intent.putExtra("userId",user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView user_Username;
        public CircleImageView user_profile_image;
        private CircleImageView img_on;
        private CircleImageView img_off;
        private TextView last_msg_textview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_Username=itemView.findViewById(R.id.user_username);
            user_profile_image=itemView.findViewById(R.id.user_profile_image);
            img_on=itemView.findViewById(R.id.img_online);
            img_off=itemView.findViewById(R.id.img_offline);
            last_msg_textview=itemView.findViewById(R.id.last_msg);
        }
    }

    private void lastMessage(final String userid,final TextView lastmsg){
        lastAvbMsg="def";
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbReference= FirebaseDatabase.getInstance().getReference("Chats");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat=dataSnapshot.getValue(Chat.class);
                    if (firebaseUser != null){
                    if ((chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) || (chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) ){
                        lastAvbMsg=chat.getMessage();
                    }}
                }
                switch (lastAvbMsg){
                    case "def":
                        lastmsg.setText("No New Message");
                        break;
                    default:
                        lastmsg.setText(lastAvbMsg);
                        break;
                }
                lastAvbMsg="def";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
