package com.auton.bradley.myfe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Bradley on 04/11/2016.
 * Java File to hold activity and class related to logging in
 * activity that handles login screen
 */


public class LoginActivity extends AppCompatActivity {
                                        // global variable declarations
    Button loginButton;
    Bundle FacebookData;
    public int currentTab;
    CallbackManager callbackManager;
    LoginButton fbLoginButton;
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
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();
        setContentView(R.layout.activity_login);                                                    // set the xml file to be viewed
                                        // declarations
        loginButton = (Button) findViewById(R.id.login_button);
        fbLoginButton = (LoginButton) findViewById(R.id.login_with_facebook_button);
        final EditText etEmail = (EditText) findViewById(R.id.editText_login_email);
        final EditText etPassword = (EditText) findViewById(R.id.editText_login_password);
        Intent intent = getIntent();
        if(intent.getExtras()!=null) { currentTab = intent.getIntExtra("tab",0);}
        else { currentTab = 0;}
                                        // function that runs every time user logs in or out in this activity
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {                                                                 // if user logged into firebase
                    Log.d("TAG1", "onAuthStateChanged:signed_in:" + user.getUid());
                        if (user.getProviders() != null && user.getProviders().contains("facebook.com")) { // check to see if facebook is linked to their account
                            if (FacebookData != null) {                                                 // if it is see if the facebook data has been gotten yet
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);     // if there is facebook data
                                intent.putExtra("tab", currentTab);                                     // start main activity and pass relevant data
                                intent.putExtra("fbConnected", true);
                                intent.putExtra("fbData", FacebookData);
                                startActivity(intent);
                            }
                        } else {                                                                          // if facebook is not linked
                            if(!user.isEmailVerified()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Your email address has not been verified yet. Please follow the link we sent you after you registered")
                                        .setPositiveButton("Resend Verification",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        user.sendEmailVerification();                       // collapse the list view which causes the view to be regenerated and so new selected item will be shown
                                                    }
                                                })
                                        .setNegativeButton("Okay", null)
                                        .create()
                                        .show();
                            }
                            else {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);         // start main activity and pass relevant data
                                intent.putExtra("tab", currentTab);
                                intent.putExtra("fbConnected", false);
                                startActivity(intent);
                            }
                        }
                } else {                                                                            // User is signed out
                    Log.d("TAG2", "onAuthStateChanged:signed_out");
                }
            }
        };
                                        // handle login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = etEmail.getText().toString();                                  // get the entered email address
                final String password = etPassword.getText().toString();                            // get the entered password
                if(email.isEmpty()) Toast.makeText(getBaseContext(),"Enter an email address",Toast.LENGTH_LONG).show();
                else if (password.isEmpty()) Toast.makeText(getBaseContext(),"Enter a password",Toast.LENGTH_LONG).show();
                else {                                                                              // sign in user
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                // when data is returned
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {                                           // if success
                                        if(task.getResult().getUser().isEmailVerified()) {          // if email has been verified
                                            List providers = task.getResult().getUser().getProviders();
                                            if (providers != null && providers.contains("facebook.com")) {    // if user has connected facebook
                                                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends"));   // login facebook and get data
                                            }
                                        }
                                    } else {                                                              // if fail
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                        builder.setMessage(getString(R.string.login_failed))
                                                .setNegativeButton(getString(R.string.login_retry_button), null)
                                                .create()
                                                .show();
                                    }
                                }
                            });
                }
            }
        });
                                        // handle facebook login button press
        fbLoginButton.setReadPermissions(Arrays.asList("user_birthday", "email", "user_friends"));
                                        // on click login
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                                        // if login was successful
            @Override
            public void onSuccess(LoginResult loginResult) {
                final AccessToken accessToken = loginResult.getAccessToken();                       // get the access token
                Log.i("fbAccessToken", accessToken.getToken());
                final AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());        // generate firebase credential
                                        // get data from fb account
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response2) {
                        JSONArray friends = getFacebookFriends(accessToken);
                        FacebookData = getFacebookData(object, friends);                                     // Get facebook data from login

                        Task<ProviderQueryResult> providers = mAuth.fetchProvidersForEmail(FacebookData.getString("email"));
                        providers.addOnSuccessListener(new OnSuccessListener<ProviderQueryResult>() {
                            @Override
                            public void onSuccess(ProviderQueryResult providerQueryResult) {
                                if(providerQueryResult.getProviders().contains("facebook.com")) {
                                    mAuth.signInWithCredential(credential);
                                }
                                else { // they have a myfe account but have not connected facebook yet
                                    final ViewGroup nullParent = null;
                                    View promptsView = getLayoutInflater().inflate(R.layout.input_prompt,nullParent); // get input_prompts.xml view
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setView(promptsView);                                    // set input_prompts.xml to alert dialog builder

                                    final EditText userInput = (EditText) promptsView.findViewById(R.id.InputPromptUserInput); // enable easy access to object
                                    userInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);                                          // set the dialog input data type
                                    final TextView promptMessage = (TextView) promptsView.findViewById(R.id.InputPromptMessage);
                                    promptMessage.setVisibility(View.GONE);

                                    builder.setMessage(FacebookData.getString("first_name")+ ", you have not yet linked Facebook to your Myfe account. Please enter your Myfe password below to link it.")
                                            .setPositiveButton("Link",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,int id) {
                                                            String pass = userInput.getText().toString();
                                                            mAuth.signInWithEmailAndPassword(FacebookData.getString("email"),pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                                    if(task.isSuccessful()) {
                                                                        task.getResult().getUser().linkWithCredential(credential);
                                                                    }
                                                                    else {
                                                                        Toast.makeText(LoginActivity.this,"Password Incorrect",Toast.LENGTH_LONG).show();
                                                                        LoginManager.getInstance().logOut();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    LoginManager.getInstance().logOut();
                                                }
                                            })
                                            .create()
                                            .show();
                                }
                                Log.d("fdjkv fd", providerQueryResult.getProviders().toString());

                            }
                        });
                    }
                });
                                        // send the request for data
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();
            }
                                        // if login screen was cancelled
            @Override
            public void onCancel() { Log.d("onFbCancel", "facebook:onCancel");}
                                        // if login error
            @Override
            public void onError(FacebookException error) { Log.d("onFbError", "facebook:onError", error);}

        });
    }
                                    // facebook - passes data back to facebook api
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);                            // Pass the activity result back to the Facebook SDK
    }

                                    // User clicks on register text
    public void onCreateAccClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);                                                                      // open register page
    }

                                    // generate login options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

                                    // respond to action bar item press
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();                                                                  // get pressed item
        switch (id) {
            case R.id.action_close:                                                                 // start main activity with no user data if closed clicked
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("tab", currentTab);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public JSONArray getFacebookFriends(AccessToken accessToken) {
        final JSONArray[] friendData = {null};
        new GraphRequest(
                accessToken,
                "/"+ accessToken.getUserId() +"/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject object = response.getJSONObject();
                        String friends = "";
                        try {
                            friendData[0] = object.getJSONArray("data");
                            friends = object.getJSONObject("summary").getString("total_count");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("bjdsnxn", friends);
                    }
                }
        ).executeAsync();
        return friendData[0];
    }
                                    // function to get all of the data
    public Bundle getFacebookData(JSONObject object, JSONArray friends) {
        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");
            Log.d("jewnjhc ds",object.toString());
            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
            bundle.putString("id", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));
            if (friends!=null)
                bundle.putString("friends", friends.toString());
            else
                bundle.putString("friends", "0");

            return bundle;
        }
        catch(JSONException e) {
            Log.d("idk","Error parsing JSON");
            return null;
        }
    }
}


