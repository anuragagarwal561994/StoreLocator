package com.storelocator.altaiezior;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.Toast;

import com.storelocator.altaiezior.api.UserDetailItem;
import com.storelocator.altaiezior.api.UserLoginServer;
import retrofit.RestAdapter;

/**
 * Created by altaiezior on 7/4/15.
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
    private UserLoginServer.UserAuthentication userAuthentication;

    UserLoginTask(String email, String password, LoginActivity context, EditText passwordView) {
        mEmail = email;
        mPassword = password;
        mLoginActivityContext = context;
        mRegisterAuthTask = null;
        mPasswordView = passwordView;
    }

    @Override
    protected LoginResponse doInBackground(Void... params) {
        UserLoginServer userDetail = new RestAdapter.Builder()
                .setServer(mLoginActivityContext.getString(R.string.base_url))
                .build()
                .create(UserLoginServer.class);
        userAuthentication = userDetail.check_user_authentication(mEmail, mPassword);
        return userAuthentication.getStatus();
    }

    @Override
    protected void onPostExecute(final LoginResponse responseStatus) {
        mLoginActivityContext.setmAuthTask(null);
        mLoginActivityContext.showProgress(false);

        switch (responseStatus){
            case NONE:
                break;
            case LOGIN:
                UserDetailItem userDetailItem =
                        userAuthentication.getUserInformation();
                mLoginActivityContext.getSharedPreferences(LOGIN_PREFERENCE_NAME, 0)
                        .edit()
                        .putBoolean("loggedIn", true)
                        .commit();
                SharedPreferences userProfilePreference =
                        mLoginActivityContext.getSharedPreferences(USER_PROFILE_PREFERENCE_NAME, 0);
                SharedPreferences.Editor userProfileEditor = userProfilePreference.edit();
                userProfileEditor.putString("First Name", userDetailItem.getFname()).apply();
                userProfileEditor.putString("Last Name", userDetailItem.getLname()).apply();
                userProfileEditor.putString("Email Address", userDetailItem.getFname()).apply();
                userProfileEditor.putLong("ID", userDetailItem.getId()).apply();
                userProfileEditor.putString("Shop Name", userDetailItem.getShop_name());
                userProfileEditor.putString("Shop Address", userDetailItem.getShop_address());
                userProfileEditor.putLong("Mobile Number", Long.parseLong(userDetailItem.getMobile()));
                if(userProfilePreference.getString("First Name", "").isEmpty())
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
                                        mLoginActivityContext);
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
