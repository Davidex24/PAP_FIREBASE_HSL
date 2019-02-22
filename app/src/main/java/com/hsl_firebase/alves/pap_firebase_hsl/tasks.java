package com.hsl_firebase.alves.pap_firebase_hsl;

import android.app.DatePickerDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class tasks extends AppCompatActivity implements View.OnClickListener {

    //Layout stuff
  //Varaibles to add a new task
    public MaterialEditText taskname, tasklocal, taskdate;

    FloatingActionButton add;

    final Calendar myCalendar = Calendar.getInstance();

    private static final  String TAG = "addtask";


    //Varaible to see tasks
    private ListView listView;

    //Listview stuff

    List<usertasks> usertasksList;

    //FireBase stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef, databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        //To set a date

        taskdate = findViewById(R.id.taskdate);
        taskdate.setOnClickListener(this);


        // Connecting variables to layout

        taskname = findViewById(R.id.taskname);
        tasklocal = findViewById(R.id.tasklocal);
        //taskdate = findViewById(R.id.taskdate);
        add=findViewById(R.id.add);
        add.setOnClickListener(this);
        listView = findViewById(R.id.list_view);



        //Access to my database

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        //Loading tasks
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(userID).child("Tasks");
        usertasksList = new ArrayList<>();

        loadingtasks();

    }

    private void loadingtasks() {
        //Load all tasks
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    usertasks usertaskss = userSnapshot.getValue(usertasks.class);
                    usertasksList.add(usertaskss);
                }
                UserTasksAdapter userTasksAdapter = new UserTasksAdapter(tasks.this,usertasksList);
                listView.setAdapter(userTasksAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    public void addtask(){
        Log.d(TAG, "onClick! Attempting to add object to database.");
        String taskName = taskname.getText().toString().trim();
        String taskLocal = tasklocal.getText().toString().trim();
        String taskDate = taskdate.getText().toString().trim();

        if(taskName.isEmpty()){
            taskname.setError("Name of the task required");
            taskname.requestFocus();
            return;
        }

        if(taskLocal.isEmpty()){
            tasklocal.setError("Local is required");
            tasklocal.requestFocus();
            return;
        }

        if(taskDate.isEmpty()){
            taskdate.setError("Date is required");
            taskdate.requestFocus();
            return;
        }

        if(taskDate.length() <= 9 || taskDate.length() >= 11){
            taskdate.setError("Valid date is required");
            taskdate.requestFocus();
            return;
        }

        if(!taskName.equals("") && !taskLocal.equals("") && !taskDate.equals("")){
            usertasks usertasks = new usertasks(taskName, taskLocal, taskDate);
            FirebaseUser user = mAuth.getCurrentUser();
            String userID = user.getUid();
            myRef.child(userID).child("Tasks").child(taskName).setValue(usertasks);

            // reset text
            taskname.setText("");
            tasklocal.setText("");
            taskdate.setText("");
            // Hint the fields
            taskname.setHint("Task Name");
            tasklocal.setHint("Task Local");
            taskdate.setHint("Task Date");
        }
    }

    @Override
    public void onClick(View v) {
        if(v == add){
            addtask();
            // startActivity(new Intent(this, addtask.class));
        }

        if(v == taskdate){

            DateDialog();

        }
    }


        public void DateDialog () {
            new DatePickerDialog(tasks.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        //Format date
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

        taskdate.setText(sdf.format(myCalendar.getTime()));
    }

}


