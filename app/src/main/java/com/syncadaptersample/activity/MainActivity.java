package com.syncadaptersample.activity;

import android.os.Bundle;
import android.view.View;

import com.syncadaptersample.R;

/**
 * Created by dima on 9/14/16.
 */
public class MainActivity extends BaseSyncActivity {
    @Override
    public int getContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        findViewById(R.id.start_sync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sync();
            }
        });
    }
}
