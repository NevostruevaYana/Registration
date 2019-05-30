package com.example.registration;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.registration.Models.Chatroom;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatroomAdap extends RecyclerView.Adapter<ChatroomAdap.ViewHolder> {

    private Context mContext;
    private List<Chatroom> mChatrooms;

    public ChatroomAdap(Context mContext, List<Chatroom> mChatrooms){
        this.mChatrooms = mChatrooms;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);
        return new ChatroomAdap.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Chatroom chatroom = mChatrooms.get(i);
        final String chatroomName = chatroom.getChatroomName();
        String chatroomImageUrl = chatroom.getChatroomImageUrl();

        viewHolder.chatroomName.setText(chatroomName);
        Picasso.get().load(chatroomImageUrl).into(viewHolder.chatroom_image);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Main_chat_activity.class);
                intent.putExtra("chatroomName", chatroomName);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChatrooms.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView chatroomName;
        ImageView chatroom_image;

        ViewHolder(View itemView){
            super(itemView);
            chatroomName = itemView.findViewById(R.id.item_username);
            chatroom_image = itemView.findViewById(R.id.item_profile_image);
        }
    }
}
