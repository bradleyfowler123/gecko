package com.auton.bradley.myfe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
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
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        setContentView(R.layout.activity_login);                                                    // set the xml file to be viewed
                                        // declarations
        loginButton = (Button) findViewById(R.id.login_button);
        final EditText etEmail = (EditText) findViewById(R.id.editText_login_email);
        final EditText etPassword = (EditText) findViewById(R.id.editText_login_password);
        Intent intent = getIntent();
        if(intent.getExtras()!=null) { currentTab = intent.getIntExtra("tab",0);}
        else { currentTab = 0;}





        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TAG1", "onAuthStateChanged:signed_in:" + user.getUid());
                    List<? extends UserInfo> data = user.getProviderData();
                    Log.d("bfv0", data.get(0).getProviderId());
                    Log.d("bfv1", data.get(1).getProviderId());
                    Log.d("bfv2", data.get(2).getProviderId());

              //      String tok = user.getToken(true).getResult().getToken()
                    if(FacebookData != null) {
                        Log.d("bdlx", FacebookData.get("gender").toString());
                    }



//                    Log.d("bndsjfv", user.getProviderData.get("acc"));

                    // get facebook data

                } else {
                    // User is signed out
                    Log.d("TAG2", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

                                        // handle login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = etEmail.getText().toString();                                  // get the entered email address
                final String password = etPassword.getText().toString();                            // get the entered password

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("Tag4", "signInWithEmail:onComplete:" + task.isSuccessful());;

                                Log.d("xxzd", task.getResult().getUser().getProviders().toString());
                                if(task.getResult().getUser().getProviders().contains("facebook.com")) {
                                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends"));
                                }



                                if (!task.isSuccessful()) {
                                    Log.w("tag5", "signInWithEmail", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
            }
        });


                                        // handle facebook login button press
        fbLoginButton = (LoginButton) findViewById(R.id.login_with_facebook_button);
        fbLoginButton.setReadPermissions(Arrays.asList("user_birthday", "email", "user_friends"));





            fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    System.out.println("onSuccess");

                    String accessToken = loginResult.getAccessToken().getToken();
                    Log.i("accessToken", accessToken);

                    AuthCredential credential = FacebookAuthProvider.getCredential(accessToken);

                    mAuth.signInWithCredential(credential);

                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response2) {
                            Log.i("LoginActivity", response2.toString());
                            // Get facebook data from login
                            FacebookData = getFacebookData(object);
                            Toast.makeText(getBaseContext(),FacebookData.get("first_name").toString(),Toast.LENGTH_SHORT).show();           // inform user that system is registering them
                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
                    request.setParameters(parameters);
                    request.executeAsync();
                }





                @Override
                public void onCancel() {
                    Log.d("oncanas", "facebook:onCancel");
                    // ...
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d("hbcsh", "facebook:onError", error);
                    // ...
                }

            });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    // New user text click
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


    public Bundle getFacebookData(JSONObject object) {
        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

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

            return bundle;
        }
        catch(JSONException e) {
            Log.d("idk","Error parsing JSON");
            return null;
        }
    }


}


