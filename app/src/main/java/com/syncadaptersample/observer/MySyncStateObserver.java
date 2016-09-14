package com.syncadaptersample.observer;

import android.content.SyncStatusObserver;

import com.syncadaptersample.manager.AppSyncManager;

/**
 * Created by dima on 9/8/16.
 */
public class MySyncStateObserver implements SyncStatusObserver {

    private AppSyncManager mAppSyncManager;

    @Override
    public synchronized void onStatusChanged(int i) {
        if (mAppSyncManager == null)
            return;

        if (mAppSyncManager.isSyncActive()) {
            mAppSyncManager.syncStarted();
        } else {
            mAppSyncManager.syncFinished();
        }
    }

    public void attach(AppSyncManager appSyncManager) {
        mAppSyncManager = appSyncManager;
    }

    public synchronized void detach() {
        mAppSyncManager = null;
    }
}
