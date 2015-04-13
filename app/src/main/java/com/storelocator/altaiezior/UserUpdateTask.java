package com.storelocator.altaiezior;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by altaiezior on 13/4/15.
 */
public class UserUpdateTask extends AsyncTask<Void, Void, Boolean> {

    private final String mFirstName;
    private final String mLastName;
    private final String mEmail;
    private final String mPhoneNumber;
    private final Long mId;

    private final UserDetail mUserActivityContext;
    private final String LOG_TAG = UserUpdateTask.class.getSimpleName();
    private JSONObject response;
    private String USER_PROFILE_PREFERENCE_NAME = "UserProfile";

    UserUpdateTask(Long id, String fname, String lname, String email, String phone, UserDetail context) {
        mId = id;
        mFirstName = fname;
        mLastName = lname;
        mEmail = email;
        mPhoneNumber = phone;
        mUserActivityContext = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if(mId == 0)
            return false;
        final String API_BASE_URL = mUserActivityContext.getString(R.string.base_url) + "user";
        final String FIRST_NAME_PARAM = "fname";
        final String LAST_NAME_PARAM = "lname";
        final String EMAIL_PARAM = "email";
        //TODO: add a phpne number param
        final String ID_PARAM = "id";

        Uri buildUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendQueryParameter(FIRST_NAME_PARAM, mFirstName)
                .appendQueryParameter(LAST_NAME_PARAM, mLastName)
                .appendQueryParameter(EMAIL_PARAM, mEmail)
                .appendQueryParameter(ID_PARAM, Long.toString(mId))
                .build();

        String userProfileUpdateJson = new ApiCall(buildUri, "POST").sendRequest();
        if(userProfileUpdateJson!=null){
            try{
                response = new JSONObject(userProfileUpdateJson);
                return response.getBoolean("status");
            }catch(JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                Toast.makeText(mUserActivityContext,
                        mUserActivityContext.getString(R.string.error_parse_json),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        Toast.makeText(mUserActivityContext,
                mUserActivityContext.getString(R.string.error_updation),
                Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    protected void onPostExecute(final Boolean isUpdated) {
        mUserActivityContext.showProgress(false);
        String ToastMessage = null;
        if(isUpdated){
            SharedPreferences userProfilePreference =
                    mUserActivityContext.getSharedPreferences(USER_PROFILE_PREFERENCE_NAME, 0);
            SharedPreferences.Editor userProfilePreferenceEditor = userProfilePreference.edit();
            userProfilePreferenceEditor.putString("First Name", mFirstName).apply();
            userProfilePreferenceEditor.putString("Last Name", mLastName).apply();
            userProfilePreferenceEditor.putString("Email Address", mEmail).apply();
            userProfilePreferenceEditor.putString("Phone Number", mPhoneNumber).apply();
            ToastMessage = mUserActivityContext.getString(R.string.successful_updation);
        }
        else{
            ToastMessage = mUserActivityContext.getString(R.string.error_updation);
        }
        Toast.makeText(mUserActivityContext, ToastMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCancelled() {
        mUserActivityContext.showProgress(false);
    }
}
