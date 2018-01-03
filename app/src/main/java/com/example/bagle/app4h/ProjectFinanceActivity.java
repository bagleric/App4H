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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ProjectFinanceActivity extends AppCompatActivity {
    //firebase database
    private FirebaseDatabase database;
    private DatabaseReference projectInfoRef;
    private DatabaseReference projectFinanceRef;
    private String projectName;
    private String firebaseUserId;

    //recycler view and adapter and layout manager
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<RecordFinance> financeRecords;

    //UI
    private TextView balanceTv;

    //other variables
    private BigDecimal balance;
    private String strBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_finance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize UI
        balanceTv = findViewById(R.id.finance_balance);

        //recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.finance_list);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        financeRecords = new ArrayList<RecordFinance>();
        mAdapter = new AdapterRecordFinance(financeRecords);
        mRecyclerView.setAdapter(mAdapter);
        //end Recycler view Initialize


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        FloatingActionButton addFinance = (FloatingActionButton) findViewById(R.id.fab);
        addFinance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addFinance = new Intent(ProjectFinanceActivity.this, AddFinance
                        .class);
                ProjectFinanceActivity.this.startActivity(addFinance);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        getUserInfo();
        if (firebaseUserId != null) {
            getDatabaseRef();
            fillFinanceRecords();
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
            projectFinanceRef.child(financeRecords.get(position).id).removeValue();
            strBalance = "$ 0.00";
            balanceTv.setText(strBalance);
            fillFinanceRecords();
        }
    };

    private void getDatabaseRef() {
        database = FirebaseDatabase.getInstance();
        projectFinanceRef = database.getReference("users").child(firebaseUserId).child
                ("projects").child(projectName).child("financeRecords");
        projectInfoRef = database.getReference("users").child(firebaseUserId).child("projects")
                .child(projectName).child("info");
    }

    private void getUserInfo() {
        //get project name from previous activity and set the activity title
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context
                .MODE_PRIVATE);
        projectName = sharedPreferences.getString("currentProject", "ERROR: No Project " +
                "Selected");
        firebaseUserId = sharedPreferences.getString("firebaseUserId", "");
    }

    private void fillFinanceRecords() {
        balance = new BigDecimal(0);
        financeRecords.clear();
        projectFinanceRef.addChildEventListener(new ChildEventListener() {
            @Override

            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String description = "";
                BigDecimal amount = new BigDecimal(0);
                boolean isExpense = false;
                if (dataSnapshot.child("description").getValue() == null
                        || dataSnapshot.child("amount").getValue() == null
                        || dataSnapshot.child("isExpense").getValue() == null) {
                    return;
                }
                description = dataSnapshot.child("description").getValue(String.class);
                amount = new BigDecimal(dataSnapshot.child("amount").getValue(String.class));
                Object object = dataSnapshot.child("isExpense").getValue();
                if (object != null) {
                    if (object.toString().equals("true")) {
                        balance = balance.subtract(amount);
                        strBalance = "$ " + String.valueOf(balance.setScale(2, RoundingMode
                                .HALF_UP));

                        isExpense = true;
                    } else if (object.toString().equals("false")) {
                        balance = balance.add(amount);
                        strBalance = "$ " + String.valueOf(balance.setScale(2, RoundingMode
                                .HALF_UP));

                        isExpense = false;
                    }
                }
//                if (balance.compareTo(BigDecimal.ZERO)  0){
//                    strBalance = "("+ strBalance + ")";
//                }
                projectInfoRef.child("balance").setValue(strBalance);
                balanceTv.setText(strBalance);
                financeRecords.add(new RecordFinance(description, amount.toString(), isExpense,
                        dataSnapshot.getKey()));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mAdapter.notifyDataSetChanged();
                Toast.makeText(ProjectFinanceActivity.this, "Meeting Deleted", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProjectFinanceActivity.this, "CANCELLED ERROR", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
