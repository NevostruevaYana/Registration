package com.example.registration;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.registration.Models.Message;

class MessageViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder{
    View view;
    private Context context;

    MessageViewHolder(View itemView){
        super(itemView);
        view = itemView;
        context = itemView.getContext();
    }

    void setMessage(Message message){
        TextView nameField = view.findViewById(R.id.message_user);
        TextView textField = view.findViewById(R.id.message_text);
        TextView timeField = view.findViewById(R.id.message_time);

        nameField.setText(message.getUser());
        textField.setText(message.getText());
        timeField.setText(message.getTime());
        //TODO: add a picture here
    }

}
