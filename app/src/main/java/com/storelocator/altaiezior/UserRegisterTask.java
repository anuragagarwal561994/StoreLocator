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

    UserRegisterTask(String email, String password, LoginActivity context, EditText passwordView) {
        mEmail = email;
        mPassword = password;
        mLoginActivityContext = context;
        mPasswordView = passwordView;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        String registerJsonStr = null;
        final String API_BASE_URL = mLoginActivityContext.getString(R.string.base_url) + "register";
        final String EMAIL_PARAM = "email";
        final String PASSWORD_PARAM = "password";

        Uri buildUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendQueryParameter(EMAIL_PARAM, mEmail)
                .appendQueryParameter(PASSWORD_PARAM, mPassword)
                .build();

        try{
            registerJsonStr = new ApiCall(buildUri, "POST").sendRequest();
            if(registerJsonStr==null)
                return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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
        mLoginActivityContext.showProgress(false);
        if(isRegistered){
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