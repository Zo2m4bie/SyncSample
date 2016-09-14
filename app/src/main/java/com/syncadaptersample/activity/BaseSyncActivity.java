package com.syncadaptersample.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.syncadaptersample.MyApplication;
import com.syncadaptersample.R;
import com.syncadaptersample.callback.ISyncCallback;
import com.syncadaptersample.manager.AppSyncManager;
import com.syncadaptersample.utils.PermissionUtils;

/**
 * Created by dima on 9/8/16.
 */
public abstract class BaseSyncActivity extends BaseActivity implements ISyncCallback{

    protected AppSyncManager mAppAccountManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppAccountManager = new AppSyncManager(this, this);
        checkState();
    }

    protected void checkState() {
        if (mAppAccountManager.isSyncActive()) {
            //syncing
            Toast.makeText(MyApplication.getAppContext(), R.string.sync_in_progress, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        mAppAccountManager.handleRequestPermissionsResult(this, requestCode);
    }

    @Override
    public void sync() {
        if (!PermissionUtils.isHaveAccountPermission(this)) {
            mAppAccountManager.sendPermission(this);
            return;
        }
        mAppAccountManager.sync();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAppAccountManager.attach();
    }

    @Override
    protected void onStop() {
        mAppAccountManager.detach();
        super.onStop();
    }

    @Override
    public void showImpossibleSyncMessageDialog() {
        //Show messsage that user app cannot start sync
        Toast.makeText(MyApplication.getAppContext(), R.string.sync_impossible, Toast.LENGTH_SHORT).show();
    }
}
