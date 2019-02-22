package com.hsl_firebase.alves.pap_firebase_hsl;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private List<Messages> mMessageList;
    //private DatabaseReference mUserDatabase;

    //Firebase variables
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout ,parent, false);
            return new MessageViewHolder(v);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageTextReceiver;
        public CircleImageView profileImage;
        public TextView messageSender;
        public MessageViewHolder(View view) {
            super(view);

            messageTextReceiver = view.findViewById(R.id.message_text_layout);
            profileImage = view.findViewById(R.id.message_profile_layout);
            messageSender = view.findViewById(R.id.message_sender);
        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();

        Messages c = mMessageList.get(i);

        //Who send the message
        String from_user = c.getFrom();

        if (from_user.equals(current_user_id)) {
            viewHolder.messageSender.setVisibility(View.VISIBLE);
            viewHolder.messageTextReceiver.setVisibility(View.INVISIBLE);
            viewHolder.profileImage.setVisibility(View.INVISIBLE);
            viewHolder.messageSender.setBackgroundColor(Color.WHITE);
            viewHolder.messageSender.setTextColor(Color.BLACK);
            viewHolder.messageSender.setText(c.getMessage());

        }

        if (!from_user.equals(current_user_id)){
            viewHolder.messageSender.setVisibility(View.INVISIBLE);
            viewHolder.messageTextReceiver.setVisibility(View.VISIBLE);
            viewHolder.profileImage.setVisibility(View.VISIBLE);
            viewHolder.messageTextReceiver.setBackgroundResource(R.drawable.message_text_background);
            viewHolder.messageTextReceiver.setTextColor(Color.WHITE);
            viewHolder.messageTextReceiver.setText(c.getMessage());

        }


    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }





}
