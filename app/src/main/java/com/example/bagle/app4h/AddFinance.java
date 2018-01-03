package com.example.bagle.app4h;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AddFinance extends AppCompatActivity {
    //user Info and Database
    private FirebaseDatabase database;
    private DatabaseReference projectRef;
    private DatabaseReference projectFinanceRef;
    private String projectName;
    private String firebaseUserId;

    //UI Elements
    private TextInputEditText recordDescription;
    private TextInputEditText recordAmount;
    private RadioButton incomeRB;
    private RadioButton expenseRB;
    //other variables
    private boolean isExpense;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_finance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize variables
        getUserInfo();
        getDatabaseRef();

        //initialize UI
        recordDescription = (TextInputEditText) findViewById(R.id.finance_record_description_input);
        recordAmount = (TextInputEditText) findViewById(R.id.finance_record_amount_input);
        incomeRB = (RadioButton) findViewById(R.id.income);
        expenseRB = (RadioButton) findViewById(R.id.expense);
        isExpense = true;

        //get the item info if there is any
        SharedPreferences editInfo = getSharedPreferences("financeInfo", Context.MODE_PRIVATE);
        String description = editInfo.getString("financeDescription", "");
        String amount = editInfo.getString("financeAmount", "");
        String isExpenseTxt = editInfo.getString("financeIsExpense", "");
        id = editInfo.getString("financeId", "");

        if (!(description.equals(""))) {
            recordDescription.setText(description);
            recordAmount.setText(amount);
            if (isExpenseTxt.equals("false")) {
                expenseRB.setChecked(false);
                incomeRB.setChecked(true);
                isExpense = false;
            }

            SharedPreferences.Editor editor = editInfo.edit();
            editor.putString("financeDescription", "");
            editor.putString("financeAmount", "");
            editor.putString("financeId", "");
            editor.putString("financeIsExpense", "");
            editor.apply();
        }
        FloatingActionButton saveBtn = (FloatingActionButton) findViewById(R.id.add_finance_record);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseUserId.equals("unknown")) {
                    Toast.makeText(AddFinance.this, "Unable to create record. You need to" +
                            " sign out and sign in again.", Toast.LENGTH_SHORT).show();
                } else {
                    if (validRecord()) {
                        createRecord();
                        Intent intent = new Intent(AddFinance.this, ProjectFinanceActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        AddFinance.this.startActivity(intent);
                        finish();
                    }
                }
            }
        });

        FloatingActionButton cancelBtn = (FloatingActionButton) findViewById(R.id.cancel_add_finance_record);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddFinance.this, ProjectFinanceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                AddFinance.this.startActivity(intent);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getDatabaseRef() {
        database = FirebaseDatabase.getInstance();
        projectRef = database.getReference("users").child(firebaseUserId).child
                ("projects").child(projectName);
        projectFinanceRef = projectRef.child("financeRecords");
    }

    private void getUserInfo() {
        //get project name from previous activity and set the activity title
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context
                .MODE_PRIVATE);
        projectName = sharedPreferences.getString("currentProject", "");
        firebaseUserId = sharedPreferences.getString("firebaseUserId", "");
    }

    private boolean validRecord() {

        if ((recordDescription.getText().toString()).equals("")) {
            recordDescription.requestFocus();
            Toast.makeText(AddFinance.this, "A description is required.", Toast
                    .LENGTH_SHORT).show();
            return false;
        }
        if ((recordAmount.getText().toString()).equals("")) {
            recordAmount.requestFocus();
            Toast.makeText(AddFinance.this, "An amount is required", Toast
                    .LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void createRecord() {
        String description = recordDescription.getText().toString();
        BigDecimal amount = new BigDecimal(recordAmount.getText().toString());
        DatabaseReference newFinanceRecordRef;
        if (id == null || id.equals("")) {
            newFinanceRecordRef = projectFinanceRef.push();
            newFinanceRecordRef.setValue(new RecordFinance(description, amount.setScale(2,
                    RoundingMode.HALF_UP).toString(), isExpense, newFinanceRecordRef.getKey()));
            Toast.makeText(this, "Finance Record Created", Toast.LENGTH_SHORT).show();

        }else{
            newFinanceRecordRef = projectFinanceRef.child(id);
            newFinanceRecordRef.setValue(new RecordFinance(description, amount.setScale(2,
                    RoundingMode.HALF_UP).toString(), isExpense, newFinanceRecordRef.getKey()));
            Toast.makeText(this, "Finance Record Updated", Toast.LENGTH_SHORT).show();
        }
    }

    public void onFinanceRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.expense:
                if (checked)
                    isExpense = true;
                break;
            case R.id.income:
                if (checked)
                    isExpense = false;
                break;
        }
    }
}
