package com.auton.bradley.myfe;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MainActivity extends AppCompatActivity {
    // declarations
    Toolbar toolbar;
    private TabLayout tabLayout;
    ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_home_grey,
            R.drawable.ic_friend_feed_grey,
            R.drawable.ic_planner
    };
    public Bundle facebookData;
    public Boolean facebookConnected;
    public CallbackManager callbackManager;
    public FirebaseAuth auth;
    int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getApplicationContext());
        // load main activity layout
        setContentView(R.layout.activity_main);                                                     // load the main activity view
        // load action bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);                                             // enable the action bar (above tabbed menus)
        setSupportActionBar(toolbar);                                                               // you can edit action bar style in activity_main.xml
        // load tab bar and tab data
        viewPager = (ViewPager) findViewById(R.id.container);                                       // find view underneith tabs
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);                                            // find tab layout
        tabLayout.setupWithViewPager(viewPager);                                                    // setup view
        setupTabIcons();                                                                            // add icons to tabs
        // store user data if any
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            currentTab = intent.getIntExtra("tab", 0);
            facebookConnected = intent.getBooleanExtra("fbConnected", false);
            if (facebookConnected) {
                facebookData = intent.getBundleExtra("fbData");
            }
            Bundle searchPref = intent.getBundleExtra("searchPref");
        }

        if (facebookConnected == null) {
            auth.signOut();
            facebookConnected = false;
        }

        viewPager.setCurrentItem(currentTab);
    }

    // facebook - passes data back to facebook api
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);                            // Pass the activity result back to the Facebook SDK
                    // add to calendar button returns data
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == 1) {
                Snackbar snackbar = Snackbar
                        .make(viewPager, "Added to calendar", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar snackbar1 = Snackbar.make(view, "Removed from calendar", Snackbar.LENGTH_SHORT);
                                snackbar1.show();
                            }
                        });
                snackbar.show();

                FirebaseUser user = auth.getCurrentUser();
                if (user!=null) {       // upload selection to there agenda
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference agendaItem = database.child("users").child(user.getUid()).child("Agenda").push();
                    HashMap<String, String> pushData = new HashMap<>();
                    pushData.put("activity",data.getStringExtra("title"));
                    pushData.put("location",data.getStringExtra("location"));
                    pushData.put("date",data.getStringExtra("date"));
                    pushData.put("time",data.getStringExtra("time"));
                    agendaItem.setValue(pushData);
                }
            }
        }

    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());               // generating adapter
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new FriendFragment(), "Friend");
        adapter.addFragment(new ProfileFragment(), "Profile");
        viewPager.setAdapter(adapter);                                                              // set the adapter to the container
    }


    // create options menu in action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (auth.getCurrentUser() != null) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
        } else {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
        }
        return true;
    }

    // respond to action bar item press
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_preferences:
                Intent intent3 = new Intent(this, SearchPrefActivity.class);
                intent3.putExtra("fbData", facebookData);
                intent3.putExtra("fbCon", facebookConnected);
                startActivity(intent3);
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_logout:
                //     if (facebookConnected) {
                LoginManager.getInstance().logOut();
                //     }
                auth.signOut();
                facebookConnected = false;
                currentTab = viewPager.getCurrentItem();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("tab", viewPager.getCurrentItem());
                startActivity(intent);
                return true;
            case R.id.action_login:
                currentTab = viewPager.getCurrentItem();
                Intent intent2 = new Intent(this, LoginActivity.class);
                intent2.putExtra("tab", viewPager.getCurrentItem());
                startActivity(intent2);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}