package com.example.bagle.app4h;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProjectAnimalHealthActivity extends AppCompatActivity {
    //firebase Database
    private FirebaseDatabase database;
    private DatabaseReference projectInfoRef;
    private DatabaseReference projectHealthRef;
    private String projectName;
    private String firebaseUserId;

    //recycler view and adapter and layout manager
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<RecordHealth> healthList;

    //UI Elements

    //other variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_animal_health);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize UI

        //recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.animal_health_list);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        healthList = new ArrayList<RecordHealth>();
        mAdapter = new AdapterRecordHealth(healthList);
        mRecyclerView.setAdapter(mAdapter);
        //end Recycler view Initialize

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_health_weight_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void onStart() {
        super.onStart();
        getUserInfo();
        if (firebaseUserId != null) {
            getDatabaseRef();
            fillHealthsList();
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
            projectHealthRef.child(healthList.get(position).id).removeValue();
            fillHealthsList();
        }
    };
    private void getDatabaseRef() {
        database = FirebaseDatabase.getInstance();
        projectHealthRef = database.getReference("users").child(firebaseUserId).child
                ("projects").child(projectName).child("healths");
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
    private void fillHealthsList() {
        healthList.clear();
        projectHealthRef.addChildEventListener(new ChildEventListener() {
            @Override

            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String species = "";
                String condition = "";
                String productUsed = "";
                String comments = "";
                long date = 0;
                if (dataSnapshot.child("description").getValue() == null
                        || dataSnapshot.child("toDos").getValue() == null
                        || dataSnapshot.child("deadline").getValue() == null) {
                    return;
                }
                species = dataSnapshot.child("species").getValue(String.class);
                condition = dataSnapshot.child("condition").getValue(String.class);
                productUsed = dataSnapshot.child("productUsed").getValue(String.class);
                comments = dataSnapshot.child("comments").getValue(String.class);
                if (dataSnapshot.child("treatmentDate").getValue(Long.class) != null) {
                    date = dataSnapshot.child("treatmentDate").getValue(Long.class);
                }
                healthList.add(new RecordHealth(dataSnapshot.getKey(),species, condition,
                        productUsed,comments,date));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mAdapter.notifyDataSetChanged();
                Toast.makeText(ProjectAnimalHealthActivity.this, "Health Record Deleted", Toast
                        .LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProjectAnimalHealthActivity.this, "CANCELLED ERROR", Toast
                        .LENGTH_SHORT).show();
            }
        });


    }
}
