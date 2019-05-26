package com.example.quickchat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsFragment extends Fragment {

    private View ContactsView;
    private RecyclerView myContactsList;

    private DatabaseReference ContactsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    public ContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ContactsView = inflater.inflate(R.layout.fragment_contacts, container, false);

        myContactsList = (RecyclerView) ContactsView.findViewById(R.id.contacts_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return ContactsView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ContactsRef, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {
                        String usersID = getRef(position).getKey();

                        UsersRef.child(usersID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    if (dataSnapshot.child("userState").hasChild("state")) {
                                        String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                        String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                        String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                        if (state.equals("online")) {
                                            holder.onlineIcon.setVisibility(View.VISIBLE);
                                        } else if (state.equals("offline")) {
                                            holder.onlineIcon.setVisibility(View.INVISIBLE);
                                        }
                                    } else {
                                        holder.onlineIcon.setVisibility(View.INVISIBLE);
                                    }

                                    if (dataSnapshot.hasChild("image")) {
                                        String userImage = dataSnapshot.child("image").getValue().toString();
                                        String profileStatus = dataSnapshot.child("status").getValue().toString();
                                        String profileName = dataSnapshot.child("name").getValue().toString();

                                        holder.userName.setText(profileName);
                                        holder.userStatus.setText(profileStatus);
                                        Picasso.get().load(userImage).placeholder(R.drawable.padrao).into(holder.profileImage);

                                    } else {
                                        String profileStatus = dataSnapshot.child("status").getValue().toString();
                                        String profileName = dataSnapshot.child("name").getValue().toString();

                                        holder.userName.setText(profileName);
                                        holder.userStatus.setText(profileStatus);

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        ContactsViewHolder viewHolder = new ContactsViewHolder(view);

                        return viewHolder;
                    }
                };

        myContactsList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder{

        TextView userName, userStatus;
        CircleImageView profileImage;
        ImageView onlineIcon;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            onlineIcon = (ImageView) itemView.findViewById(R.id.user_online_status);
        }
    }
}
