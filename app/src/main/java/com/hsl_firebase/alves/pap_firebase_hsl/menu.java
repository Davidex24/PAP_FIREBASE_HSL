package com.hsl_firebase.alves.pap_firebase_hsl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import io.grpc.Server;

public class menu extends AppCompatActivity implements View.OnClickListener {

    // Firebase Variables

    private FirebaseAuth firebaseAuth;

    //private TextView textViewUser, textViewPhone, textViewEmail;
   private  DatabaseReference mUserRef;
    private FirebaseDatabase firebaseDatabase;

    //Layout variables

    TextView seename;

    private Button buttonLogOut;

    private Button btntask,btnchat, btnteste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        firebaseAuth = FirebaseAuth.getInstance();
        // Check if were any user login if not go to launcher activity

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, launcher.class));
        }else {

           // mUserRef.child("online").setValue("true");
            mUserRef.child("online").setValue("true");
        }

        firebaseDatabase = FirebaseDatabase.getInstance();

        seename = findViewById(R.id.seename);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usuariosRef = rootRef.child("Users");
        DatabaseReference current_userRef = usuariosRef.child(uid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue(String.class);
                Log.d("TAG", name);
                seename.setText(name);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        current_userRef.addListenerForSingleValueEvent(eventListener);



        // Logout current session
        buttonLogOut = findViewById(R.id.buttonLogOut);

        buttonLogOut.setOnClickListener(this);
        //ATENÇÃO ISTO É UM TESTE

        btnteste = findViewById(R.id.btnteste);
        btnteste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Go to activity tasks
        btntask = findViewById(R.id.btntask);
        btntask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu.this, tasks.class);
                startActivity(intent);
            }
        });

        //Go to chat activity
        btnchat = findViewById(R.id.btnchat);
        btnchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu.this,chat_menu.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View v) {

        if(v == buttonLogOut){
            //End of session logout
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, launcher.class));

        }

     /*if(v == btntask){
         finish();
         startActivity(new Intent(this, tasks.class));
     }*/

    }

    /*@Override
    protected void onStop() {
        super.onStop();

        mUserRef.child("Online").setValue(false);
    }*/
}
