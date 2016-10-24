package com.auton.bradley.myfe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    EditText email, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.login_button);
        email = (EditText) findViewById(R.id.editText_login_email);
        password = (EditText) findViewById(R.id.editText_login_password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(),"Searching!",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onCreateAccClick(View view) {

        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
