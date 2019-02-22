package com.hsl_firebase.alves.pap_firebase_hsl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import static com.hsl_firebase.alves.pap_firebase_hsl.R.mipmap.ic_arrowup;

public class launcher extends AppCompatActivity implements View.OnClickListener{

    //Language Stuff

    boolean IsCheckedPT;
    boolean IsCheckedEN;

    //CardView stuff

    private CardView CardViewLanguages;

    private ImageButton btnArrowdown, btnArrowUp;
    boolean isCheckedEN, isCheckedPT;
    //Layout variables
    private Button buttonLogin;

    private EditText editTextEmail, editTextPassword;

    private TextView textViewSignUp, userLoginTitle;

    private ProgressDialog progressDialog;

    //RadioButton Stuff

        private CheckBox CHECK_PT;

         private CheckBox CHECK_EN;

    //Firebase Variables

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        //CardView

        CardViewLanguages = findViewById(R.id.CardViewLanguages);
        btnArrowdown = findViewById(R.id.btnArrowDown);
        btnArrowdown.setOnClickListener(this);
        btnArrowUp = findViewById(R.id.btnArrowUp);
        btnArrowUp.setOnClickListener(this);

         //Firebase stuff

        firebaseAuth = FirebaseAuth.getInstance();

        //iF application close with user looged, next time she start, she will open with the last user logged
        if(firebaseAuth.getCurrentUser() != null){
            //
            finish();
            startActivity(new Intent(getApplicationContext(),menu.class));

        }

        userLoginTitle = findViewById(R.id.textView);

        buttonLogin = findViewById(R.id.buttonLogin);

        editTextEmail = findViewById(R.id.editTextEmail);

        editTextPassword = findViewById(R.id.editTextPassword);

        textViewSignUp = findViewById(R.id.textViewSignUp);

        progressDialog = new ProgressDialog(this);

        buttonLogin.setOnClickListener(this);

        textViewSignUp.setOnClickListener(this);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        //CHECKBOX STUFF
        CHECK_EN = findViewById(R.id.CHECK_EN);
        CHECK_PT = findViewById(R.id.CHECK_PT);
        //CHECK_EN.setChecked(true);


        // By pre definition the application starts in English
        isCheckedEN = CHECK_EN.isChecked();
        CHECK_PT.setBackgroundColor(Color.WHITE);
        CHECK_EN.setChecked(true);
        CHECK_EN.setBackgroundColor(Color.GRAY);
        CHECK_EN.setText("English");
        CHECK_PT.setText("Portuguese");
        editTextEmail.setHint("Insert your email");
        editTextPassword.setHint("Insert your password");
        textViewSignUp.setText("SignUp Here");
        userLoginTitle.setText("User Login");
        buttonLogin.setText("Sing In");

        CHECK_EN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheckedEN = CHECK_EN.isChecked();
                CHECK_PT.setChecked(false);
                CHECK_EN.setClickable(false);
                CHECK_PT.setClickable(true);
                CHECK_EN.setBackgroundColor(Color.GRAY);
                CHECK_PT.setBackgroundColor(Color.WHITE);
                CHECK_EN.setText("English");
                CHECK_PT.setText("Portuguese");
                editTextEmail.setHint("Insert your email");
                editTextPassword.setHint("Insert your password");
                textViewSignUp.setText("SignUp Here");
                userLoginTitle.setText("User Login");
                buttonLogin.setText("Sing In");

            }
        });

        CHECK_PT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isCheckedPT = CHECK_PT.isChecked();
                CHECK_EN.setChecked(false);
                CHECK_PT.setClickable(false);
                CHECK_EN.setClickable(true);
                CHECK_EN.setBackgroundColor(Color.WHITE);
                CHECK_PT.setBackgroundColor(Color.GRAY);
                CHECK_EN.setText("Inglês");
                CHECK_PT.setText("Português");
                editTextEmail.setHint("Insira o seu email");
                editTextPassword.setHint("Insira a sua password");
                textViewSignUp.setText("Registe-se Aqui");
                userLoginTitle.setText("LOGIN");
                buttonLogin.setText("ENTRAR");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        IsCheckedPT = this.getIntent().getBooleanExtra("checkBoxValuePT", false);
        IsCheckedEN = this.getIntent().getBooleanExtra("checkBoxValueEN", false);

        if (IsCheckedEN == true){
            isCheckedEN = CHECK_EN.isChecked();
            CHECK_PT.setBackgroundColor(Color.WHITE);
            CHECK_EN.setChecked(true);
            CHECK_PT.setChecked(false);
            CHECK_PT.setClickable(true);
            CHECK_EN.setClickable(false);
            CHECK_EN.setBackgroundColor(Color.GRAY);
            CHECK_EN.setText("English");
            CHECK_PT.setText("Portuguese");
            editTextEmail.setHint("Insert your email");
            editTextPassword.setHint("Insert your password");
            textViewSignUp.setText("SignUp Here");
            userLoginTitle.setText("User Login");
            buttonLogin.setText("Sing In");
        }

        if (IsCheckedPT == true){

            isCheckedPT = CHECK_PT.isChecked();
            CHECK_EN.setChecked(false);
            CHECK_PT.setChecked(true);
            CHECK_PT.setClickable(false);
            CHECK_EN.setClickable(true);
            CHECK_EN.setBackgroundColor(Color.WHITE);
            CHECK_PT.setBackgroundColor(Color.GRAY);
            CHECK_EN.setText("Inglês");
            CHECK_PT.setText("Português");
            editTextEmail.setHint("Insira o seu email");
            editTextPassword.setHint("Insira a sua password");
            textViewSignUp.setText("Registe-se Aqui");
            userLoginTitle.setText("LOGIN");
            buttonLogin.setText("ENTRAR");

        }

       /* isCheckedEN = CHECK_EN.isChecked();
        CHECK_PT.setBackgroundColor(Color.WHITE);
        CHECK_EN.setChecked(true);
        CHECK_EN.setBackgroundColor(Color.GRAY);
        CHECK_EN.setText("English");
        CHECK_PT.setText("Portuguese");
        editTextEmail.setHint("Insert your email");
        editTextPassword.setHint("Insert your password");
        textViewSignUp.setText("SignUp Here");
        userLoginTitle.setText("User Login");
        buttonLogin.setText("Sing In");*/
    }

    @Override
    public void onClick(View view) {
        if(view == buttonLogin ){
            UserLogin();
        }

        if(view == textViewSignUp ){

            //open register activity here

            if(CHECK_EN.isChecked() == true){
                Intent goRegist = new Intent(this, Register.class);
                goRegist.putExtra("checkBoxValueEN", isCheckedEN);
                startActivity(goRegist);
                finish();
            }
            if(CHECK_PT.isChecked() == true){
                Intent goRegist = new Intent(this, Register.class);
                goRegist.putExtra("checkBoxValuePT", isCheckedPT);
                startActivity(goRegist);
                finish();
            }

        }

        if (view == btnArrowdown){
            arrowShowUp();
        }
        if (view == btnArrowUp){
            arrowHide();
        }
    }

    private void arrowHide() {

        if(btnArrowUp.isPressed()){
            btnArrowdown.setVisibility(View.VISIBLE);
            btnArrowUp.setVisibility(View.GONE);
            CHECK_EN.setVisibility(View.GONE);
            CHECK_PT.setVisibility(View.GONE);
            CardViewLanguages.setLayoutParams(new RelativeLayout.LayoutParams(1500, 90));
        }
    }

    private void arrowShowUp() {
        if(btnArrowdown.isPressed()){

            btnArrowdown.setVisibility(View.GONE);
            btnArrowUp.setVisibility(View.VISIBLE);
            CHECK_EN.setVisibility(View.VISIBLE);
            CHECK_PT.setVisibility(View.VISIBLE);
            CardViewLanguages.setLayoutParams(new RelativeLayout.LayoutParams(1500, 400));
        }

    }

    private void UserLogin() {
        if (CHECK_EN.isChecked() == true) {
            String email = editTextEmail.getText().toString().trim();

            String password = editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {

                //IF user didnt fill camp email
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();

                return;
            }
            if (TextUtils.isEmpty(password)) {

                //IF user didnt fill camp password
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();

                return;
            }

            //if everithing ok go progressDialog

            progressDialog.setMessage("Loging User");

            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                progressDialog.dismiss();

                                String current_user_id = firebaseAuth.getCurrentUser().getUid();

                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        // If email and password are ok, it´s time to check if the email are verified
                                        checkEmailVerification();

                                    }
                                });

                            } else {
                                // User did something wrong
                                Toast.makeText(launcher.this, "Check your email and password", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }

        if (CHECK_PT.isChecked() == true) {
            String email = editTextEmail.getText().toString().trim();

            String password = editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {

                //IF user didnt fill camp email
                Toast.makeText(this, "Por favor, Introduza o seu email", Toast.LENGTH_SHORT).show();

                return;
            }
            if (TextUtils.isEmpty(password)) {

                //IF user didnt fill camp password
                Toast.makeText(this, "Por favor, Introduza a sua password", Toast.LENGTH_SHORT).show();

                return;
            }

            //if everithing ok go progressDialog

            progressDialog.setMessage("Entrando");

            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                progressDialog.dismiss();

                                String current_user_id = firebaseAuth.getCurrentUser().getUid();

                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        // If email and password are ok, it´s time to check if the email are verified
                                        checkEmailVerification();

                                    }
                                });

                            } else {
                                // User did something wrong
                                Toast.makeText(launcher.this, "Verifique o seu Email", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }
    private void checkEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();

        if(emailflag){
            //go to menu if email are verified
            if (CHECK_PT.isChecked() == true) {
                finish();
                Intent intent = new Intent(getApplicationContext(),menu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            if (CHECK_EN.isChecked() == true) {
                finish();
                Intent intent = new Intent(getApplicationContext(),menu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        } else {
            // If email aren't verified will show a message warning to check the email
            if(CHECK_EN.isChecked() == true){
                Toast.makeText(this, "Verify your email", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
            }

            if(CHECK_PT.isChecked() == true){
                Toast.makeText(this, "Verifique o seu email", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
            }
        }
    }

}
