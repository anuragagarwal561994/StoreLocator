package com.storelocator.altaiezior;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

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

/**
 * Created by altaiezior on 7/4/15.
 */
enum LoginResponse {NONE, LOGIN, NOT_FOUND, WRONG_PASSWORD, INTERRUPTED};
/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginTask extends AsyncTask<Void, Void, LoginResponse> {

    private final String mEmail;
    private final String mPassword;
    private final LoginActivity mLoginActivityContext;
    private UserRegisterTask mRegisterAuthTask;
    private final String LOG_TAG = UserLoginTask.class.getSimpleName();
    private final String LOGIN_PREFERENCE_NAME = "Login";
    private final String USER_PROFILE_PREFERENCE_NAME = "UserProfile";
    private final EditText mPasswordView;

    UserLoginTask(String email, String password, LoginActivity context, EditText passwordView) {
        mEmail = email;
        mPassword = password;
        mLoginActivityContext = context;
        mRegisterAuthTask = null;
        mPasswordView = passwordView;
    }

    @Override
    protected LoginResponse doInBackground(Void... params) {
        final String API_BASE_URL = mLoginActivityContext.getString(R.string.base_url) + "login";
        final String EMAIL_PARAM = "email";
        final String PASSWORD_PARAM = "password";


        Uri buildUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendQueryParameter(EMAIL_PARAM, mEmail)
                .appendQueryParameter(PASSWORD_PARAM, mPassword)
                .build();
        String loginJsonStr = new ApiCall(buildUri, "POST").sendRequest();
        if(loginJsonStr==null)
            return LoginResponse.INTERRUPTED;

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
        mLoginActivityContext.setmAuthTask(null);
        mLoginActivityContext.showProgress(false);

        switch (responseStatus){
            case NONE:
                break;
            case LOGIN:
                mLoginActivityContext.getSharedPreferences(LOGIN_PREFERENCE_NAME, 0)
                        .edit()
                        .putBoolean("loggedIn", true)
                        .commit();
                SharedPreferences userProfilePreference =
                        mLoginActivityContext.getSharedPreferences(USER_PROFILE_PREFERENCE_NAME, 0);
                if(userProfilePreference.getString("First Name", "").isEmpty() &&
                        userProfilePreference.getString("Last Name", "").isEmpty())
                    mLoginActivityContext.startActivity(new Intent(mLoginActivityContext, UserDetail.class));
                else
                    mLoginActivityContext.startActivity(new Intent(mLoginActivityContext, MainActivity.class));
                mLoginActivityContext.finish();
                break;
            case NOT_FOUND:
                new AlertDialog.Builder(mLoginActivityContext)
                        .setTitle(mLoginActivityContext.getString(R.string.login_dialog_title))
                        .setMessage(mLoginActivityContext.getString(R.string.login_dialog_message))
                        .setPositiveButton(mLoginActivityContext.getString(R.string.login_dialog_button_positive)
                                , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mLoginActivityContext.showProgress(true);
                                mRegisterAuthTask = new UserRegisterTask(mEmail, mPassword,
                                        mLoginActivityContext, mPasswordView);
                                mRegisterAuthTask.execute((Void) null);
                            }
                        })
                        .setNegativeButton(mLoginActivityContext.getString(R.string.login_dialog_button_negative), null)
                        .create()
                        .show();
                break;
            case WRONG_PASSWORD:
                mPasswordView.setError(mLoginActivityContext.getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
                break;
            case INTERRUPTED:
                Toast.makeText(mLoginActivityContext, mLoginActivityContext.getString(R.string.error_interrupted), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled() {
        mLoginActivityContext.setmAuthTask(null);
        mLoginActivityContext.showProgress(false);
    }
}
