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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_FINAL_LEFT=0;
    public static final int MSG_FINAL_RIGHT=1;
    private Context context;
    private List<Chat> chats;
    private String imageUrl;
    FirebaseUser firebaseUser;

    public MessageAdapter(Context context, List<Chat> chats,String imageurl) {
        this.context = context;
        this.chats = chats;
        this.imageUrl=imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_FINAL_RIGHT){
            View view= LayoutInflater.from(context).inflate(R.layout.chat_right_item,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }else{
            View view= LayoutInflater.from(context).inflate(R.layout.chat_left_item,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat=chats.get(position);
        holder.display_message.setText(chat.getMessage());
        if (imageUrl.equals("imgurl")){
            holder.message_user_profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(context).load(imageUrl).into(holder.message_user_profile_image);
        }
        if (position == chats.size()-1){
            if (chat.isIsseen()){
                holder.msg_seen.setText("Seen");
            }else{
                holder.msg_seen.setText("Delivered");
            }
        }else{
            holder.msg_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView display_message;
        public CircleImageView message_user_profile_image;
        public TextView msg_seen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            display_message=itemView.findViewById(R.id.msg_show_text);
            message_user_profile_image=itemView.findViewById(R.id.msg_profile_image);
            msg_seen=itemView.findViewById(R.id.msg_seen_indicator);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(chats.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_FINAL_RIGHT;
        }else{
            return MSG_FINAL_LEFT;
        }
    }
}

