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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
    private Boolean Pass = false;                                                                   // variable used to prevent main activity loading in certain case
    private int friendCount;
    private HashMap<String, Boolean> friendFirebaseIDs;
    private ArrayList<String> friendFirebaseIDsOnly;
    private FirebaseAnalytics mFirebaseAnalytics;

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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    //    mFirebaseAnalytics.setUserProperty("test", "some string");
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();
        setContentView(R.layout.activity_login);                                                    // set the xml file to be viewed
                                        // declarations
        loginButton = (Button) findViewById(R.id.login_button);
        fbLoginButton = (LoginButton) findViewById(R.id.login_with_facebook_button);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
        final EditText etEmail = (EditText) findViewById(R.id.editText_login_email);
        final EditText etPassword = (EditText) findViewById(R.id.editText_login_password);
        final Intent intent = getIntent();
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
                                        // store friends in firebase
                                            // store users uid and fb id in database lookup table - need not run every time
                                final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                DatabaseReference myFacebookIDRef = database.child("facebookIDs").child(FacebookData.getString("id"));
                                myFacebookIDRef.setValue(user.getUid());
                                            // if you have no friends run main activity
                                final ArrayList<String> facebookFriendIDs = FacebookData.getStringArrayList("friendIds");
                                if (facebookFriendIDs == null || facebookFriendIDs.isEmpty()) {
                                    DatabaseReference friends = database.child("users").child(user.getUid()).child("friendUIDs");
                                    friends.removeValue();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);     // if there is facebook data
                                    intent.putExtra("tab", currentTab);                                     // start main activity and pass relevant data
                                    intent.putExtra("fbConnected", true);
                                    intent.putExtra("fbData", FacebookData);
                                    startActivity(intent);
                                }
                                else {          // else store friends UIDs in the users friend list on firebase
                                                        // initialise variables
                                    friendFirebaseIDs = new HashMap<>();                          // will hold the list of friend UIDs
                                    friendCount = facebookFriendIDs.size();
                                    friendFirebaseIDsOnly =  new ArrayList<>(Collections.nCopies(friendCount, " "));
                                                    // for every facebook friend, get their uid
                                    for (int i = 0; i < facebookFriendIDs.size(); i++) {
                                        DatabaseReference firebaseIDRef = database.child("facebookIDs").child(facebookFriendIDs.get(i));
                                        firebaseIDRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                        // store their uid in list
                                                String theUID = dataSnapshot.getValue().toString();
                                                int index = facebookFriendIDs.indexOf(dataSnapshot.getKey()); // the facebook id
                                                friendFirebaseIDsOnly.add(index, theUID);
                                                friendFirebaseIDs.put(theUID,true);
                                                friendCount = friendCount - 1;
                                                            // once all UIDs stored, store friends UIDs in user entry of database
                                                if (friendCount == 0) {
                                                    DatabaseReference friends = database.child("users").child(user.getUid()).child("friendUIDs");
                                                    friends.setValue(friendFirebaseIDs);
                                                    FacebookData.putStringArrayList("friendUids", friendFirebaseIDsOnly);
                                                                // start main activity
                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);     // if there is facebook data
                                                    intent.putExtra("tab", currentTab);                                     // start main activity and pass relevant data
                                                    intent.putExtra("fbConnected", true);
                                                    intent.putExtra("fbData", FacebookData);
                                                    startActivity(intent);
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }
                        } else {                                                                          // if facebook is not linked
                            if(!user.isEmailVerified() && !intent.getExtras().containsKey("verificationPass")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Your email address has not been verified yet. Please follow the link we sent you after you registered")
                                        .setPositiveButton("Resend Verification",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        user.sendEmailVerification();                       // collapse the list view which causes the view to be regenerated and so new selected item will be shown
                                                        Toast.makeText(LoginActivity.this,"Email verification request sent",Toast.LENGTH_LONG).show();
                                                    }
                                                })
                                        .setNegativeButton("Okay", null)
                                        .create()
                                        .show();
                            }
                            else if(!Pass){
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
                progressBar.setVisibility(View.VISIBLE);                                                // show progress bar
                InputMethodManager imm = (InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);  // hide keyboard
                imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
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
                                        List providers = task.getResult().getUser().getProviders();
                                        if (providers != null && providers.contains("facebook.com")) {    // if user has connected facebook
                                            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email", "user_friends"));   // login facebook and get data
                                        }
                                    } else {                                                              // if fail
                                        progressBar.setVisibility(View.INVISIBLE);                          // hide progress bar
                                                                            // if fails due to not a network error
                                        if (task.getException().getMessage().contains("The password is invalid")){
                                                                                    // allow them to reset password
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                            builder.setMessage("Login Failed - " + task.getException().getMessage())
                                                    .setPositiveButton(getString(R.string.login_retry_button), null)
                                                    .setNegativeButton("reset password", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(LoginActivity.this);
                                                                    builder2.setMessage("Are you sure you want to reset your password? This cannot be undone.")
                                                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                                public void onClick(DialogInterface dialog, int id) {
                                                                                    mAuth.sendPasswordResetEmail(email);
                                                                                    Toast.makeText(LoginActivity.this,"Password reset link sent", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            })
                                                                            .setNegativeButton("No", null)
                                                                            .create()
                                                                            .show();
                                                                }
                                                            })
                                                    .create()
                                                    .show();
                                        }
                                        else {                          // if login fails due to other reason, tell them why
                                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                            builder.setMessage("Login Failed - " + task.getException().getMessage())
                                                    .setPositiveButton(getString(R.string.login_retry_button), null)
                                                    .create()
                                                    .show();
                                        }
                                    }
                                }
                            });
                }
            }
        });
                                        // handle facebook login button press
        fbLoginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
                                        // on click, login to facebook
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                                        // if login to facebook was successful
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressBar.setVisibility(View.VISIBLE);
                final AccessToken accessToken = loginResult.getAccessToken();                       // get the access token
                Log.i("fbAccessToken", accessToken.getToken());
                final AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());  // generate firebase credential
                                        // make request for facebook user information
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override           // when data is returned
                    public void onCompleted(JSONObject object, GraphResponse response2) {
                        FacebookFriendData friends = getFacebookFriends(accessToken);                        // make separate request for friend list of friends using myfe
                        FacebookData = getFacebookData(object, friends);                            // format the data into a nice bundle
                                        // find out what providers, if any, email is used for
                        final Task<ProviderQueryResult> providers = mAuth.fetchProvidersForEmail(FacebookData.getString("email")); // check to see what providers the user uses
                        providers.addOnSuccessListener(new OnSuccessListener<ProviderQueryResult>() {
                            @Override   // when the data is returned
                            public void onSuccess(ProviderQueryResult providerQueryResult) {
                               final List provs =  providerQueryResult.getProviders();
                                if (provs == null || provs.isEmpty() || provs.contains("facebook.com")) {              // if new user; if used facebook before
                                    if (provs == null || provs.isEmpty()) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, "facebook");
                                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
                                    }
                                    mAuth.signInWithCredential(credential);                         // create an account; log them in
                                }
                                else {  // if they have a myfe account, but have not connected facebook yet
                                            // request they enter their myfe password
                                    final ViewGroup nullParent = null;
                                    View promptsView = getLayoutInflater().inflate(R.layout.input_prompt,nullParent);               // get input_prompts.xml view
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setView(promptsView);                                                                   // set input_prompts.xml to alert dialog builder
                                    final EditText userInput = (EditText) promptsView.findViewById(R.id.InputPromptUserInput);      // enable easy access to object
                                    userInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);                                 // set the dialog input data type
                                    final TextView promptMessage = (TextView) promptsView.findViewById(R.id.InputPromptMessage);
                                    promptMessage.setVisibility(View.GONE);
                                    builder.setMessage(FacebookData.getString("first_name")+ ", you have not yet linked Facebook to your Myfe account. Please enter your Myfe password below to link it.")
                                            .setPositiveButton("Link",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,int id) {
                                                            String pass = userInput.getText().toString();
                                                                            // sign in with given password
                                                            Pass = true;                // prevent main activity starting from the following login
                                                            mAuth.signInWithEmailAndPassword(FacebookData.getString("email"),pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                                   Pass = false;
                                                                             // if password entered correctly
                                                                    if(task.isSuccessful()) {
                                                                        task.getResult().getUser().linkWithCredential(credential);      // link facebook account
                                                                    }       // if password entered in correctly
                                                                    else {          // return to initial state
                                                                        Toast.makeText(LoginActivity.this,"Password Incorrect",Toast.LENGTH_LONG).show();
                                                                        LoginManager.getInstance().logOut();
                                                                        progressBar.setVisibility(View.INVISIBLE);                          // hide progress bar
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    LoginManager.getInstance().logOut();
                                                    progressBar.setVisibility(View.INVISIBLE);                          // hide progress bar
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
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this,"Error - " + error.getMessage(),Toast.LENGTH_SHORT).show();
                Log.d("onFbError", "facebook:onError", error);}

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
                            // function to get users friends info
    public FacebookFriendData getFacebookFriends(AccessToken accessToken) {
        final FacebookFriendData friendData = new FacebookFriendData();
        new GraphRequest(
                accessToken,
                "/"+ accessToken.getUserId() +"/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject object = response.getJSONObject();
                        String friendsNo = "";
                        try {
                            JSONArray jsonArray = object.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject aFriend = (JSONObject) jsonArray.get(i);
                                friendData.names.add(i,aFriend.getString("name"));
                                friendData.ids.add(i,aFriend.getString("id"));
                                friendData.picUrls.add(i,"https://graph.facebook.com/" + aFriend.getString("id") + "/picture?width=200&height=150");
                            }
                            friendsNo = object.getJSONObject("summary").getString("total_count");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("You have friends", friendsNo);
                    }
                }
        ).executeAsync();
        return friendData;
    }
                                    // function to format all of the data
    public Bundle getFacebookData(JSONObject object, FacebookFriendData friends) {
        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");
            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
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
            if (object.has("email")){ bundle.putString("email", object.getString("email"));}
            else { bundle.putString("email", id + "@facebook.com");}                                // for users who signed upto facebook with mobile
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));
            if (friends!=null) {
                bundle.putStringArrayList("friendNames", friends.names);
                bundle.putStringArrayList("friendIds", friends.ids);
                bundle.putStringArrayList("friendUrls", friends.picUrls);}

            return bundle;
        }
        catch(JSONException e) {
            Log.d("idk","Error parsing JSON");
            return null;
        }
    }
}


