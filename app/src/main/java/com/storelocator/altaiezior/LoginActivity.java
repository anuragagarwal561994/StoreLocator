package com.storelocator.altaiezior;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
public class LoginActivity extends PlusBaseActivity implements LoaderCallbacks<Cursor> {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mEmailLoginFormView;
    private SignInButton mPlusSignInButton;
    private View mSignOutButtons;
    private View mLoginFormView;
    private SharedPreferences.Editor loginPreferenceEditor;
    private SharedPreferences userProfilePreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginPreferenceEditor = getSharedPreferences("Login", 0).edit();
        userProfilePreference = getSharedPreferences("UserProfile", 0);

        // Find the Google+ sign in button.
        mPlusSignInButton = (SignInButton) findViewById(R.id.plus_sign_in_button);
        if (supportsGooglePlayServices()) {
            // Set a listener to connect the user when the G+ button is clicked.
            mPlusSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });
        } else {
            // Don't offer G+ sign in if the app's version is too low to support Google Play
            // Services.
            mPlusSignInButton.setVisibility(View.GONE);
            return;
        }

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mEmailLoginFormView = findViewById(R.id.email_login_form);
        mSignOutButtons = findViewById(R.id.plus_sign_out_buttons);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password, this);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onPlusClientSignIn() {
        //Set up sign out and disconnect buttons.
        Button signOutButton = (Button) findViewById(R.id.plus_sign_out_button);
        signOutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        Button disconnectButton = (Button) findViewById(R.id.plus_disconnect_button);
        disconnectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                revokeAccess();
            }
        });
    }

    @Override
    protected void onPlusClientBlockingUI(boolean show) {
        showProgress(show);
    }

    @Override
    protected void updateConnectButtonState() {
        //TODO: Update this logic to also handle the user logged in by email.
        boolean connected = getPlusClient().isConnected();

        mSignOutButtons.setVisibility(connected ? View.VISIBLE : View.GONE);
        mPlusSignInButton.setVisibility(connected ? View.GONE : View.VISIBLE);
        mEmailLoginFormView.setVisibility(connected ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onPlusClientRevokeAccess() {
        // TODO: Access to the user's G+ account has been revoked.  Per the developer terms, delete
        // any stored user data here.
    }

    @Override
    protected void onPlusClientSignOut() {

    }

    /**
     * Check if the device supports Google Play Services.  It's best
     * practice to check first rather than handling this as an error case.
     *
     * @return whether the device supports Google Play Services
     */
    private boolean supportsGooglePlayServices() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) ==
                ConnectionResult.SUCCESS;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private enum LoginResponse {NONE, LOGIN, NOT_FOUND, WRONG_PASSWORD, INTERRUPTED};

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, LoginResponse>{

        private final String mEmail;
        private final String mPassword;
        private final Context mContext;
        private UserRegisterTask mRegisterAuthTask;
        private final String LOG_TAG = UserLoginTask.class.getSimpleName();

        UserLoginTask(String email, String password, Context context) {
            mEmail = email;
            mPassword = password;
            mContext = context;
            mRegisterAuthTask = null;
        }

        @Override
        protected LoginResponse doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String loginJsonStr = null;

            try {
                final String API_BASE_URL = getString(R.string.base_url) + "login";
                final String EMAIL_PARAM = "email";
                final String PASSWORD_PARAM = "password";

                Uri buildUri = Uri.parse(API_BASE_URL).buildUpon()
                        .appendQueryParameter(EMAIL_PARAM, mEmail)
                        .appendQueryParameter(PASSWORD_PARAM, mPassword)
                        .build();
                URL url = new URL(buildUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return LoginResponse.INTERRUPTED;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return LoginResponse.INTERRUPTED;
                }
                loginJsonStr = buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return LoginResponse.INTERRUPTED;
            } catch (ProtocolException e) {
                e.printStackTrace();
                return LoginResponse.INTERRUPTED;
            } catch (IOException e) {
                e.printStackTrace();
                return LoginResponse.INTERRUPTED;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try{
                return getLoginDataFromJson(loginJsonStr);
            }catch(JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return LoginResponse.INTERRUPTED;
        }

        private LoginResponse getLoginDataFromJson(String loginJsonStr)
                throws JSONException {
            return LoginResponse.valueOf(new JSONObject(loginJsonStr).getString("status"));
        }

        @Override
        protected void onPostExecute(final LoginResponse responseStatus) {
            mAuthTask = null;
            showProgress(false);

            switch (responseStatus){
                case NONE:
                    break;
                case LOGIN:
                    loginPreferenceEditor.putBoolean("loggedIn", true).commit();
                    if(userProfilePreference.getString("First Name", "").isEmpty() &&
                            userProfilePreference.getString("Last Name", "").isEmpty())
                        mContext.startActivity(new Intent(mContext, UserDetail.class));
                    else
                        mContext.startActivity(new Intent(mContext, MainActivity.class));
                    finish();
                    break;
                case NOT_FOUND:
                    new AlertDialog.Builder(mContext)
                            .setTitle(getString(R.string.login_dialog_title))
                            .setMessage(getString(R.string.login_dialog_message))
                            .setPositiveButton(getString(R.string.login_dialog_button_positive), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    showProgress(true);
                                    mRegisterAuthTask = new UserRegisterTask(mEmail, mPassword, mContext);
                                    mRegisterAuthTask.execute((Void) null);
                                }
                            })
                            .setNegativeButton(getString(R.string.login_dialog_button_negative), null)
                            .create()
                            .show();
                    break;
                case WRONG_PASSWORD:
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    break;
                case INTERRUPTED:
                    Toast.makeText(mContext, getString(R.string.error_interrupted), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        public class UserRegisterTask extends AsyncTask<Void, Void, Boolean>{

            private final String mEmail;
            private final String mPassword;
            private final Context mContext;

            UserRegisterTask(String email, String password, Context context) {
                mEmail = email;
                mPassword = password;
                mContext = context;
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String registerJsonStr = null;

                try {
                    final String API_BASE_URL = getString(R.string.base_url) + "register";
                    final String EMAIL_PARAM = "email";
                    final String PASSWORD_PARAM = "password";

                    Uri buildUri = Uri.parse(API_BASE_URL).buildUpon()
                            .appendQueryParameter(EMAIL_PARAM, mEmail)
                            .appendQueryParameter(PASSWORD_PARAM, mPassword)
                            .build();
                    URL url = new URL(buildUri.toString());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("PUT");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return false;
                    }

                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return false;
                    }
                    registerJsonStr = buffer.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return false;
                } catch (ProtocolException e) {
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally{
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }

                try{
                    return getRegistrationResponseFromJson(registerJsonStr);
                }catch(JSONException e){
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                return false;
            }

            private boolean getRegistrationResponseFromJson(String registerJsonStr)
            throws JSONException{
                return Boolean.valueOf(new JSONObject(registerJsonStr).getString("status"));
            }

            @Override
            protected void onPostExecute(final Boolean isRegistered) {
                mRegisterAuthTask = null;
                showProgress(false);
                if(isRegistered){
                    loginPreferenceEditor.putBoolean("loggedIn", true).commit();
                    if(userProfilePreference.getString("First Name", "").isEmpty() &&
                            userProfilePreference.getString("Last Name", "").isEmpty())
                        mContext.startActivity(new Intent(mContext, UserDetail.class));
                    else
                        mContext.startActivity(new Intent(mContext, MainActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(mContext, getString(R.string.error_registration), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onCancelled() {
                mRegisterAuthTask = null;
                showProgress(false);
            }
        }
    }
}