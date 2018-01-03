package com.example.bagle.app4h;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddHealth extends AppCompatActivity {
    //userInfo and database
    private DatabaseReference projectHealthRef;
    private DatabaseReference projectInfoRef;
    private FirebaseDatabase database;
    private String projectName;
    private String firebaseUserId;

    //date picker
    private DatePickerDialog.OnDateSetListener treatmentDateListener;
    private Calendar dateCalendar;
    private TextView dateTV;

    //ui elements
    private TextInputEditText speciesInput;
    private TextInputEditText conditionInput;
    private TextInputEditText productInput;
    private TextInputEditText commentsInput;

    //other variables
    private String id;
    private long dateNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_health);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //initialize variables
        getUserInfo();
        getDatabaseRef();

        //Initialize UI
        speciesInput = (TextInputEditText) findViewById(R.id.health_species_input);
        conditionInput = (TextInputEditText) findViewById(R.id.health_condition_input);
        productInput = (TextInputEditText) findViewById(R.id.health_product_used_input);
        commentsInput = (TextInputEditText) findViewById(R.id.health_comments_input);

        setInitialDate();
        //get the item info if there is any
        SharedPreferences editInfo = getSharedPreferences("healthInfo", Context.MODE_PRIVATE);
        String species = editInfo.getString("healthSpecies", "");
        String condition = editInfo.getString("healthCondition", "");
        String product = editInfo.getString("healthProductUsed", "");
        String comments = editInfo.getString("healthComments", "");
        String treatmentDate = editInfo.getString("healthDate", "");
        id = editInfo.getString("healthId", "");

        if (!(species.equals(""))) {
            speciesInput.setText(species);
            conditionInput.setText(condition);
            productInput.setText(product);
            commentsInput.setText(comments);
            dateTV.setText(treatmentDate);

            SimpleDateFormat f = new SimpleDateFormat("E, MMM d, yyyy", Locale.US);
            try {
                Date d = f.parse(treatmentDate);
                dateNumber = d.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SharedPreferences.Editor editor = editInfo.edit();
            editor.putString("healthSpecies", "");
            editor.putString("healthCondition", "");
            editor.putString("healthProductUsed", "");
            editor.putString("healthComments","");
            editor.putString("healthDate", "");
            editor.putString("healthId","");
            editor.apply();
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void setInitialDate() {
        dateCalendar = Calendar.getInstance();
        dateNumber = dateCalendar.getTimeInMillis();
        String today = new SimpleDateFormat("E, MMM d, yyyy", Locale.US).format(dateCalendar
                .getTime());
        dateTV.setText(today);
        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = dateCalendar.get(Calendar.YEAR);
                int month = dateCalendar.get(Calendar.MONTH);
                int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog deadlineDatePickerDialog = new DatePickerDialog(AddHealth.this,
                        android.R.style.Theme_DeviceDefault_Dialog, treatmentDateListener,
                        year, month, day);
                deadlineDatePickerDialog.show();
            }
        });

        treatmentDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                dateCalendar.set(Calendar.YEAR, year);
                dateCalendar.set(Calendar.MONTH, month);
                dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String deadline = new SimpleDateFormat("E, MMM d, yyyy", Locale.US).format
                        (dateCalendar.getTime());
                dateTV.setText(deadline);
                dateNumber = dateCalendar.getTimeInMillis();
            }
        };
    }


    private void getDatabaseRef() {
        database = FirebaseDatabase.getInstance();
        projectHealthRef = database.getReference("users").child(firebaseUserId).child
                ("projects").child(projectName).child("animalHealth");
        projectInfoRef = database.getReference("users").child(firebaseUserId).child
                ("projects").child(projectName).child("info");
    }


    private void getUserInfo() {
        //get project name from previous activity and set the activity title
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context
                .MODE_PRIVATE);
        projectName = sharedPreferences.getString("currentProject", "");
        firebaseUserId = sharedPreferences.getString("firebaseUserId", "");
    }

    public void validHealthRecord(){

    }
    public void createRecord(){

    }
}
