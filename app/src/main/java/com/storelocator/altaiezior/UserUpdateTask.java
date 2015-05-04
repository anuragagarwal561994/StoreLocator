package com.storelocator.altaiezior;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.storelocator.altaiezior.api.UserDetailServer;
import retrofit.RestAdapter;

/**
 * Created by altaiezior on 13/4/15.
 */
public class UserUpdateTask extends AsyncTask<Void, Void, Boolean> {

    private final String mFirstName;
    private final String mLastName;
    private final Long mMobileNumber;
    private final String mShopName;
    private final String mShopAddress;
    private final Long mId;

    private final UserDetail mUserActivityContext;
    private final String LOG_TAG = UserUpdateTask.class.getSimpleName();

    UserUpdateTask(Long id, String fname, String lname, Long mobile,
                   String shop_name, String shop_address, UserDetail context) {
        mId = id;
        mFirstName = fname;
        mLastName = lname;
        mShopName = shop_name;
        mShopAddress = shop_address;
        mMobileNumber = mobile;
        mUserActivityContext = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if(mId == 0)
            return false;
        UserDetailServer userDetailServer = new RestAdapter.Builder()
                .setServer(mUserActivityContext.getString(R.string.base_url))
                .build()
                .create(UserDetailServer.class);
        return userDetailServer.updateUser(mFirstName, mLastName, mShopName,
                mShopAddress, mMobileNumber, mId)
                .getStatus();
    }

    @Override
    protected void onPostExecute(final Boolean isUpdated) {
        mUserActivityContext.showProgress(false);
        Toast.makeText(mUserActivityContext,mUserActivityContext.getString(
                        isUpdated?R.string.successful_updation:R.string.error_updation),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCancelled() {
        mUserActivityContext.showProgress(false);
    }
}
