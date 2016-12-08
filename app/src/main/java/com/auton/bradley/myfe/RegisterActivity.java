package com.auton.bradley.myfe;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by Bradley on 09/11/2016.
 * activity that handles register screen
 */

public class RegisterActivity extends AppCompatActivity {
                                    // global variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
                                    // cycle functions
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
                                    // main function
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_register);                                                 // set the xml file to be viewed
                                    // declarations
        final Button registerButton = (Button) findViewById(R.id.register_button);
        final EditText etEmail = (EditText) findViewById(R.id.editText_register_email);
        final EditText etPassword = (EditText) findViewById(R.id.editText_register_password);
        final EditText etName = (EditText) findViewById(R.id.editText_register_name);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.registerProgressBar);
                                    // function that runs every time user logs in or out
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {                                                                 // User is signed in
                    Log.d("TAGr1", "onAuthStateChanged:signed_in:" + user.getUid());
                                    // add name and profile pic
                    final String name = etName.getText().toString();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            //    .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                            .build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("DisplayNameAdded", "User profile updated.");
                                                        // start Main activity
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                } else {                                                                            // User is signed out
                    Log.d("TAG2r", "onAuthStateChanged:signed_out");
                }
            }
        };
                                    // handle sign up button click
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                                    // get the user's entered data
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();
                final String name = etName.getText().toString();
                                    // silly data entered checks
                if(email.isEmpty()) Toast.makeText(getBaseContext(),"Enter an email address",Toast.LENGTH_LONG).show();
                else if (password.isEmpty()) Toast.makeText(getBaseContext(),"Enter a password",Toast.LENGTH_LONG).show();
                else if (password.length() < 6) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("Password should be at least 6 characters")
                            .setNegativeButton(getString(R.string.login_retry_button), null)
                            .create()
                            .show();}
                else if (name.isEmpty()) Toast.makeText(getBaseContext(),"Enter a name",Toast.LENGTH_LONG).show();
                                    // if all data meets checks
                else {                                                                              // create account with firebase
                    progressBar.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);  // hide keyboard
                    imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("TAGr", "createUserWithEmail:onComplete:" + task.isSuccessful());
                                    if (task.isSuccessful()) {
                                        task.getResult().getUser().sendEmailVerification();
                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                        builder.setMessage("Register Failed - " + task.getException().getMessage())
                                                .setNegativeButton(getString(R.string.login_retry_button), null)
                                                .create()
                                                .show();
                                    }
                                }
                            });
                }
            }
        });
    }

                                    // add back button to take user to login screen
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        return true;
    }
}