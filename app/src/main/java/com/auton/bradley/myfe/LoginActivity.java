package com.auton.bradley.myfe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
                                        // main function
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

                                        // handle login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = etEmail.getText().toString();                                  // get the entered email address
                final String password = etPassword.getText().toString();                            // get the entered password
                // setup database response listener
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");                   // check whether database data request returned success
                            if(success) {
                                                        // get database data
                                String name = jsonResponse.getString("name");
                                String dob = jsonResponse.getString("dob");
                                String agenda = jsonResponse.getString("agenda");
                                String fbUserId = jsonResponse.getString("fbUserId");
                                String fbPassword = jsonResponse.getString("fbPassword");
                                // add bit to get facebook data
                                                        // start main activity passing user's data
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("password", password);
                                intent.putExtra("fbId", fbUserId);
                                intent.putExtra("fbPass", fbPassword);
                                intent.putExtra("name", name);
                                intent.putExtra("dob", dob);
                                intent.putExtra("agenda", agenda);
                                intent.putExtra("tab", currentTab);
                                LoginActivity.this.startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(getString(R.string.login_failed))
                                        .setNegativeButton(getString(R.string.login_retry_button), null)
                                        .create()
                                        .show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                };
                // send the login request
                LoginRequest loginRequest = new LoginRequest(email,password,responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });



                                        // handle facebook login button press
        fbLoginButton = (LoginButton) findViewById(R.id.login_with_facebook_button);
        fbLoginButton.setReadPermissions(Arrays.asList("user_birthday", "email", "user_friends"));



        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                                                // if users logs into facebook and accepts
                @Override
                public void onSuccess(LoginResult loginResult) {

                    final String accessToken = loginResult.getAccessToken().getToken();
                    Log.i("accessToken", accessToken);

                                                // login to facebook and get data
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                                // when logged in
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response2) {
                                                // Get facebook data
                            Log.i("qwert",object.toString());
                            FacebookData = getFacebookData(object);
                            Log.i("imp!",FacebookData.toString());
                            final String email = FacebookData.get("email").toString();                                  // get the entered email address
                            final String fbUserId = FacebookData.get("id").toString();
                            final String fbPassword = "facepass";
                            final String name = FacebookData.get("first_name").toString();
                            final String dob = FacebookData.get("birthday").toString();

                                                // setup database response listener
                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        boolean success = jsonResponse.getBoolean("success");
                                        Log.i("response!!!!:",jsonResponse.toString());
                                        if (success) {
                                                            // get database data
                                            String password = jsonResponse.getString("password");
                                            String agenda = jsonResponse.getString("agenda");
                                                            // start main activity passing user's data
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            intent.putExtra("email", email);
                                            intent.putExtra("password", password);
                                            intent.putExtra("fbUserId", fbUserId);
                                            intent.putExtra("fbPassword", fbPassword);
                                            intent.putExtra("name", name);
                                            intent.putExtra("dob", dob);
                                            intent.putExtra("agenda", agenda);
                                            intent.putExtra("tab", currentTab);
                                            LoginActivity.this.startActivity(intent);
                                        } else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                            builder.setMessage(getString(R.string.login_failed))
                                                    .setNegativeButton(getString(R.string.login_retry_button), null)
                                                    .create()
                                                    .show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            };

                            // send the login request
                            FbLoginRequest fbloginRequest = new FbLoginRequest(email, fbPassword, fbUserId, fbPassword, name, dob, responseListener);
                            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                            queue.add(fbloginRequest);




                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
                    request.setParameters(parameters);
                    request.executeAsync();


                }


                @Override
                public void onCancel() {
                    // App code
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                }
            });

       // LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));



    }




    Bundle getFacebookData(JSONObject object) {

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
}



                            // class used to send login request data nicely
class LoginRequest extends StringRequest {
    private  static final String LOGIN_REQUEST_URL = "https://myfe.000webhostapp.com/Login_User.php";
    private Map<String, String> params;

    LoginRequest(String email, String password, Response.Listener<String> listener) {
        super(Method.POST, LOGIN_REQUEST_URL,listener, null);
        params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}



// class used to send login request data nicely
class FbLoginRequest extends StringRequest {
    private  static final String LOGIN_REQUEST_URL = "https://myfe.000webhostapp.com/FB_Login_User.php";
    private Map<String, String> params;

    FbLoginRequest(String email, String password, String fbUserId, String fbPassword, String name, String dob, Response.Listener<String> listener) {
        super(Method.POST, LOGIN_REQUEST_URL,listener, null);
        params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("fbUserId", fbUserId);
        params.put("fbPassword", fbPassword);
        params.put("name", name);
        params.put("dob", dob);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}