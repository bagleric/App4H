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
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProjectMeetingActivity extends AppCompatActivity {
    //recycler view and adapter and layout manager
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<RecordMeeting> meetingList;

    //
    private FirebaseDatabase database;
    private DatabaseReference projectInfoRef;
    private DatabaseReference projectMeetingsRef;
    private String projectName;
    private String firebaseUserId;
    private String firebaseUserName;

    //other
    private int meetingNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_meeting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.meeting_list);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        meetingList = new ArrayList<RecordMeeting>();
        mAdapter = new AdapterRecordMeeting(meetingList);
        mRecyclerView.setAdapter(mAdapter);
        //end Recycler view Initialize


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        FloatingActionButton addMeeting = (FloatingActionButton) findViewById(R.id.add_meeting_btn);
        addMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addMeeting = new Intent(ProjectMeetingActivity.this, AddMeeting.class);
                ProjectMeetingActivity.this.startActivity(addMeeting);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            projectMeetingsRef.child(meetingList.get(position).id).removeValue();
            fillMeetingsList();
        }
    };

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

    public void onStart() {

        super.onStart();
        //initialize user
        getUserInfo();
        meetingNumber = 0;
        if (firebaseUserId != null) {
            getDatabaseRef();
            fillMeetingsList();
        }

        //initialize recycler view


    }

    private void fillMeetingsList() {
        meetingList.clear();

        projectMeetingsRef.orderByChild("meetingDate").addChildEventListener(new ChildEventListener
                () {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String description = "";
                long meetingDate = 0;
                if (dataSnapshot.child("description").getValue() == null
                        || dataSnapshot.child("meetingDate").getValue() == null) {
                    return;
                }
                description = dataSnapshot.child("description").getValue(String.class);
                if (dataSnapshot.child("meetingDate").getValue(Long.class) != null) {
                    meetingDate = dataSnapshot.child("meetingDate").getValue(Long.class);
                }
                meetingNumber++;
                projectInfoRef.child("numberMeetings").setValue(meetingNumber);
                meetingList.add(new RecordMeeting(description, meetingDate, dataSnapshot.getKey()));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mAdapter.notifyDataSetChanged();
                Toast.makeText(ProjectMeetingActivity.this, "Meeting Deleted", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProjectMeetingActivity.this, "CANCELLED DATABASE CONNECTION", Toast
                        .LENGTH_SHORT).show();
            }
        });
    }
}

