package com.storelocator.altaiezior;

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
 * Represents an asynchronous registration task used to authenticate
 * the user.
 */
public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

    private final String mEmail;
    private final String mPassword;
    private final LoginActivity mLoginActivityContext;
    private final String LOG_TAG = UserRegisterTask.class.getSimpleName();
    private final String LOGIN_PREFERENCE_NAME = "Login";
    private final String USER_PROFILE_PREFERENCE_NAME = "UserProfile";
    private final EditText mPasswordView;
    private JSONObject response;

    UserRegisterTask(String email, String password, LoginActivity context, EditText passwordView) {
        mEmail = email;
        mPassword = password;
        mLoginActivityContext = context;
        mPasswordView = passwordView;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        final String API_BASE_URL = mLoginActivityContext.getString(R.string.base_url) + "register";
        final String EMAIL_PARAM = "email";
        final String PASSWORD_PARAM = "password";

        Uri buildUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendQueryParameter(EMAIL_PARAM, mEmail)
                .appendQueryParameter(PASSWORD_PARAM, mPassword)
                .build();

        String registerJsonStr = new ApiCall(buildUri, "PUT").sendRequest();
        if(registerJsonStr==null)
            return false;

        try{
            response = new JSONObject(registerJsonStr);
            return response.getBoolean("status");
        }catch(JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            Toast.makeText(mLoginActivityContext,
                    mLoginActivityContext.getString(R.string.error_parse_json),
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    protected void onPostExecute(final Boolean isRegistered) {
        mLoginActivityContext.showProgress(false);
        if(isRegistered){
            mLoginActivityContext.getSharedPreferences(LOGIN_PREFERENCE_NAME, 0)
                    .edit()
                    .putBoolean("loggedIn", true)
                    .commit();
            SharedPreferences userProfilePreference =
                    mLoginActivityContext.getSharedPreferences(USER_PROFILE_PREFERENCE_NAME, 0);
            try {
                userProfilePreference.edit().putLong("ID", response.getLong("id")).commit();
                userProfilePreference.edit().putString("Email Address", mEmail).commit();
                mLoginActivityContext.startActivity(new Intent(mLoginActivityContext, UserDetail.class));
                mLoginActivityContext.finish();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(mLoginActivityContext,
                        mLoginActivityContext.getString(R.string.error_parse_json),
                        Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(mLoginActivityContext, mLoginActivityContext.getString(R.string.error_registration), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled() {
        mLoginActivityContext.showProgress(false);
    }
}