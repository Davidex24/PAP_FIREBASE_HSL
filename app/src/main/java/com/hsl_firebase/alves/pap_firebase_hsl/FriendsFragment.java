package com.hsl_firebase.alves.pap_firebase_hsl;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.HolderFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    //Layout Variable
    private RecyclerView mFriendsList;

    //Firebase Variables
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;

    // Normal String
    private String mCurrent_user_id;


    //This variable will create all the view of the friends fragment
    private View mMainView;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);


        //Linking variables to Recycler View
        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.users_list);
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        //Getting Current Users
        mAuth = FirebaseAuth.getInstance();


        //Setting the value of user id to the string mCurrent_user_id

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        //Refering the path of reference to the child Friends

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);

        //Refering the path of reference to the child Users

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mFriendsDatabase.keepSynced(true);


        mRootRef = FirebaseDatabase.getInstance().getReference();

        // Inflate the layout for this fragment*/
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> options=
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(mFriendsDatabase,Friends.class)
                        .setLifecycleOwner(this)
                        .build();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options){
            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new FriendsViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.users_single_layout, viewGroup, false));

            }

            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Friends model) {
                holder.setDate(model.getDate());

                final String list_user_id = getRef(position).getKey();

                mUsersDatabase.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        holder.setName(userName);
                        holder.setUsersImage(userThumb, getContext());
                        if (dataSnapshot.hasChild("online")){

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(userOnline);;

                        }


                        //When press on user
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence options[] = new CharSequence[]{"Open profile", "Send Message","Unfriend this person"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //If user want see someone progfile of his friend list
                                        if(which == 0){
                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                            profileIntent.putExtra("user_id", list_user_id);
                                            startActivity(profileIntent);
                                        }

                                        //Open chatFriend activity
                                        if (which == 1){

                                            Intent chatFriend = new Intent(getContext(), ChatFriend.class);
                                            chatFriend.putExtra("user_id", list_user_id);
                                            chatFriend.putExtra("user_name", userName);
                                            startActivity(chatFriend);

                                        }

                                        //Delete friend
                                        if(which == 2){

                                            Map unfriendMap = new HashMap();
                                            unfriendMap.put("Friends/" + mCurrent_user_id + "/" + list_user_id, null);
                                            unfriendMap.put("Friends/" + list_user_id + "/" + mCurrent_user_id, null);

                                            mRootRef.updateChildren(unfriendMap);

                                        }

                                    }
                                });
                                //Will show options
                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };


        //Will update RecyclerView when user add a new friend
        mFriendsList.setAdapter(friendsRecyclerViewAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(date);

        }

        public void setName(String username){
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(username);
        }

        public void setUsersImage(String thumb_image, Context ctx){

            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);

            Glide.with(ctx).load(thumb_image).into(userImageView);
        }

        public void setUserOnline(String online_status) {

            CircleImageView userOnlineView = (CircleImageView) mView.findViewById(R.id.user_single_online_look);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.GONE);

            }

        }
    }

}
