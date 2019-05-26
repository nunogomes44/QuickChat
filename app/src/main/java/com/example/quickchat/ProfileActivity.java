package com.example.quickchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private String receiverUserID, senderUserId, Current_Stats;

    private ImageView userProfileImage;
    private TextView userProfileStatus, userProfileGender, userProfileID, userProfileLastSeen;
    private Button sendMessageRequestButton, DeclineMessageRequestButton;

    private DatabaseReference UserRef, chatRequestRef, ContactsRef;
    private FirebaseAuth mAuth;

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.toolbarProfileName);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat_Request");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();

        userProfileImage = (ImageView) findViewById(R.id.visit_profile_image);
        userProfileStatus = (TextView) findViewById(R.id.visit_profile_status);
        userProfileGender = (TextView) findViewById(R.id.gender);
        userProfileLastSeen = (TextView) findViewById(R.id.visit_user_last_seen);
        userProfileID = (TextView) findViewById(R.id.visit_profile_id);

        sendMessageRequestButton = (Button) findViewById(R.id.send_message_request_button);
        DeclineMessageRequestButton = (Button) findViewById(R.id.decline_message_request_button);
        Current_Stats = "new";
        senderUserId = mAuth.getCurrentUser().getUid();

        retrieveUserInfo();
    }

    private void retrieveUserInfo() {
        UserRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))){
                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.padrao).into(userProfileImage);

                    toolbar.setTitle(userName);
                    userProfileStatus.setText(userStatus);
                    userProfileGender.setText(gender);

                    if(dataSnapshot.child("userState").hasChild("state")){
                        String state = dataSnapshot.child("userState").child("state").getValue().toString();
                        String date = dataSnapshot.child("userState").child("date").getValue().toString();
                        String time = dataSnapshot.child("userState").child("time").getValue().toString();

                        if(state.equals("online")){
                            userProfileLastSeen.setText("online");
                        }
                        if(state.equals("offline")){
                            userProfileLastSeen.setText("last Seen: "+ date + " at " +time);
                        }
                    }

                    if(dataSnapshot.hasChild("phonenumber")){
                        String phonenumber = "+258 "+dataSnapshot.child("phonenumber").getValue().toString();
                        userProfileID.setText(phonenumber);
                    } else if(dataSnapshot.hasChild("email")){
                        String email = dataSnapshot.child("email").getValue().toString();
                        userProfileID.setText(email);
                    }

                    MessageChatRequests();

                }else{
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();

                    toolbar.setTitle(userName);
                    userProfileStatus.setText(userStatus);
                    userProfileGender.setText(gender);

                    if(dataSnapshot.child("userState").hasChild("state")){
                        String state = dataSnapshot.child("userState").child("state").getValue().toString();
                        String date = dataSnapshot.child("userState").child("date").getValue().toString();
                        String time = dataSnapshot.child("userState").child("time").getValue().toString();

                        if(state.equals("online")){
                            userProfileLastSeen.setText("online");
                        }
                        if(state.equals("offline")){
                            userProfileLastSeen.setText("last Seen: "+ date + " at " +time);
                        }
                    }

                    if(dataSnapshot.hasChild("phonenumber")){
                        String phonenumber = "+258 "+dataSnapshot.child("phonenumber").getValue().toString();
                        userProfileID.setText(phonenumber);
                    } else if(dataSnapshot.hasChild("email")){
                        String email = dataSnapshot.child("email").getValue().toString();
                        userProfileID.setText(email);
                    }

                    MessageChatRequests();

                    MessageChatRequests();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void MessageChatRequests() {

        chatRequestRef.child(senderUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiverUserID)){
                            String request_type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();

                            if(request_type.equals("sent")){
                                Current_Stats = "request_sent";
                                sendMessageRequestButton.setText("Cancel Chat Request");
                            } else if(request_type.equals("received"))
                            {
                                Current_Stats = "request_received";
                                sendMessageRequestButton.setText("Accept Chat Request");

                                DeclineMessageRequestButton.setVisibility(View.VISIBLE);
                                DeclineMessageRequestButton.setEnabled(true);
                                DeclineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelChatRequest();
                                    }
                                });
                            }
                        }
                        else{
                            ContactsRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(receiverUserID)){
                                                Current_Stats = "friends";
                                                sendMessageRequestButton.setText("Remove this Contacts");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        if(!senderUserId.equals(receiverUserID)){
            sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessageRequestButton.setEnabled(false);

                    if(Current_Stats.equals("new")){
                        SendChatRequest();
                    }
                    if(Current_Stats.equals("request_sent")){
                        CancelChatRequest();
                    }
                    if(Current_Stats.equals("request_received")){
                        AcceptChatRequest();
                    }
                    if(Current_Stats.equals("friends")){
                        RemoveSpecificContact();
                    }
                }
            });

        }else{
            sendMessageRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveSpecificContact() {
        ContactsRef.child(senderUserId).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ContactsRef.child(receiverUserID).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendMessageRequestButton.setEnabled(true);
                                                Current_Stats = "new";
                                                sendMessageRequestButton.setText("Send Message");

                                                DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineMessageRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptChatRequest() {
        ContactsRef.child(senderUserId).child(receiverUserID)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ContactsRef.child(receiverUserID).child(senderUserId)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                chatRequestRef.child(senderUserId).child(receiverUserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    chatRequestRef.child(receiverUserID).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    sendMessageRequestButton.setEnabled(true);
                                                                                    Current_Stats = "friends";
                                                                                    sendMessageRequestButton.setText("Remove this Contact");

                                                                                    DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                                                    DeclineMessageRequestButton.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });


                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    private void CancelChatRequest() {
        chatRequestRef.child(senderUserId).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            chatRequestRef.child(receiverUserID).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendMessageRequestButton.setEnabled(true);
                                                Current_Stats = "new";
                                                sendMessageRequestButton.setText("Send Message");

                                                DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineMessageRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void SendChatRequest() {
        chatRequestRef.child(senderUserId).child(receiverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            chatRequestRef.child(receiverUserID).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendMessageRequestButton.setEnabled(true);
                                                Current_Stats = "request_sent";
                                                sendMessageRequestButton.setText("Cancel Chat Request");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

}
