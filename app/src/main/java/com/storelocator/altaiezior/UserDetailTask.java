package com.storelocator.altaiezior;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.storelocator.altaiezior.api.UserDetailItem;
import com.storelocator.altaiezior.api.UserDetailServer;

import retrofit.RestAdapter;

/**
 * Created by altaiezior on 4/5/15.
 */
public class UserDetailTask extends AsyncTask<Void, Void, UserDetailItem> {
    private Long mId;
    private final UserDetail mUserActivityContext;

    public UserDetailTask(Long id, UserDetail context){
        mId = id;
        mUserActivityContext = context;
    }

    @Override
    protected UserDetailItem doInBackground(Void... voids) {
        if(mId!=0){
            UserDetailServer userDetailServer = new RestAdapter.Builder()
                    .setServer(mUserActivityContext.getString(R.string.base_url))
                    .build()
                    .create(UserDetailServer.class);
            return userDetailServer.getShopkeeper(mId);
        }
        return null;
    }

    @Override
    protected void onPostExecute(final UserDetailItem userDetails) {
        mUserActivityContext.showProgress(false);
        if(userDetails!=null){
            mUserActivityContext.mFirstName.setText(userDetails.getFname());
            mUserActivityContext.mLastName.setText(userDetails.getLname());
            mUserActivityContext.mEmail.setText(userDetails.getEmail());
            if(userDetails.getMobile()!=null)
                mUserActivityContext.mMobileNumber.setText(userDetails.getMobile().toString());
            else
                mUserActivityContext.mMobileNumber.setText(null);
            mUserActivityContext.mShopAddress.setText(userDetails.getShop_address());
            mUserActivityContext.mShopName.setText(userDetails.getShop_name());
        }
        else{
            mUserActivityContext.mFirstName.setText(null);
            mUserActivityContext.mLastName.setText(null);
            mUserActivityContext.mEmail.setText(null);
            mUserActivityContext.mMobileNumber.setText(null);
            mUserActivityContext.mShopAddress.setText(null);
            mUserActivityContext.mShopName.setText(null);
        }
    }

    @Override
    protected void onCancelled() {
        mUserActivityContext.showProgress(false);
    }
}
