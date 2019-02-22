package com.hsl_firebase.alves.pap_firebase_hsl;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    //Recycler Layout Variable
    private RecyclerView mRequestList;

    //Firebase Variables

    private DatabaseReference mRequestListDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;

    //String to get uid
    private String mCurrent_user_id;

    // Variable to see main layout
    private View mMainView;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);
        //Recycler view stuff
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRequestList = (RecyclerView) mMainView.findViewById(R.id.chat_list);
        mRequestList.setHasFixedSize(true);
        mRequestList.setLayoutManager(linearLayoutManager);

        //Gertting the current user id reference, and put the vaue in the string
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        //Seetting the databaseReference into variables
        //-- Link to child Request
        mRequestListDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
        mRequestListDatabase.keepSynced(true);

        //-- Link to child USERS
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query conversationQuery = mRequestListDatabase.orderByChild("request_type").equalTo("received");

        FirebaseRecyclerOptions<Friend_Req> options=
                new FirebaseRecyclerOptions.Builder<Friend_Req>()
                        .setQuery(conversationQuery,Friend_Req.class)
                        .setLifecycleOwner(this)
                        .build();

        FirebaseRecyclerAdapter<Friend_Req, RequestViewHolder> firebaseRequestAdapter = new FirebaseRecyclerAdapter<Friend_Req, RequestViewHolder>(options) {


            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new RequestViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.users_single_layout, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Friend_Req model) {
                holder.setDate(model.getRequest_type());

                final String list_user_id = getRef(position).getKey();

                mUsersDatabase.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        holder.setName(userName);
                        holder.setUsersImage(userThumb, getContext());

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence options[] = new CharSequence[]{"Open profile"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //Too see profile
                                        if(which == 0){
                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                            profileIntent.putExtra("user_id", list_user_id);
                                            startActivity(profileIntent);
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

        mRequestList.setAdapter(firebaseRequestAdapter);
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public RequestViewHolder(View itemView) {
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



    }
}
