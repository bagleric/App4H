package com.example.bagle.app4h;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddProject extends AppCompatActivity {
    //UI
    private DatePickerDialog.OnDateSetListener _startDateListener;
    private DatePickerDialog.OnDateSetListener _dueDateListener;
    private String date;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String firebaseUserId = "unknown";
    private String projectType = "";
    private EditText _projectName;
    private TextView _startDate;
    private TextView _dueDate;
    private EditText _projectYears;
    private Calendar startCal;
    private Calendar dueCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //UI
        _projectName = (TextInputEditText) findViewById(R.id.project_name);
        _startDate = (TextView) findViewById(R.id.start_date);
        _dueDate = (TextView) findViewById(R.id.due_date);
        _projectYears = (EditText) findViewById(R.id.years_in_project);

//        _dueDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int year = dueCal.get(Calendar.YEAR);
//                int month = dueCal.get(Calendar.MONTH);
//                int day = dueCal.get(Calendar.DAY_OF_MONTH);
//                DatePickerDialog dueDateDialog = new DatePickerDialog(AddProject.this, R.style
//                        .Theme_AppCompat_Dialog, _dueDateListener, year, month, day);
//                dueDateDialog.show();
//            }
//        });




        FloatingActionButton cancelBtn = (FloatingActionButton) findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddProject.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                AddProject.this.startActivity(intent);
                finish();
            }
        });

        FloatingActionButton saveBtn = (FloatingActionButton) findViewById(R.id.save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseUserId.equals("unknown")){
                    Toast.makeText(AddProject.this, "Unable to create project. You need to sign out and sign in.", Toast.LENGTH_SHORT).show();
                }else{
                   if(validProject()){
                       createProject();
                       Intent intent = new Intent(AddProject.this, MainActivity.class);
                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                       AddProject.this.startActivity(intent);
                       finish();
                   }
                }
            }
        });

        //get firebase items
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            firebaseUserId = firebaseUser.getUid();
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("users").child(firebaseUserId).child("projects");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    @Override
    public void onStart() {
        super.onStart();
        setTime();
        _startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int year = startCal.get(Calendar.YEAR);
                int month = startCal.get(Calendar.MONTH);
                int day = startCal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(AddProject.this,
                        android.R.style.Theme_DeviceDefault_Dialog, _startDateListener,
                        year, month, day);
                dialog.show();
            }
        });

        _startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startCal = Calendar.getInstance();
                startCal.set(Calendar.YEAR, year);
                startCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                startCal.set(Calendar.MONTH, month);
                String startMessage = new SimpleDateFormat("E, MMM d, yyyy", Locale.US).format
                        (startCal.getTime());
                _startDate.setText(startMessage);
            }
        };
        _dueDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dueCal = Calendar.getInstance();
                dueCal.set(year, month, dayOfMonth);
                String dueDateString = new SimpleDateFormat("E, MMM d, yyyy", Locale.US).format
                        (dueCal.getTime());
                _dueDate.setText(dueDateString);
            }
        };

    }

    private void setTime() {
        startCal = Calendar.getInstance();
        dueCal = Calendar.getInstance();
        String today = new SimpleDateFormat("E, MMM d, " + "yyyy", Locale.US).format(startCal
                .getTime());
        _startDate.setText(today);
        int year = dueCal.get(Calendar.YEAR) + (dueCal.get(Calendar.MONTH) +3)/ 11;
        dueCal.set(Calendar.DAY_OF_MONTH,28);
        dueCal.set(Calendar.MONTH, (dueCal.get(Calendar.MONTH)+ 3)%11);
        dueCal.set(Calendar.YEAR, year);
        String dueDate = new SimpleDateFormat("E, MMM d, " + "yyyy", Locale.US).format(dueCal
                .getTime());

        _dueDate.setText(dueDate);
    }

    private boolean validProject() {

        String projectName = _projectName.getText().toString();

        if(projectName.equals("")){
            _projectName.requestFocus();
            Toast.makeText(AddProject.this, "A project name is required.", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        if((_projectYears.getText().toString()).equals("")){
            _projectYears.requestFocus();
            Toast.makeText(AddProject.this, "Years in project is requred.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(projectType.equals("")){
            Toast.makeText(AddProject.this, "A project type (Livestock or General) is required.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.general:
                if (checked)
                    projectType = "general";
                    break;
            case R.id.livestock:
                if (checked)
                    projectType = "livestock";
                    break;
        }
    }

    private void createProject() {
        String projectName = _projectName.getText().toString();
        String startDate = _startDate.getText().toString();
        String dueDate = _dueDate.getText().toString();
        String projectYears = _projectYears.getText().toString();

        myRef.child(projectName).child("info").child("projectName").setValue(projectName);
        myRef.child(projectName).child("info").child("startDate").setValue(startDate);
        myRef.child(projectName).child("info").child("dueDate").setValue(dueDate);
        myRef.child(projectName).child("info").child("yearsInProject").setValue(projectYears);
        myRef.child(projectName).child("info").child("projectType").setValue(projectType);

        Toast.makeText(AddProject.this, projectName + " created.", Toast.LENGTH_SHORT).show();
    }

    public void openDueDateDialog(View view) {
        int year = dueCal.get(Calendar.YEAR);
        int month = dueCal.get(Calendar.MONTH);
        int day = dueCal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(
                AddProject.this,
                android.R.style.Theme_DeviceDefault_Dialog,
                _dueDateListener,
                year, month, day);
        dialog.show();
    }
}
