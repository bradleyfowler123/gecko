package com.auton.bradley.myfe;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
                                    // activity that handles register screen
public class RegisterActivity extends AppCompatActivity {

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
                                builder.setMessage("Register Failed! Email already registered")
                                        .setNegativeButton("Ok", null)
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
