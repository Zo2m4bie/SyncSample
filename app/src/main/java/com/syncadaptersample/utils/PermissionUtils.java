package com.syncadaptersample.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by dima on 9/9/16.
 */
public class PermissionUtils {

    public static boolean isHaveAccountPermission(Context context) {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS);
    }
}
