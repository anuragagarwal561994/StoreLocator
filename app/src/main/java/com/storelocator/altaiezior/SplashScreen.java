package com.storelocator.altaiezior;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


public class SplashScreen extends Activity {

    public static final String LOGIN_PREFERENCE_NAME = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final SharedPreferences loginPreference = getSharedPreferences(LOGIN_PREFERENCE_NAME, 0);

        final Intent nextActivity;
        if(loginPreference.getBoolean("loggedIn", false))
            nextActivity = new Intent(this, MainActivity.class);
        else
            nextActivity = new Intent(this, LoginActivity.class);

        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    startActivity(nextActivity);
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
