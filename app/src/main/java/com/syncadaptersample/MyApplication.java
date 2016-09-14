package com.syncadaptersample;

import android.app.Application;
import android.content.Context;

/**
 * Created by dima on 9/14/16.
 */
public class MyApplication extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return sContext;
    }
}
