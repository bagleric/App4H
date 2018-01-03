package com.example.bagle.app4h;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProjectGoalsActivity extends AppCompatActivity {
    //firebase Database
    private FirebaseDatabase database;
    private DatabaseReference projectInfoRef;
    private DatabaseReference projectGoalsRef;
    private String projectName;
    private String firebaseUserId;

    //recycler view and adapter and layout manager
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<RecordGoal> goalList;

    //UI Elements
    private TextView goalFraction;

    //other variables
    private int numberGoals;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_goals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize UI
        goalFraction = (TextView) findViewById(R.id.goal_fraction);
        //recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.goal_list);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        goalList = new ArrayList<RecordGoal>();
        mAdapter = new AdapterRecordGoal(goalList);
        mRecyclerView.setAdapter(mAdapter);
        //end Recycler view Initialize

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addGoal = new Intent(ProjectGoalsActivity.this, AddGoal.class);
                ProjectGoalsActivity.this.startActivity(addGoal);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onStart() {
        super.onStart();
        getUserInfo();
        if (firebaseUserId != null) {
            getDatabaseRef();
            fillGoalsList();
        }
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

            //Remove swiped item from list and notify the RecyclerView
            final int position = viewHolder.getAdapterPosition();
            projectGoalsRef.child(goalList.get(position).id).removeValue();
            fillGoalsList();
        }
    };

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


    private void fillGoalsList() {
        numberGoals = 0;
        goalList.clear();
        projectGoalsRef.addChildEventListener(new ChildEventListener() {
            @Override

            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String description = "";
                String todos = "";
                long deadline = 0;
                if (dataSnapshot.child("description").getValue() == null
                        || dataSnapshot.child("toDos").getValue() == null
                        || dataSnapshot.child("deadline").getValue() == null) {
                    return;
                }
                description = dataSnapshot.child("description").getValue(String.class);
                todos = dataSnapshot.child("toDos").getValue(String.class);
                if (dataSnapshot.child("deadline").getValue(Long.class) != null) {
                    deadline = dataSnapshot.child("deadline").getValue(Long.class);
                }
                numberGoals++;
                projectInfoRef.child("numberGoals").setValue(numberGoals);
                String fraction = String.valueOf(numberGoals) + "/3";
                goalFraction.setText(fraction);
                goalList.add(new RecordGoal(description, todos, deadline, dataSnapshot.getKey()));

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mAdapter.notifyDataSetChanged();
                Toast.makeText(ProjectGoalsActivity.this, "Meeting Deleted", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProjectGoalsActivity.this, "CANCELLED ERROR", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
