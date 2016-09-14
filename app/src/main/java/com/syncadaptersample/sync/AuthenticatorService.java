package com.syncadaptersample.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by dima on 9/6/16.
 */
public class AuthenticatorService extends Service {
    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}