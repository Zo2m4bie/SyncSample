package com.syncadaptersample.manager;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.syncadaptersample.MyApplication;
import com.syncadaptersample.R;
import com.syncadaptersample.callback.ISyncCallback;
import com.syncadaptersample.observer.MySyncStateObserver;
import com.syncadaptersample.utils.Constants;
import com.syncadaptersample.utils.PermissionUtils;


/**
 * Created by dima on 9/6/16.
 */
public class AppSyncManager {

    public static final String TAG = AppSyncManager.class.getName();

    public static final String ACCOUNT = "dummyaccount";

    private Account mAccount;
    private Context mContext;
    private boolean isAttachedSyncStateListener;
    private Object mStatusChangeHandle;
    private ISyncCallback mSyncCallback;

    private MySyncStateObserver mSyncStateObserver;

    public AppSyncManager(Context context, ISyncCallback syncCallback) {
        loadOrCreateAccount();
        mContext = context;
        mSyncCallback = syncCallback;
    }

    /**
     * This method tries to get account in system. if account doesn't exists,
     * it will call methos @createAccount() which will create new one
     *
     * @return true - if account exists, false - if account doesn't exists and @createAccount() was called
     */
    private boolean loadOrCreateAccount() {
        mAccount = getAccount();
        if (mAccount == null) {
            createAccount();
            return false;
        }
        return true;
    }

    /**
     * @return return account of current app if it exists
     */
    public Account getAccount() {
        final AccountManager accountManager = AccountManager.get(MyApplication.getAppContext());
        String accountType = MyApplication.getAppContext().getString(R.string.account_type);
        Account[] accounts = accountManager.getAccountsByType(accountType);
        if (accounts.length > 0) {
            return accounts[0];
        } else {
            return null;
        }
    }

    /**
     * Create new stub account
     */
    public void createAccount() {
        Context context = MyApplication.getAppContext();
        String accountType = context.getString(R.string.account_type);
        Account newAccount = new Account(ACCOUNT, accountType);
        AccountManager accountManager = AccountManager.get(MyApplication.getAppContext());
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            ContentResolver.setSyncAutomatically(newAccount, Constants.AUTHORITY, true);
            ContentResolver.requestSync(newAccount, Constants.AUTHORITY, new Bundle());
        } else {
            //error
            Log.e(TAG, "Cannot create account!");
        }
    }

    /**
     * because Manifest.permission.GET_ACCOUNTS is dangerous,
     * app have to ask user about opportunity to us this permission
     *
     * @param activity - Activity class is needed to send permission request
     */
    public void sendPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.GET_ACCOUNTS},
                Constants.MY_PERMISSIONS_REQUEST_GET_ACCOUNT);
    }

    public boolean handleRequestPermissionsResult(Context context, int requestCode) {
        if (requestCode == Constants.MY_PERMISSIONS_REQUEST_GET_ACCOUNT) {
            if (PermissionUtils.isHaveAccountPermission(context)) {
                sync();
            } else {
                mSyncCallback.showImpossibleSyncMessageDialog();
            }
            return true;
        }
        return false;
    }

    /**
     * start sync
     */
    public void sync() {
        if (mAccount == null && !loadOrCreateAccount())
            return;
        ContentResolver.requestSync(mAccount, Constants.AUTHORITY, new Bundle());
    }

    /**
     * attach manager to sync adapter to listen sync adapter states
     */
    public void attach() {
        mSyncStateObserver = new MySyncStateObserver();
        mSyncStateObserver.attach(this);
        mStatusChangeHandle = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE, mSyncStateObserver);
        isAttachedSyncStateListener = true;
    }

    public void detach() {
        if (isAttachedSyncStateListener) {
            mSyncStateObserver.detach();
            ContentResolver.removeStatusChangeListener(mStatusChangeHandle);
            mStatusChangeHandle = null;
            mSyncStateObserver = null;
            isAttachedSyncStateListener = false;
        }
    }

    /**
     * Check is sync activated
     *
     * @return true - if app is syncing, false - if app isn't syncing
     */
    public boolean isSyncActive() {
        if (mAccount != null) {
            return ContentResolver.isSyncActive(mAccount, Constants.AUTHORITY);
        }
        return false;
    }

    /**
     * method will call when sync will be started
     */
    public void syncStarted() {
    }

    /**
     * method will call when sync will be finished
     */
    public void syncFinished() {
    }
}
