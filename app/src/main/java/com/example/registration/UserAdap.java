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

import com.example.registration.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdap extends RecyclerView.Adapter<UserAdap.ViewHolder> {

    private String username2;

    private Context mContext;
    private List<User> mUsers;

    public UserAdap(Context mContext, List<User> mUsers){
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);
        return new UserAdap.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final User user = mUsers.get(i);
        final String username1 = user.getUsername();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("/users/" + firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username2 = user.getUsername();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewHolder.username.setText(user.getUsername());
        Picasso.get().load(user.getProfileImageUrl()).into(viewHolder.profile_image);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatroomName(username1, username2);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView username;
        ImageView profile_image;

        ViewHolder(View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.item_username);
            profile_image = itemView.findViewById(R.id.item_profile_image);
        }
    }
    private void chatroomName(String username1, String username2){
        final String chatroomName = "@" + username1 + "@" + username2;
        final String otherChatroomName = "@" + username2 + "@" + username1;
        final String[] finalname = new String[1];

        Query chatroomNameQuery = FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("chatroomName").equalTo(chatroomName);
        chatroomNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0){
                    finalname[0] = chatroomName;
                }
                else{
                    finalname[0] = otherChatroomName;
                }
                IntentWithData(finalname[0]);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void IntentWithData(String chatroomName){
        Intent intent = new Intent(mContext, Main_chat_activity.class);
        intent.putExtra("chatroomName", chatroomName);
        mContext.startActivity(intent);
    }
}