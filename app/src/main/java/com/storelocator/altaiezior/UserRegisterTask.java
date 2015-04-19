package com.storelocator.altaiezior;

import android.content.Intent;
import android.content.SharedPreferences;;
import android.os.AsyncTask;
import android.widget.Toast;

import com.storelocator.altaiezior.api.UserRegisterServer;
import retrofit.RestAdapter;

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
    private UserRegisterServer.RegisterResponse registerResponse;

    UserRegisterTask(String email, String password, LoginActivity context) {
        mEmail = email;
        mPassword = password;
        mLoginActivityContext = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        UserRegisterServer userRegisterServer = new RestAdapter.Builder()
                .setServer(mLoginActivityContext.getString(R.string.base_url))
                .build()
                .create(UserRegisterServer.class);
        registerResponse = userRegisterServer.register_user(mEmail, mPassword);
        return registerResponse.getStatus();
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
            userProfilePreference.edit().putLong("ID", registerResponse.getId()).commit();
            userProfilePreference.edit().putString("Email Address", mEmail).commit();
            mLoginActivityContext.startActivity(new Intent(mLoginActivityContext, UserDetail.class));
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