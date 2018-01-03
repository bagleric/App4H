package com.example.bagle.app4h;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProjectActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String projectName;
    private String balance;
    private TextView financeInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getUserInfo();

//        Intent intent = getIntent();
//        projectName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(projectName);
        }

        //initialize the database elements
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //setupthedatabase
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String firebaseUserId = firebaseUser.getUid();
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("users").child(firebaseUserId).child("projects").child
                    (projectName);
        }

        FloatingActionButton editProject = (FloatingActionButton) findViewById(R.id.edit);
        editProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProjectActivity.this, GeneratePDF.class);
                ProjectActivity.this.startActivity(intent);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        FloatingActionButton addRecord = (FloatingActionButton) findViewById(R.id.add_record_btn);
        addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addRecord = new Intent(ProjectActivity.this, AddDialogProject.class);
                ProjectActivity.this.startActivity(addRecord);
            }
        });


        fillQuickInfo();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void getUserInfo() {
        //get project name from previous activity and set the activity title
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context
                .MODE_PRIVATE);
        projectName = sharedPreferences.getString("currentProject", "ERROR: No Project " +
                "Selected");

    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            if(resultCode == RESULT_OK) {
//                String strEditText = data.getStringExtra("editTextValue");
//                projectName = strEditText;
//                Toast.makeText(this, strEditText, Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    @Override
    public void onStart() {

        super.onStart();

    }

    private void fillQuickInfo() {

//        getFinanceInfo();
    }

    public void openFinances(View view) {
        Intent financesIntent = new Intent(ProjectActivity.this, ProjectFinanceActivity.class);
        ProjectActivity.this.startActivity(financesIntent);
    }
    public void openGoals(View view) {
        Intent goalsIntent = new Intent(ProjectActivity.this, ProjectGoalsActivity.class);
        ProjectActivity.this.startActivity(goalsIntent);
    }
    public void openMeetings(View view) {
        Intent meetingsIntent = new Intent(ProjectActivity.this, ProjectMeetingActivity.class);
        ProjectActivity.this.startActivity(meetingsIntent);
    }

    public void openAnimalHealth(View view) {
        Intent animalIntent = new Intent(ProjectActivity.this, ProjectAnimalHealthActivity.class);
        ProjectActivity.this.startActivity(animalIntent);
    }

    public void openOtherProjectItems(View view) {
        Intent otherIntent = new Intent(ProjectActivity.this, ProjectOtherActivity.class);
        ProjectActivity.this.startActivity(otherIntent);
    }
    public void openAnimalWeight(View view) {
        Intent otherIntent = new Intent(ProjectActivity.this, ProjectAnimalWeightActivity.class);
        ProjectActivity.this.startActivity(otherIntent);
    }
    public void openLeadership(View view) {
        Intent otherIntent = new Intent(ProjectActivity.this, ProjectLeadershipActivity.class);
        ProjectActivity.this.startActivity(otherIntent);
    }

}
