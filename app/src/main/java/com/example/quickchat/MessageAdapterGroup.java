package com.example.quickchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapterGroup extends RecyclerView.Adapter<MessageAdapterGroup.MyViewHolder> {

    private List<MessagesGroup> userMessagesList;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;

    public MessageAdapterGroup(List<MessagesGroup> userMessagesList){
        this.userMessagesList = userMessagesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_messages_layout_group, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);

        mAuth = FirebaseAuth.getInstance();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final String messageSenderID = mAuth.getCurrentUser().getUid();
        MessagesGroup messages = userMessagesList.get(position);

        String fromUserID = messages.getFrom();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("image")){
                    String receiverImage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverImage).placeholder(R.drawable.padrao).into(holder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverProfileImage.setVisibility(View.GONE);
        holder.receiverMessageName.setVisibility(View.GONE);
        holder.receiverMessageTime.setVisibility(View.GONE);

        holder.senderMessageText.setVisibility(View.GONE);
        holder.senderMessageTime.setVisibility(View.GONE);

        holder.senderMessageDisappear.setVisibility(View.GONE);
        holder.receiverMessageDisappear.setVisibility(View.GONE);

            if(fromUserID.equals(messageSenderID)){
                holder.senderMessageDisappear.setVisibility(View.VISIBLE);
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageTime.setVisibility(View.VISIBLE);

                //holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMessageText.setText(messages.getMessage());
                holder.senderMessageTime.setText(messages.getTime());
            }
            else{
                holder.receiverMessageDisappear.setVisibility(View.VISIBLE);
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverMessageName.setVisibility(View.VISIBLE);
                holder.receiverMessageTime.setVisibility(View.VISIBLE);
                //holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                holder.receiverMessageName.setText(messages.getName());
                holder.receiverMessageText.setText(messages.getMessage());
                holder.receiverMessageTime.setText(messages.getTime());
            }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMessageText, senderMessageTime, receiverMessageText, receiverMessageName, receiverMessageTime;
        public CircleImageView receiverProfileImage;
        public LinearLayout senderMessageDisappear, receiverMessageDisappear;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text_group);
            senderMessageTime = (TextView) itemView.findViewById(R.id.sender_message_time_group);

            receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image_group);
            receiverMessageName = (TextView) itemView.findViewById(R.id.receiver_message_name_group);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text_group);
            receiverMessageTime = (TextView) itemView.findViewById(R.id.receiver_message_time_group);

            senderMessageDisappear = (LinearLayout) itemView.findViewById(R.id.disappear_receiver_layout);
            receiverMessageDisappear = (LinearLayout) itemView.findViewById(R.id.disappear_send_layout);
        }
    }
}