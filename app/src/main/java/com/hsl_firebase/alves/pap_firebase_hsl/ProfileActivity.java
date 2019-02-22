package com.hsl_firebase.alves.pap_firebase_hsl;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    //Layout variables

    private CircleImageView mProfileImage;

    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;

    private Button mProfileSendReqBtn, mDeclineReq;

    private ProgressDialog mProgressDialog;

    //Strings
    private String user_id;
    public String mCurrent_state;

    // FireBase variables

    private DatabaseReference mRootRef, mUsersDatabase, mFriendReqDatabase, mFriendDatabase, mNotoficationDatabase;

    private FirebaseUser mCurrent_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Will get the username that user want see

        user_id = getIntent().getStringExtra("user_id");

        mProfileName = findViewById(R.id.profile_DisplayName);

        mProfileStatus = findViewById(R.id.profile_status);

        mProfileFriendsCount = findViewById(R.id.profile_totalFriends);

        mProfileSendReqBtn = findViewById(R.id.profile_send_req_btn);
        mProfileSendReqBtn.setOnClickListener(this);

        mDeclineReq = findViewById(R.id.profile_decline_btn);
        //mDeclineReq.setOnClickListener(this);

        mProfileImage = findViewById(R.id.profile_image);

        mCurrent_state = "not_friends";

        //Progress Dialog

        mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Loading User Profile");

        mProgressDialog.setCanceledOnTouchOutside(false);

        mProgressDialog.show();

        //Firebase stuff

        mNotoficationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("images").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                if (image.equals("")) {

                    mProfileImage.setImageResource(R.mipmap.ic_user);

                } else {

                    Glide.with(ProfileActivity.this).load(image).into(mProfileImage);

                }

                // Friends list

                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)) {

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if (req_type.equals("received")) {

                                mCurrent_state = "req_received";
                                mProfileSendReqBtn.setText("Accept Friend Request");

                                mDeclineReq.setVisibility(View.VISIBLE);

                                mDeclineReq.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        DeclineRequest();

                                    }
                                });

                            } else if (req_type.equals("sent")) {

                                mCurrent_state = "req_sent";
                                mProfileSendReqBtn.setText("Cancel Friend Request");

                                mDeclineReq.setVisibility(View.INVISIBLE);
                                mDeclineReq.setEnabled(false);

                            }

                            mProgressDialog.dismiss();

                        } else {

                            mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(user_id)) {

                                        mCurrent_state = "friends";
                                        mProfileSendReqBtn.setText("Unfriend this Person");

                                        mDeclineReq.setVisibility(View.INVISIBLE);
                                        mDeclineReq.setEnabled(false);

                                    }

                                    mProgressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    mProgressDialog.dismiss();

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });}

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        //pressing this buttom will send the  friendShip request
        if (v == mProfileSendReqBtn) {

            sendrequest();

        }

    }

    private void cancelRequest() {

        mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mProfileSendReqBtn.setEnabled(true);
                        mCurrent_state = "not_friends";
                        mProfileSendReqBtn.setText("Send Friend Request");

                        mDeclineReq.setVisibility(View.INVISIBLE);
                        mDeclineReq.setEnabled(false);

                    }
                });

            }
        });

    }

    //Send friend request method
    private void sendrequest() {

        //mProfileSendReqBtn.setEnabled(false);

        if (mCurrent_state.equals("not_friends")) {

            DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_id).push();
            String newNotificationId = newNotificationref.getKey();

            HashMap<String, String> notificationData = new HashMap<>();
            notificationData.put("from", mCurrent_user.getUid());
            notificationData.put("type", "request");

            Map requestMap = new HashMap();
            requestMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id + "/request_type", "sent");
            requestMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid() + "/request_type", "received");
            requestMap.put("notifications/" + user_id + "/" + newNotificationId, notificationData);

            mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {

                        Toast.makeText(ProfileActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                    } else {

                        mCurrent_state = "req_sent";
                        mProfileSendReqBtn.setText("Cancel Friend Request");

                    }

                    mProfileSendReqBtn.setEnabled(true);

                }
            });

        }

        //If user want cancel friend request this will send to a method

        if (mCurrent_state.equals("req_sent")) {

            cancelRequest();

        }

        // Request State

        if (mCurrent_state.equals("req_received")) {

            requestState();

        }

        if (mCurrent_state.equals("friends")) {

            DeleteFriend();

        }

    }

    //ATENÇÃO MAXIMA

    //Method to decline friend request
    private void DeclineRequest() {


        mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        mProfileSendReqBtn.setEnabled(true);
                        mCurrent_state = "not_friends";
                        mProfileSendReqBtn.setText("Send Friend Request");

                        mDeclineReq.setVisibility(View.INVISIBLE);
                        mDeclineReq.setEnabled(false);


                    }
                });

            }
        });


    }

    private void DeleteFriend() {
        //Small version

        Map unfriendMap = new HashMap();
        unfriendMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id, null);
        unfriendMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid(), null);

        mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                if (databaseError == null) {

                    mCurrent_state = "not_friends";
                    mProfileSendReqBtn.setText("Send Friend Request");

                    mDeclineReq.setVisibility(View.INVISIBLE);
                    mDeclineReq.setEnabled(false);

                } else {

                    String error = databaseError.getMessage();

                    Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();


                }

                mProfileSendReqBtn.setEnabled(true);

            }
        });

    }

    private void requestState() {

        final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

        Map friendsMap = new HashMap();
        friendsMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id + "/date", currentDate);
        friendsMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid() + "/date", currentDate);


        friendsMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id, null);
        friendsMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid(), null);


        mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                if (databaseError == null) {

                    mProfileSendReqBtn.setEnabled(true);
                    mCurrent_state = "friends";
                    mProfileSendReqBtn.setText("Unfriend this Person");

                    mDeclineReq.setVisibility(View.INVISIBLE);
                    mDeclineReq.setEnabled(false);

                } else {

                    String error = databaseError.getMessage();

                    Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();

                }

            }
        });
    }
}
