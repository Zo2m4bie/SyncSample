package com.syncadaptersample.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by dima on 9/14/16.
 */
public abstract class BaseActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayout());
        init(savedInstanceState);
    }

    protected void init(Bundle savedInstanceState) {
    }

    public abstract int getContentLayout();
}
