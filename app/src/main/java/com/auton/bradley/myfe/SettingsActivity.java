package com.auton.bradley.myfe;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.List;

/*
 */
public class SettingsActivity extends PreferenceActivity {
    private static boolean[] start = new boolean[3];
                // when a setting is changed

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(final Preference preference, Object value) {
            String stringValue = value.toString();                                                  // the value set
                            // firstly separate by type in order to set summaries
                                        //  all list preferences
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;                        // For list preferences, look up the correct display value in the preference's 'entries' list.
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(                                                              // Set the summary to reflect the new value.
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            }                           // ringtone preference
            else if (preference instanceof RingtonePreference) {
                if (TextUtils.isEmpty(stringValue)) {                                                // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));
                    if (ringtone == null) {  // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {                // Set the summary to reflect the new ringtone name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {                    // For all other preferences
                preference.setSummary(stringValue);                                                 // set the summary to the value's simple string representation
                Log.d("kidsklx", preference.getKey());
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (preference.getKey().equals("email_text")){
                    if (start[0] || stringValue.isEmpty()) {
                        preference.setSummary(user.getEmail());
                        preference.setDefaultValue(user.getEmail());
                        start[0] = false;
                    }
                    else {
                        // update email address
                        final CharSequence backup = preference.getSummary();
                        preference.setSummary(stringValue);
                        preference.setDefaultValue(stringValue);
                        user.updateEmail(stringValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                if (task.isSuccessful()) {
                                    Toast.makeText(preference.getContext(),"Email address updated",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    preference.setSummary(backup);
                                    preference.setDefaultValue(backup);
                                    Toast.makeText(preference.getContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                else if (preference.getKey().equals("password_text")){
                    preference.setDefaultValue("*********");
                    preference.setSummary("*********");
                    if (start[1] || stringValue.isEmpty()) {
                        start[1] = false;
                    }
                    else {
                        //update password
                        user.updatePassword(stringValue)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(preference.getContext(),"Password Updated",Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(preference.getContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }

                }
                else if (preference.getKey().equals("name_text")){
                    if (start[2] || stringValue.isEmpty()) {
                        preference.setSummary(user.getDisplayName());
                        preference.setDefaultValue(user.getDisplayName());
                        start[2] = false;
                    }
                    else {
                        final CharSequence backup = preference.getSummary();
                        preference.setSummary(stringValue);
                        preference.setDefaultValue(stringValue);
                        //update name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(stringValue)
                                //        .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(preference.getContext(),"User profile updated.",Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            preference.setSummary(backup);
                                            preference.setDefaultValue(backup);
                                            Toast.makeText(preference.getContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }

                }
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override           // setup settings page
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);

        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                                   // when back is click finish current activity
                finish();
            }
        });
    }

    @Override           // load list
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

                        // This method stops fragment injection in malicious applications. Make sure to deny any unknown fragments here.
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    // This fragment shows general preferences
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            start[0] = true; start[1] = true; start[2] = true;
            bindPreferenceSummaryToValue(findPreference("email_text"));
            bindPreferenceSummaryToValue(findPreference("password_text"));
            bindPreferenceSummaryToValue(findPreference("name_text"));
            findPreference("name_text").getContext();
    //        bindPreferenceSummaryToValue(findPreference("fbCon_switch"));
    //        bindPreferenceSummaryToValue(findPreference("profilePic_pref"));
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    //This fragment shows notification preferences only
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    // This fragment shows data and sync preferences
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
