package com.friendly.walking.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.friendly.walking.firabaseManager.FireBaseNetworkManager;

/**
 * Created by Administrator on 2017-10-28.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FireBaseNetworkManager.getInstance(this).onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FireBaseNetworkManager.getInstance(this).onStop();
    }
}
