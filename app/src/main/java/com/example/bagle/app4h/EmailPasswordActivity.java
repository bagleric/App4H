package com.example.bagle.app4h;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String TAG = "EMAIL_PASSWORD_ACTIVITY: ";
    public ProgressDialog mProgressDialog;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mEmailCheckView;
    private EditText mPasswordView;
    private EditText mPasswordCheckView;
    private View mProgressView;
    private View mLoginFormView;
    private boolean newUser;
    private TextView newUserToggleTV;
    private TextView existingUserToggleTV;
    private TextView loginTV;
    private TextView registerTV;
    private Button mEmailSignInButton;
    private Button mEmailCreateAccountButton;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_email_password);
        //initialize the variables and the views.
        newUser = false;
        loginTV = (TextView) findViewById(R.id.login_title);
        registerTV = (TextView) findViewById(R.id.register_title);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailCreateAccountButton = (Button) findViewById(R.id.email_create_account_btn);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newUser = false;
                signIn();
            }
        });
        mEmailCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newUser = false;
                createAccount();
            }
        });
        mPasswordCheckView = (EditText) findViewById(R.id.password_check);
        mEmailCheckView = (EditText) findViewById(R.id.email_check);
        newUserToggleTV = (TextView) findViewById(R.id.create_new_account_tv);
        existingUserToggleTV = (TextView) findViewById(R.id.existing_user_sign_in_toggle_tv);
        newUserToggleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if we click the create new user show the new user things
                mPasswordCheckView.setVisibility(View.VISIBLE);
                mEmailCheckView.setVisibility(View.VISIBLE);
                existingUserToggleTV.setVisibility(View.VISIBLE);
                mEmailCreateAccountButton.setVisibility(View.VISIBLE);
                registerTV.setVisibility(View.VISIBLE);

                //and hide the previously registered things.
                loginTV.setVisibility(View.GONE);
                newUserToggleTV.setVisibility(View.GONE);
                mEmailSignInButton.setVisibility(View.GONE);
            }
        });
        existingUserToggleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if we have already registered then we hide the register items.
                mPasswordCheckView.setVisibility(View.GONE);
                mEmailCheckView.setVisibility(View.GONE);
                mEmailCreateAccountButton.setVisibility(View.GONE);
                existingUserToggleTV.setVisibility(View.GONE);
                registerTV.setVisibility(View.GONE);

                //and we show the sign in items
                loginTV.setVisibility(View.VISIBLE);
                newUserToggleTV.setVisibility(View.VISIBLE);
                mEmailSignInButton.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        newUser = false;
        mPasswordCheckView.setVisibility(View.GONE);
        mEmailCheckView.setVisibility(View.GONE);
        mEmailCreateAccountButton.setVisibility(View.GONE);
        existingUserToggleTV.setVisibility(View.GONE);

        newUserToggleTV.setVisibility(View.VISIBLE);
        mEmailSignInButton.setVisibility(View.VISIBLE);
        // Check if user is signed in (non-null) and update UI accordingly.
       // mAuth.signOut();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent mainActivity = new Intent(EmailPasswordActivity.this,
                    MainActivity.class);
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            EmailPasswordActivity.this.startActivity(mainActivity);
            finish();
        }
    }


    public void createAccount() {
        if (!validateForm(true)) {
            return;
        }
        String password = mPasswordView.getText().toString();
        String email = mEmailView.getText().toString();
        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            sendEmailVerification();
                            //TODO decide what to do here
                            Intent informationIntent = new Intent(EmailPasswordActivity.this, UpdateUserInfo
                                    .class);
                            informationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent
                                    .FLAG_ACTIVITY_NEW_TASK);
                            EmailPasswordActivity.this.startActivity(informationIntent);
                            hideProgressDialog();
                            finish();
                        }
                        if (!task.isSuccessful()) {
                            Toast.makeText(EmailPasswordActivity.this, R.string
                                            .create_account_failed, Toast.LENGTH_LONG).show();
                            hideProgressDialog();
                        }
                    }
                });
    }

    public void signIn() {
        if (!validateForm(false)) {
            return;
        }
        String password = mPasswordView.getText().toString();
        String email = mEmailView.getText().toString();
        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            Intent mainActivity = new Intent(EmailPasswordActivity.this,
                                    MainActivity.class);
                            mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            EmailPasswordActivity.this.startActivity(mainActivity);
                            hideProgressDialog();
                            finish();
                        }
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_LONG).show();
                            hideProgressDialog();
                        }
                    }
                });
    }

    public boolean validateForm(boolean newUser) {
        String password = mPasswordView.getText().toString();
        String passwordCheck = mPasswordCheckView.getText().toString();
        String emailCheck = mEmailCheckView.getText().toString();
        String email = mEmailView.getText().toString();

        if (email.equals("") || !email.contains("@") || !email.contains(".")) {
            mEmailView.requestFocus();
            Toast.makeText(this, "Please enter a valid email.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.equals("")) {
            mPasswordView.requestFocus();
            Toast.makeText(this, "Please enter a valid password.", Toast.LENGTH_LONG).show();
            return false;
        }
        if(password.length() < 6){
            mPasswordView.requestFocus();
            Toast.makeText(this, "Password must be more than 6 characters long.", Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (newUser) {
            if (emailCheck.equals("")) {
                mEmailCheckView.requestFocus();
                Toast.makeText(this, "Please re-enter your email.", Toast.LENGTH_LONG).show();
                return false;
            }
            if (passwordCheck.equals("")) {
                mPasswordCheckView.requestFocus();
                Toast.makeText(this, "Please re-enter your password.", Toast
                        .LENGTH_LONG).show();
                return false;
            }
            if (!email.equals(emailCheck)) {
                Toast.makeText(this, "Emails do not match.", Toast.LENGTH_LONG).show();
                mEmailView.requestFocus();
                return false;
            }
            if (!password.equals(passwordCheck)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
                mPasswordView.requestFocus();
                return false;
            }
        }
        return true;
    }

    public void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(EmailPasswordActivity.this,
                                        "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "Verification email sent.");
                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(EmailPasswordActivity.this,
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "Verification email NOT sent.");

                            }
                        }
                    });
        }
    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mPasswordCheckView.setVisibility(View.GONE);
        mEmailCheckView.setVisibility(View.GONE);
        mEmailCreateAccountButton.setVisibility(View.GONE);
        existingUserToggleTV.setVisibility(View.GONE);

        newUserToggleTV.setVisibility(View.VISIBLE);
        mEmailSignInButton.setVisibility(View.VISIBLE);
       // hideProgressDialog();
    }
}
