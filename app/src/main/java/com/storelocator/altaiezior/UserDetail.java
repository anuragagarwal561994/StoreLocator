package com.storelocator.altaiezior;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UserDetail extends FragmentActivity {

    private SharedPreferences.Editor userProfilePreferenceEditor;
    private SharedPreferences userProfilePreference;

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private EditText mPhoneNumber;

    private View mUserDetailForm;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        userProfilePreference = getSharedPreferences("UserProfile", 0);
        userProfilePreferenceEditor = userProfilePreference.edit();

        mFirstName = (EditText) findViewById(R.id.first_name);
        mLastName = (EditText) findViewById(R.id.last_name);
        mEmail = (EditText) findViewById(R.id.email);
        mPhoneNumber = (EditText) findViewById(R.id.phone_number);

        mFirstName.setText(userProfilePreference.getString("First Name", null));
        mLastName.setText(userProfilePreference.getString("Last Name", null));
        mEmail.setText(userProfilePreference.getString("Email Address", null));
        mPhoneNumber.setText(userProfilePreference.getString("Phone Number", null));

        mUserDetailForm = findViewById(R.id.user_detail_form);
        mProgressView = findViewById(R.id.progressBar);
        mEmail.setEnabled(false);

        final UserDetail currentUserDetailContext = this;
        Button mButton = (Button) findViewById(R.id.update_user_detail);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Validate
                //TODO: Remove email from the edit list
                //TODO: Add phone number field
                Boolean cancel = false;
                if (TextUtils.isEmpty(mFirstName.getText().toString()) ||
                        isAlphabeticString(mFirstName.getText().toString())) {
                    mFirstName.setError("Invalid First Name");
                    mFirstName.requestFocus();
                    cancel = true;
                }
                if (isAlphabeticString(mLastName.getText().toString())) {
                    mLastName.setError("Invalid Last Name");
                    mLastName.requestFocus();
                    cancel = true;
                }
                if (TextUtils.isEmpty(mPhoneNumber.getText().toString()) ||
                        mPhoneNumber.getText().length()!=10) {
                    mPhoneNumber.setError("Invalid Phone Number");
                    mPhoneNumber.requestFocus();
                    cancel = true;
                }
                if(cancel)
                    return;
                currentUserDetailContext.showProgress(true);
                new UserUpdateTask(
                        userProfilePreference.getLong("ID", 0),
                        mFirstName.getText().toString(),
                        mLastName.getText().toString(),
                        mEmail.getText().toString(),
                        mPhoneNumber.getText().toString(),
                        currentUserDetailContext
                ).execute();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_detail, menu);
        return true;
    }

    private boolean isAlphabeticString(String toCheck) {
        //TODO: Replace this with your own logic
        return !toCheck.matches("[a-zA-Z]+");
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mUserDetailForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mUserDetailForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mUserDetailForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mUserDetailForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings: break;
            case R.id.action_logout:
                SharedPreferences.Editor loginPreference = getSharedPreferences("Login", 0).edit();
                loginPreference.putBoolean("loggedIn", false).commit();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
