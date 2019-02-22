package com.hsl_firebase.alves.pap_firebase_hsl;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.Call;

public class SettingsActivity extends AppCompatActivity  implements View.OnClickListener {

    //Android Layout

    private CircleImageView image_profile;

    private TextView mName, mStatus;

    private EditText MStatus;

    private Button mSaveStatusbtn, MsAVED, setting_image_btn;

    // Progresses

    private ProgressDialog mProgress, mProgressDialog;

    ProgressBar progressBar;

    //User profile

    private static final int GALLERY_PICK = 1;

    private StorageReference mImageStorage;

  //  private Uri imageUri, ImageUri;

    private StorageTask<UploadTask.TaskSnapshot> uploadTask;

    // Firebase variables



    private DatabaseReference mUserDatabase, mStatusDatabase, reference;

    private FirebaseUser mCurrentUser, MCurrentUser, fuser;

    private FirebaseAuth firebaseUser, mAuth;

    private FirebaseDatabase mdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //FireBase Stuff

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance();

        mdatabase = FirebaseDatabase.getInstance();

        //mImageStorage = FirebaseStorage.getInstance().getReference();



        //progress

        mProgress = new ProgressDialog(this);

        progressBar = findViewById(R.id.progressbar);

        //Linking variables to layout

        MsAVED = findViewById(R.id.saving_status);

        MsAVED.setOnClickListener(this);

        image_profile = findViewById(R.id.image_profile);

        image_profile.setOnClickListener(this);

        mName = findViewById(R.id.settings_display_name);

        mStatus = findViewById(R.id.settings_status);

        mSaveStatusbtn = findViewById(R.id.settings_status_btn);

        mSaveStatusbtn.setOnClickListener(this);

        MStatus = findViewById(R.id.newstatus);

        setting_image_btn = findViewById(R.id.settings_image_btn);

        setting_image_btn.setOnClickListener(this);

        //Invoque user data

        gettindcurrentdatafromuser();

    }

    //Will update the status of the user

    private void updateuserstatus() {
        //progressDialog

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Status");
        mProgress.setMessage("Saving your status ");
        mProgress.show();

        //StorageFirebase

        String status = MStatus.getText().toString();

        MCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = MCurrentUser.getUid();

        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mProgress.dismiss();
                } else {
                    Toast.makeText(SettingsActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Getting Current User

    private void gettindcurrentdatafromuser() {

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        /*FirebaseUser user = mAuth.getInstance().getCurrentUser();
        if(user.getPhotoUrl() !=  null){
            Glide.with(this). load(user.getPhotoUrl().toString()).into(mDisplayImage);
        }*/

        // Will get data from the current user
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mUserDatabase.keepSynced(true);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("images").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                mName.setText(name);
                mStatus.setText(status);
                if(!image.equals("")){
                    //Glide.with(SettingsActivity.this).load(image).into(image_profile);
                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.ic_add).into(image_profile, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.ic_add).into(image_profile);

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    // Buttons of layout

    @Override
    public void onClick(View v) {
        //Button to alow the change of status
        if (mSaveStatusbtn == v) {
            //Will change to a previus layout
            mSaveStatusbtn.setVisibility(View.GONE);
            mStatus.setVisibility(View.GONE);
            MStatus.setVisibility(View.VISIBLE);
            MsAVED.setVisibility(View.VISIBLE);
        }

        //Button to save status
        if (MsAVED == v) {
            //This will restore the layout and save the status
            //This will edit user status
            updateuserstatus();
            mSaveStatusbtn.setVisibility(View.VISIBLE);
            mStatus.setVisibility(View.VISIBLE);
            MStatus.setVisibility(View.GONE);
            MsAVED.setVisibility(View.GONE);
        }

        if (setting_image_btn == v) {
            //Will change image of user profile
            //setting_image_btn.setVisibility(View.GONE);
           // mSaveStatusbtn.setVisibility(View.VISIBLE);
            openImage();
        }

    }
   // will open gallery to user pick a image
    private void openImage() {

        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"), GALLERY_PICK);


        /*CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            //String imageUri = data.getDataString();

            Uri imageUri = data.getData();

            CropImage.activity(imageUri).setAspectRatio(1,1).start(this);
            //Toast.makeText(this, imageUri, Toast.LENGTH_SHORT).show();
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Uploading");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                final Uri resultUri = result.getUri();

                final File thumb_filePath = new File(resultUri.getPath());

                try {
                    final Bitmap thumb_bitmap = new Compressor(this).setMaxWidth(200).setMaxHeight(200).setQuality(75).compressToBitmap(thumb_filePath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                    String current_user_id = mCurrentUser.getUid();
                    final StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id+".jpg");
                    final StorageReference thump_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + "jpg");
                    uploadTask = filepath.putFile(resultUri);

                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return filepath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull final Task<Uri> task) {
                            if (task.isSuccessful()) {
                                final Uri downloadUri = resultUri;
                                final String mUri = downloadUri.toString();


                                UploadTask uploadTask = thump_filepath.putBytes(thumb_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                        Uri downloaduuri = resultUri;
                                        final String thumb_download = downloaduuri.toString();

                                        if(thumb_task.isSuccessful()){

                                            Map update_hashmap = new HashMap<>();
                                            update_hashmap.put("images", mUri);
                                            update_hashmap.put("thumb_image", thumb_download);

                                            mUserDatabase.updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if(task.isSuccessful()){

                                                        mProgressDialog.dismiss();

                                                        Toast.makeText(SettingsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });

                                        }
                                    }
                                });

                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }



    /*public static String random(){
        Random generator = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        int randomlength = generator.nextInt(10);
        char tempchar;
        for(int i = 0; i < randomlength; i++){
            tempchar =(char) (generator.nextInt(96) + 32);
            stringBuilder.append(tempchar);
        }
        return stringBuilder.toString();
    }*/
}


