package com.storelocator.altaiezior;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class UserDetail extends FragmentActivity {

    private SharedPreferences.Editor userProfilePreferenceEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        userProfilePreferenceEditor = getSharedPreferences("UserProfile", 0).edit();

        Button mUpdateButton = (Button) findViewById(R.id.update_user_detail);
        final EditText mFirstName = (EditText) findViewById(R.id.first_name);
        final EditText mLastName = (EditText) findViewById(R.id.last_name);
        final EditText mEmail = (EditText) findViewById(R.id.email);
        final EditText mPhoneNumber = (EditText) findViewById(R.id.phone_number);

        final Context currentContext = this;

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Validate
                userProfilePreferenceEditor.putString("First Name", mFirstName.getText().toString()).commit();
                userProfilePreferenceEditor.putString("Last Name", mLastName.getText().toString()).commit();
                userProfilePreferenceEditor.putString("Email Address", mEmail.getText().toString());
                userProfilePreferenceEditor.putString("Phone Number", mPhoneNumber.getText().toString());
                Toast.makeText(currentContext, "Information Updated",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_detail, menu);
        return true;
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_user_detail, container, false);
            return rootView;
        }
    }
}
