package com.example.bagle.app4h;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class AddDialogProject extends AppCompatActivity {
    //private String projectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dialog_project);
        Intent intent = getIntent();
     //   projectName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        intent.putExtra("editTextValue", "value_here");
        setResult(RESULT_OK, intent);
        finish();


        FloatingActionButton addMeeting = (FloatingActionButton) findViewById(R.id.new_meeting_btn);
        FloatingActionButton addFinance = (FloatingActionButton) findViewById(R.id.new_finance_btn);
        FloatingActionButton addGoal = (FloatingActionButton) findViewById(R.id.new_goal_btn);

        addMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addRecord = new Intent(AddDialogProject.this, AddDialogProject.class);
                AddDialogProject.this.startActivity(addRecord);
            }
        });
        addFinance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addRecord = new Intent(AddDialogProject.this, AddDialogProject.class);
                AddDialogProject.this.startActivity(addRecord);
            }
        });
        addGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addRecord = new Intent(AddDialogProject.this, AddDialogProject.class);
                AddDialogProject.this.startActivity(addRecord);
            }
        });

        FloatingActionButton addAnimalHealthRecord = (FloatingActionButton) findViewById(R.id
                .new_animal_health_btn);
        addAnimalHealthRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addRecord = new Intent(AddDialogProject.this, AddDialogProject.class);
                AddDialogProject.this.startActivity(addRecord);
            }
        });

    }

}
