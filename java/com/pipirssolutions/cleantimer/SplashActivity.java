package com.pipirssolutions.cleantimer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.splash);


        //splash screen timer shows splashscreen and switches to main activity after
        Thread logoTimer = new Thread(){
            public void run() {
                try {
                    //splashscreen timer
                    sleep(1000);
                    //check manifest for more info
                    Intent mainIntent = new Intent("com.pipirssolutions.cleantimer.MAINVIEW");
                    startActivity(mainIntent);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally{
                    finish();

                }
            }
        };
        //show splashscreen
        logoTimer.start();

    }


}
