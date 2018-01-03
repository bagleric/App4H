package com.example.bagle.app4h;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class AddDialogMainActivity extends AppCompatActivity {

    //UI



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dialog_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);



        FloatingActionButton newProject = (FloatingActionButton) findViewById(R.id.new_project_btn);
        newProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addProject = new Intent(AddDialogMainActivity.this, AddProject.class);
                AddDialogMainActivity.this.startActivity(addProject);
            }
        });
        FloatingActionButton newOther = (FloatingActionButton) findViewById(R.id.new_other_btn);
        newOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ActionBar actionBar =  getActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
