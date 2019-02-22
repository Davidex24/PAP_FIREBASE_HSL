package com.hsl_firebase.alves.pap_firebase_hsl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity implements View.OnClickListener{

    //Layout variables

    private Button buttonRegister;

    private EditText editTextEmail, editTextPassword, editTextFullName, editTextPhone, confirmpassword;

    private TextView textViewSignin, textView;

    private ProgressDialog progressDialog;

    //Getting checkBox values

    boolean isCheckedPT;
    boolean isCheckedEN;
    //Firebase variables

    private FirebaseAuth firebaseAuth;

    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();

        //Edittext´s

        editTextFullName = findViewById(R.id.editTextFullName);

        editTextPhone = findViewById(R.id.editTextPhone);

        editTextEmail = findViewById(R.id.editTextEmail);

        editTextPassword = findViewById(R.id.editTextPassword);

        confirmpassword = findViewById(R.id.confirmpassword);

        //button register

        buttonRegister = findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(this);

        // text sign in
        textView = findViewById(R.id.textView);

        textViewSignin = findViewById(R.id.textViewSigin);

        textViewSignin.setOnClickListener(this);

        // end of sign in

        progressDialog = new ProgressDialog(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        isCheckedPT = this.getIntent().getBooleanExtra("checkBoxValuePT", false);
        isCheckedEN = this.getIntent().getBooleanExtra("checkBoxValueEN", false);

        if(isCheckedPT == true){
            textView.setText("Registo");
            editTextEmail.setHint("Insira seu email");
            editTextFullName.setHint("Insira seu nome completo");
            editTextPhone.setHint("Numero de telefone");
            editTextPassword.setHint("Insira uma password");
            confirmpassword.setHint("Confirme a password");
            buttonRegister.setText("REGISTAR");
            textViewSignin.setText("Já registado? Inicie a sessão!");
        }else

       /*if(isCheckedEN == true)*/{
            textView.setText("User Registration");
            editTextEmail.setHint("Insert your email");
            editTextFullName.setHint("Insert your Full Name");
            editTextPhone.setHint(" Phone number");
            editTextPassword.setHint("Insert your password");
            confirmpassword.setHint(" Confirm your password");
            buttonRegister.setText("REGISTER");
            textViewSignin.setText("Already Registed? Sign In!");
        }
    }

    public void registerUser(){

        //linking new variables to variables linked to layout

            final String name = editTextFullName.getText().toString().trim();

            final String email = editTextEmail.getText().toString().trim();

            final String password = editTextPassword.getText().toString().trim();

            final String c_password = confirmpassword.getText().toString().trim();

            final String phone = editTextPhone.getText().toString().trim();

            final String image = "";

            final String status = "";

            final String thumb_image = "";

            // If user dont fill name camp
        if(isCheckedEN == true){
            if(name.isEmpty()){
                editTextFullName.setError("Name is required");
                editTextFullName.requestFocus();
                return;
            }

            //IF user didnt fill camp email

            if(email.isEmpty()){
                editTextEmail.setError("Email is required");
                editTextEmail.requestFocus();
                return;
            }

            // If email already exists in database, this will warn him that he are already registed

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                editTextEmail.setError("Please insert a valid email");
                return;
            }


            //IF user didnt fill camp password

            if(password.isEmpty()){
                editTextPassword.setError("Password is required");
                editTextPassword.requestFocus();
                return;
            }

            //User have to put a password at least with 6 caracters

            if(password.length() < 6){
                editTextPassword.setError("Password minimum lenght is 6!");
                editTextPassword.requestFocus();
                return;
            }

            //User have to confirm password

            if(c_password.isEmpty()){
                confirmpassword.setError("Confirm your password");
                confirmpassword.requestFocus();
                return;
            }

            //If password and c_passoword dont are equal this will remind the user to put both equal

            if(!c_password.equals(password)){
                confirmpassword.setError("Both password have to be equal!");
                confirmpassword.requestFocus();
                return;
            }

            //IF user didnt fill camp Phone number

            if(phone.isEmpty()){
                editTextPhone.setError("Phone number is required");
                editTextPhone.requestFocus();
                return;
            }

            //User have to put a phone number with 9 caracters

            if(phone.length() <= 8 || phone.length() >= 10 ){
                editTextPhone.setError("Enter a valid phone number");
                editTextPhone.requestFocus();
                return;
            }

            //if user have his name has a password

            if(password.equals(name)){
                editTextPassword.setError("Your name can't be used as password");
                editTextPassword.requestFocus();
                return;
            }

            //if everithing ok go progressDialog
            progressDialog.setMessage("Registering User");

            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                UserInformation userInformation = new UserInformation(name, email, phone, image, status, thumb_image);

                                FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).setValue(userInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if(task.isSuccessful()){
                                            //user is successfully registed and send email Verification
                                            sendEmailVerification();
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                //If user already exists show a message
                                if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                    Toast.makeText(Register.this, "You are already registered", Toast.LENGTH_SHORT).show();
                                } else{
                                    Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }

                            }
                        }
                    });
        }

        if(isCheckedPT == true){
            // If user dont fill name camp

            if(name.isEmpty()){
                editTextFullName.setError("Nome é requerido!");
                editTextFullName.requestFocus();
                return;
            }

            //IF user didnt fill camp email

            if(email.isEmpty()){
                editTextEmail.setError("Email é requerido!");
                editTextEmail.requestFocus();
                return;
            }

            // If email already exists in database, this will warn him that he are already registed

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                editTextEmail.setError("Por favor, insere um email válido");
                return;
            }


            //IF user didnt fill camp password

            if(password.isEmpty()){
                editTextPassword.setError("PassWord é requerida! ");
                editTextPassword.requestFocus();
                return;
            }

            //User have to put a password at least with 6 caracters

            if(password.length() < 6){
                editTextPassword.setError("A PassWord tem que ter pelo menos 6 caracteres!");
                editTextPassword.requestFocus();
                return;
            }

            //User have to confirm password

            if(c_password.isEmpty()){
                confirmpassword.setError("Confirma a tua password");
                confirmpassword.requestFocus();
                return;
            }

            //If password and c_passoword dont are equal this will remind the user to put both equal

            if(!c_password.equals(password)){
                confirmpassword.setError("Ambas as passwords tem que combinar!");
                confirmpassword.requestFocus();
                return;
            }

            //IF user didnt fill camp Phone number

            if(phone.isEmpty()){
                editTextPhone.setError("O numero de telemovel é requerido!");
                editTextPhone.requestFocus();
                return;
            }

            //User have to put a phone number with 9 caracters

            if(phone.length() <= 8 || phone.length() >= 10 ){
                editTextPhone.setError("Insere um numero de telefone válido!");
                editTextPhone.requestFocus();
                return;
            }

            //if user have his name has a password

            if(password.equals(name)){
                editTextPassword.setError("Não podes usar o teu nome na password");
                editTextPassword.requestFocus();
                return;
            }

            //if everithing ok go progressDialog
            progressDialog.setMessage("Registando Utilizador");

            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                UserInformation userInformation = new UserInformation(name, email, phone, image, status, thumb_image);

                                FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).setValue(userInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if(task.isSuccessful()){
                                            //user is successfully registed and send email Verification
                                            sendEmailVerification();
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                //If user already exists show a message
                                if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                    Toast.makeText(Register.this, "Voce já está registado!", Toast.LENGTH_SHORT).show();
                                } else{
                                    Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }

                            }
                        }
                    });
        }

    }

    private void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        //Check if user isnt null
        if(firebaseUser != null){
            //send email to verify account
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){

                        // After been registered, will show a message to go check email
                        if (isCheckedEN == true){
                            Toast.makeText(Register.this, "Registered Successfully, Verification email sent!", Toast.LENGTH_SHORT).show();

                            firebaseAuth.signOut();

                            finish();

                            // go to launcher activity
                            Intent goMenu = new Intent(Register.this, launcher.class);
                            goMenu.putExtra("checkBoxValueEN", isCheckedEN);
                            startActivity(goMenu);
                            finish();
                        }

                        if (isCheckedPT == true){
                            Toast.makeText(Register.this, "Registado com Sucesso, verifique o seu email!", Toast.LENGTH_SHORT).show();

                            firebaseAuth.signOut();

                            Intent goMenu = new Intent(Register.this, launcher.class);
                            goMenu.putExtra("checkBoxValuePT", isCheckedPT);
                            startActivity(goMenu);
                            finish();
                        }


                    } else {

                        //if something went wrong
                         if (isCheckedEN){
                             Toast.makeText(Register.this, "Something wrong, Verication email wasn´t sent, and you aren't registered!!", Toast.LENGTH_SHORT).show();
                         }

                        if (isCheckedPT){
                            Toast.makeText(Register.this, "Ocorreu algum erro, A verificação de email não foi enviada e não está registado!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }



    @Override
    public void onClick(View view) {

        if(view == buttonRegister ){
            //Go to register user
            registerUser();
        }

        if(view == textViewSignin ){
            //Open launcher activity
            if(isCheckedPT == true){
                Intent goMenu = new Intent(this, launcher.class);
                goMenu.putExtra("checkBoxValuePT", isCheckedPT);
                startActivity(goMenu);
                finish();
            }

            if(isCheckedEN == true){
                Intent goMenu = new Intent(this, launcher.class);
                goMenu.putExtra("checkBoxValueEN", isCheckedEN);
                startActivity(goMenu);
                finish();
            }
        }

    }
}
