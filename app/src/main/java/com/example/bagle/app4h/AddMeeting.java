package com.example.bagle.app4h;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddMeeting extends AppCompatActivity {
    //userInfo
    private DatabaseReference projectMeetingsRef;
    private DatabaseReference projectInfoRef;
    private FirebaseDatabase database;
    private String projectName;
    private String firebaseUserId;

    //date picker
    private DatePickerDialog.OnDateSetListener meetingDateListener;
    private Calendar meetingCalendar;
    private TextView meetingDateTV;

    // other UI
    private TextInputEditText meetingDescription;

    // other variables
    private long dateNumber;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meeting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize variables
        getUserInfo();
        getDatabaseRef();

        //Initialize UI
        meetingDateTV = (TextView) findViewById(R.id.meeting_date);
        meetingDescription = (TextInputEditText) findViewById(R.id.meeting_description);

        getToday();

        //get the item info if there is any
        SharedPreferences editInfo = getSharedPreferences("meetingInfo", Context.MODE_PRIVATE);
        String description = editInfo.getString("meetingDescription", "");
        String date = editInfo.getString("meetingDate", "");
        id = editInfo.getString("meetingId", "");

        if (!(description.equals(""))) {
            meetingDescription.setText(description);
            meetingDateTV.setText(date);

            SimpleDateFormat f = new SimpleDateFormat("E, MMM d, yyyy", Locale.US);
            try {
                Date d = f.parse(date);
                dateNumber = d.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SharedPreferences.Editor editor = editInfo.edit();
            editor.putString("meetingInfo", "");
            editor.putString("meetingDescription", "");
            editor.putString("meetingId", "");
            editor.apply();
            Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        }


        FloatingActionButton saveMeeting = (FloatingActionButton) findViewById(R.id
                .save_meeting_btn);
        saveMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidMeeting()) {
                    createMeeting();
                    Intent intent = new Intent(AddMeeting.this, ProjectMeetingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    AddMeeting.this.startActivity(intent);
                    finish();
                }
            }
        });
        FloatingActionButton cancel = (FloatingActionButton) findViewById(R.id.cancel_add_meeting);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddMeeting.this, ProjectMeetingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                AddMeeting.this.startActivity(intent);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void getToday() {
        meetingCalendar = Calendar.getInstance();
        dateNumber = meetingCalendar.getTimeInMillis();
        String today = new SimpleDateFormat("E, MMM d, yyyy", Locale.US).format(meetingCalendar
                .getTime());
        meetingDateTV.setText(today);
        meetingDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = meetingCalendar.get(Calendar.YEAR);
                int month = meetingCalendar.get(Calendar.MONTH);
                int day = meetingCalendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog meetingDatePickerDialog = new DatePickerDialog(AddMeeting.this,
                        android.R.style.Theme_DeviceDefault_Dialog, meetingDateListener,
                        year, month, day);
                meetingDatePickerDialog.show();
            }
        });

        meetingDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                meetingCalendar.set(Calendar.YEAR, year);
                meetingCalendar.set(Calendar.MONTH, month);
                meetingCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String meeting = new SimpleDateFormat("E, MMM d, yyyy", Locale.US).format
                        (meetingCalendar.getTime());
                meetingDateTV.setText(meeting);
                dateNumber = meetingCalendar.getTimeInMillis();
            }
        };

    }

    private void getDatabaseRef() {
        database = FirebaseDatabase.getInstance();
        projectMeetingsRef = database.getReference("users").child(firebaseUserId).child
                ("projects").child(projectName).child("meetings");
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

    public boolean isValidMeeting() {
        if (meetingDescription.getText().toString().equals("")) {
            meetingDescription.requestFocus();
            Toast.makeText(this, "You must have a description.", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    public void createMeeting() {
        String description = meetingDescription.getText().toString();
        DatabaseReference newMeetingRef;
        if (id == null || id.equals("")) {
            newMeetingRef = projectMeetingsRef.push();
            newMeetingRef.setValue(new RecordMeeting(description, dateNumber, newMeetingRef.getKey()));
            Toast.makeText(this, "Meeting Created", Toast.LENGTH_SHORT).show();

        } else {
            newMeetingRef = projectMeetingsRef.child(id);
            newMeetingRef.setValue(new RecordMeeting(description, dateNumber, newMeetingRef.getKey()));
            Toast.makeText(this, "Meeting Updated", Toast.LENGTH_SHORT).show();
        }
    }
}
