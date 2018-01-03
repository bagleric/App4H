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

public class AddGoal extends AppCompatActivity {
    //userInfo and database
    private DatabaseReference projectGoalsRef;
    private DatabaseReference projectInfoRef;
    private FirebaseDatabase database;
    private String projectName;
    private String firebaseUserId;

    //date picker
    private DatePickerDialog.OnDateSetListener goalDeadlineListener;
    private Calendar deadlineCalendar;
    private TextView deadlineTV;

    //ui elements
    private TextInputEditText descriptionInput;
    private TextInputEditText toDoTaskInput;

    //other variables
    private String id;
    private long dateNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize variables
        getUserInfo();
        getDatabaseRef();

        //Initialize UI
        descriptionInput = (TextInputEditText) findViewById(R.id.goal_description_input);
        toDoTaskInput = (TextInputEditText) findViewById(R.id.to_do_list_input);
        deadlineTV = (TextView) findViewById(R.id.goal_deadline);

        setInitialDeadlineTime();

        //get the item info if there is any
        SharedPreferences editInfo = getSharedPreferences("goalInfo", Context.MODE_PRIVATE);
        String description = editInfo.getString("goalDescription", "");
        String todo = editInfo.getString("goalTodo", "");
        String deadline = editInfo.getString("goalDeadline", "");
        id = editInfo.getString("goalId", "");

        if (!(description.equals(""))) {
            descriptionInput.setText(description);
            toDoTaskInput.setText(todo);
            deadlineTV.setText(deadline);

            SimpleDateFormat f = new SimpleDateFormat("E, MMM d, yyyy", Locale.US);
            try {
                Date d = f.parse(deadline);
                dateNumber = d.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SharedPreferences.Editor editor = editInfo.edit();
            editor.putString("goalDescription", "");
            editor.putString("goalTodo", "");
            editor.putString("goalDeadline", "");
            editor.putString("goalId", "");
            editor.apply();
        }


        FloatingActionButton addGoalBtn = (FloatingActionButton) findViewById(R.id.add_goal_btn);
        addGoalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseUserId.equals("unknown")) {
                    Toast.makeText(AddGoal.this, "Unable to create record. You need to" +
                            " sign out and sign in again.", Toast.LENGTH_SHORT).show();
                } else {
                    if (validGoal()) {
                        createRecord();
                        Intent intent = new Intent(AddGoal.this, ProjectGoalsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        AddGoal.this.startActivity(intent);
                        finish();
                    }
                }
            }
        });
        FloatingActionButton cancelBtn = (FloatingActionButton) findViewById(R.id.cancel_add_goal);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddGoal.this, ProjectGoalsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                AddGoal.this.startActivity(intent);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setInitialDeadlineTime() {
        deadlineCalendar = Calendar.getInstance();
        dateNumber = deadlineCalendar.getTimeInMillis();
        String today = new SimpleDateFormat("E, MMM d, yyyy", Locale.US).format(deadlineCalendar
                .getTime());
        deadlineTV.setText(today);
        deadlineTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = deadlineCalendar.get(Calendar.YEAR);
                int month = deadlineCalendar.get(Calendar.MONTH);
                int day = deadlineCalendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog deadlineDatePickerDialog = new DatePickerDialog(AddGoal.this,
                        android.R.style.Theme_DeviceDefault_Dialog, goalDeadlineListener,
                        year, month, day);
                deadlineDatePickerDialog.show();
            }
        });

        goalDeadlineListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                deadlineCalendar.set(Calendar.YEAR, year);
                deadlineCalendar.set(Calendar.MONTH, month);
                deadlineCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String deadline = new SimpleDateFormat("E, MMM d, yyyy", Locale.US).format
                        (deadlineCalendar.getTime());
                deadlineTV.setText(deadline);
                dateNumber = deadlineCalendar.getTimeInMillis();
            }
        };
    }

    private void getDatabaseRef() {
        database = FirebaseDatabase.getInstance();
        projectGoalsRef = database.getReference("users").child(firebaseUserId).child
                ("projects").child(projectName).child("goals");
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


    private boolean validGoal() {
        if (descriptionInput.getText().toString().equals("")) {
            descriptionInput.requestFocus();
            Toast.makeText(this, "You must have a description.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (toDoTaskInput.getText().toString().equals("")) {
            toDoTaskInput.requestFocus();
            Toast.makeText(this, "You must have at least one To-Do task.", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    private void createRecord() {
        String description = descriptionInput.getText().toString();
        String toDos = toDoTaskInput.getText().toString();
        DatabaseReference newGoalRef;
        if (id == null || id.equals("")) {
            newGoalRef = projectGoalsRef.push();
            newGoalRef.setValue(new RecordGoal(description, toDos, dateNumber, newGoalRef.getKey()));
            Toast.makeText(this, "Goal Created", Toast.LENGTH_SHORT).show();

        } else {
            newGoalRef = projectGoalsRef.child(id);
            newGoalRef.setValue(new RecordGoal(description, toDos, dateNumber, newGoalRef.getKey()));
            Toast.makeText(this, "Goal Updated", Toast.LENGTH_SHORT).show();
        }
    }
}
