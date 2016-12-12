package com.auton.bradley.myfe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Bradley on 04/11/2016.
 * Java File to hold activity and class related to logging in
 * activity that handles login screen
 */


public class FriendActivity extends AppCompatActivity {
                                        // global variable declarations
    private TabLayout tabLayout;
    ViewPager viewPager;
    public ArrayList<String> UIDs;
    public ArrayList<String> names;
    public ArrayList<String> Urls;
    public int index;

                                        // main function
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        Intent intent = getIntent();
        UIDs = intent.getStringArrayListExtra("uid");
        names = intent.getStringArrayListExtra("name");
        Urls = intent.getStringArrayListExtra("url");
        index = intent.getIntExtra("index",0);

        TextView naneTv = (TextView) findViewById(R.id.af_tv_profile_name);
        naneTv.setText(names.get(index));
        ImageView imageView = (ImageView) findViewById(R.id.af_img_profile_pic);
        RequestCreator picURL = Picasso.with(getBaseContext()).load(Urls.get(index));
        picURL.transform(new CircleTransform()).into(imageView);


        // load tab bar and tab data
        viewPager = (ViewPager) findViewById(R.id.af_viewpager_profile);                                       // find view underneith tabs
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.af_tabLayout_profile);                                            // find tab layout
        tabLayout.setupWithViewPager(viewPager);                                                    // setup view
        setupTabTitles();                                                                            // add icons to tabs


    }

    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());               // generating adapter
        adapter.addFragment(new ProfileAgendaFragment(), "Agenda");
        adapter.addFragment(new ProfilePhotosFragment(), "Photos");
        adapter.addFragment(new ProfileFriendsFragment(), "Friends");
        viewPager.setAdapter(adapter);
    }

    private void setupTabTitles() {
        tabLayout.getTabAt(0).setText(getString(R.string.profileAgenda_tabName));
        tabLayout.getTabAt(1).setText(getString(R.string.profilePhotos_tabName));
        tabLayout.getTabAt(2).setText("Your Friends");
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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}


