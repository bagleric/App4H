package com.example.bagle.app4h;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateUserInfo extends AppCompatActivity {
    private static final String TAG = "UPDATE_USER_INFO_CLASS";

    //firebase Elements
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String firebaseUserId;

    //UI elements
    private AutoCompleteTextView mFirstName;
    private AutoCompleteTextView mLastName;
    private TextView mBirthday;
    private EditText mYearIn4h;
    private Button mUpdateBtn;
    private View mUpdateInfoFormView;
    private View mProgressView;
    private TextInputEditText mPhoneNumber;
    private TextInputEditText mStreetAddress;
    private TextInputEditText mCityState;
    private TextInputEditText mZipCode;

    //date picker
    private DatePickerDialog.OnDateSetListener birthdateListener;
    private Calendar birthdateCalendar;

    private long dateNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
//        myToolbar.setTitleTextColor(Color.WHITE);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //get the ui elements
        mFirstName = findViewById(R.id.first_name);
        mLastName  = findViewById(R.id.last_name);
        mBirthday  = findViewById(R.id.birthday);
        mYearIn4h  = findViewById(R.id.year_in_4H);
        mPhoneNumber = findViewById(R.id.phone_number);
        mStreetAddress = findViewById(R.id.street_address);
        mCityState = findViewById(R.id.address_city_state);
        mZipCode = findViewById(R.id.zip_code);
        mUpdateBtn = findViewById(R.id.update_user_info_button);
        mProgressView = findViewById(R.id.update_user_info_progress);
        mUpdateInfoFormView = (View) findViewById(R.id.update_user_info_layout);

        getToday();

        // Write a message to the database
        if (firebaseUser != null){
            firebaseUserId = firebaseUser.getUid();
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("users").child(firebaseUserId);
            fillUserInfo();
        }

        // set up the database
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Intent backToSignin = new Intent(UpdateUserInfo.this, EmailPasswordActivity.class);
            UpdateUserInfo.this.startActivity(backToSignin);
            return;
        }



        //set onclick listeners
        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInfo();
            }
        });
        new ProgressViewer().execute();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_update_user_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                updateInfo();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    public void getToday() {
        birthdateCalendar = Calendar.getInstance();
        dateNumber = birthdateCalendar.getTimeInMillis();
        String today = new SimpleDateFormat("MMM d, yyyy", Locale.US).format(birthdateCalendar
                .getTime());
        mBirthday.setText(today);
        mBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = birthdateCalendar.get(Calendar.YEAR);
                int month = birthdateCalendar.get(Calendar.MONTH);
                int day = birthdateCalendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog birthdatePickerDialog = new DatePickerDialog(UpdateUserInfo.this,
                        android.R.style.Theme_DeviceDefault_Dialog, birthdateListener,
                        year, month, day);
                birthdatePickerDialog.show();
            }
        });

        birthdateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                birthdateCalendar.set(Calendar.YEAR, year);
                birthdateCalendar.set(Calendar.MONTH, month);
                birthdateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String birthdate = new SimpleDateFormat("MMM d, yyyy", Locale.US).format
                        (birthdateCalendar.getTime());
                mBirthday.setText(birthdate);
                dateNumber = birthdateCalendar.getTimeInMillis();
            }
        };

    }
    private void fillUserInfo() {

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstName;
                String lastName;
                String birthday;
                String yearIn4H;
                String phoneNumber;
                String streetAddress;
                String cityState;
                String zipCode;

                if(dataSnapshot.child("firstName").getValue() != null) {
                    firstName = dataSnapshot.child("firstName").getValue().toString();
                    mFirstName.setText(firstName);
                }
                if(dataSnapshot.child("lastName").getValue() != null) {
                    lastName = dataSnapshot.child("lastName").getValue().toString();
                    mLastName.setText(lastName);
                }
                if(dataSnapshot.child("birthday").getValue() != null) {
                    birthday = dataSnapshot.child("birthday").getValue().toString();
                    mBirthday.setText(birthday);

                }
                if(dataSnapshot.child("yearIn4H").getValue() != null) {
                    yearIn4H = dataSnapshot.child("yearIn4H").getValue().toString();
                    mYearIn4h.setText(yearIn4H);
                }
                if(dataSnapshot.child("phoneNumber").getValue() != null) {
                    phoneNumber = dataSnapshot.child("phoneNumber").getValue().toString();
                    mPhoneNumber.setText(phoneNumber);
                }
                if(dataSnapshot.child("streetAddress").getValue() != null) {
                    streetAddress = dataSnapshot.child("streetAddress").getValue().toString();
                    mStreetAddress.setText(streetAddress);
                }
                if(dataSnapshot.child("cityState").getValue() != null) {
                    cityState = dataSnapshot.child("cityState").getValue().toString();
                    mCityState.setText(cityState);
                }
                if(dataSnapshot.child("zipCode").getValue() != null) {
                    zipCode = dataSnapshot.child("zipCode").getValue().toString();
                    mZipCode.setText(zipCode);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


        mUpdateInfoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mUpdateInfoFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mUpdateInfoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
    private void updateInfo() {
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String birthday = mBirthday.getText().toString();
        String yearIn4H = mYearIn4h.getText().toString();
        String phoneNumber = mPhoneNumber.getText().toString();
        String streetAddress = mStreetAddress.getText().toString();
        String cityState = mCityState.getText().toString();
        String zipCode = mZipCode.getText().toString();
        String username = firebaseUser.getEmail();
//TODO add the input checking

        myRef.child("firstName").setValue(firstName);
        myRef.child("lastName").setValue(lastName);
        myRef.child("birthday").setValue(birthday);
        myRef.child("yearIn4H").setValue(yearIn4H);
        myRef.child("phoneNumber").setValue(phoneNumber);
        myRef.child("streetAddress").setValue(streetAddress);
        myRef.child("cityState").setValue(cityState);
        myRef.child("zipCode").setValue(zipCode);
        myRef.child("username").setValue(username);
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(firstName + " " + lastName)
                .build();

        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            Toast.makeText(UpdateUserInfo.this, "Updated Successfully", Toast
                                    .LENGTH_SHORT).show();
                            Intent mainActivity = new Intent(UpdateUserInfo.this, MainActivity
                                    .class);
                            UpdateUserInfo.this.startActivity(mainActivity);
                            finish();
                        }
                        else{
                            Toast.makeText(UpdateUserInfo.this, "Unable to update User profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    @SuppressLint("StaticFieldLeak")
    private class ProgressViewer extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            showProgress(true);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {

            showProgress(false);
            String date = mBirthday.getText().toString();

            SimpleDateFormat f = new SimpleDateFormat("MMM d, yyyy", Locale.US);
            try {
                Date d = f.parse(date);
                dateNumber = d.getTime();
                birthdateCalendar.setTime(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            fillUserInfo();

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}


