package com.auton.bradley.myfe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bradley on 04/11/2016.
 * Java File to hold all activities and class' related to logging in (includes registering)
 */



                                // activity that handles login screen
class LoginActivity extends AppCompatActivity {
    Button loginButton;
    public int currentTab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                                // start main activity passing user's data
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("password", password);
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



                        // activity that handles register screen
class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);                                                 // set the xml file to be viewed
        // declarations
        final Button registerButton = (Button) findViewById(R.id.register_button);
        final EditText etEmail = (EditText) findViewById(R.id.editText_register_email);
        final EditText etPassword = (EditText) findViewById(R.id.editText_register_password);
        final EditText etName = (EditText) findViewById(R.id.editText_register_name);
        final EditText etDob = (EditText) findViewById(R.id.editText_register_dob);
        // handle sign up button click
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the user's entered data
                final String name = etName.getText().toString();
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();
                final String dob = etDob.getText().toString();

                Toast.makeText(getBaseContext(),"Registering",Toast.LENGTH_SHORT).show();           // inform user that system is registering them
                // setup database response listener
                Response.Listener<String> responseListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");                   // check whether database accepted our data
                            if (success) {
                                // start login screen
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                RegisterActivity.this.startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage(getString(R.string.register_failed))
                                        .setNegativeButton(getString(R.string.register_ok_button), null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                };
                // send the register request
                RegisterRequest registerRequest = new RegisterRequest(email, password, name, dob, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
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



                                // class used to send register request data nicely
class RegisterRequest extends StringRequest {
    private  static final String REGISTER_REQUEST_URL = "https://myfe.000webhostapp.com/Register_User2.php";
    private Map<String, String> params;

    RegisterRequest(String email, String password, String name, String dob, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL,listener, null);
        params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("name", name);
        params.put("dob", dob);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}