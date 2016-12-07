package com.auton.bradley.myfe;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.share.internal.DeviceShareDialogFragment.TAG;


// fragment that handles the friends tab
public class FriendFragment extends Fragment {
    ViewPager viewPager;
    TabLayout tabLayout;
    View rootView;
    LoginButton fbLinkButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                                // get friend tab layout
      //  mAuth = FirebaseAuth.getInstance();
        rootView = inflater.inflate(R.layout.fragment_friend, container, false);
        TextView signUp = (TextView) rootView.findViewById(R.id.friend_signUp_text);
        View LoggedInView = rootView.findViewById(R.id.friend_LoggedIn);
        fbLinkButton = (LoginButton) rootView.findViewById(R.id.connect_with_fb_button);
                                // get user info
        MainActivity activity = (MainActivity) getActivity();
        FirebaseUser user = activity.auth.getCurrentUser();
        Boolean fbCon = activity.facebookConnected;
        Log.d("refdvrfd",fbCon.toString());

        if(user != null) {
            if (fbCon) {
                signUp.setVisibility(View.GONE);
                fbLinkButton.setVisibility(View.GONE);
                LoggedInView.setVisibility(View.VISIBLE);
                // load tab bar and tab data into friend layout
                viewPager = (ViewPager) rootView.findViewById(R.id.viewpager_friend);
                setupViewpagerChild(viewPager);
                tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout_friend);
                tabLayout.setupWithViewPager(viewPager);
                setupTabTitles();
            }
            else {
                signUp.setVisibility(View.VISIBLE);
                signUp.setText("Link Facebook to see your friends!");
                fbLinkButton.setVisibility(View.VISIBLE);
                LoggedInView.setVisibility(View.GONE);

            }
        }
        else {
            signUp.setVisibility(View.VISIBLE);
            fbLinkButton.setVisibility(View.GONE);
            LoggedInView.setVisibility(View.GONE);
        }

                                // return the view
        return rootView;
    }

    public void setupViewpagerChild(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new FriendFeedFragment(), "Feed");
        adapter.addFragment(new FriendMapFragment(), "Map");
        viewPager.setAdapter(adapter);
    }

    private void setupTabTitles() {
        try {
            tabLayout.getTabAt(0).setText(getString(R.string.friendFeed_tabName));
            tabLayout.getTabAt(1).setText(getString(R.string.friendMap_tabName));
        }catch (NullPointerException e){
            e.printStackTrace();

        }
    }
}