package com.hsl_firebase.alves.pap_firebase_hsl;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class chat_menu extends AppCompatActivity {

    //Variables of layout stuff

    private FirebaseAuth mAuth;

    private DatabaseReference mUserRef;

    //Layout

    private ViewPager mViewPager;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_menu);

        //Linking layout stuff
        mToolbar = findViewById(R.id.main_page_toolbars);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Getting ID of user

        mAuth = FirebaseAuth.getInstance();
        //Getting data of current user
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        //Tabs of this activity
        mViewPager = findViewById(R.id.main_tabPager);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        if (mAuth.getCurrentUser() != null) {


            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)  {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_settings_btn){
            Intent settingsIntent = new Intent(this, SettingsActivity.class );
            startActivity(settingsIntent);
        }


        if(item.getItemId() == R.id.all_users){
            Intent canuwork = new Intent(this, UsersActivity.class);
            startActivity(canuwork);
        }

        return true;
    }

}
