package com.friendly.walking.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.friendly.walking.firabaseManager.FireBaseNetworkManager;

/**
 * Created by Administrator on 2017-10-28.
 */

public class BaseActivity extends AppCompatActivity {

    protected static int        mVisibleState;
    protected ProgressBar       mProgressBar;
    protected ViewGroup         mRootView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mRootView = (ViewGroup) findViewById(android.R.id.content).getRootView();

        mProgressBar = new ProgressBar(BaseActivity.this, null, android.R.attr.progressBarStyle);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.INVISIBLE);
        mVisibleState = View.INVISIBLE;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout rl = new RelativeLayout(BaseActivity.this);

        rl.setGravity(Gravity.CENTER);
        rl.addView(mProgressBar);

        mRootView.addView(rl, params);

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

    @Override
    protected void onResume() {
        super.onResume();
        if(mVisibleState != View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void setProgressBar(int visible) {
        if(mProgressBar != null) {
            mProgressBar.setVisibility(visible);
            mVisibleState = visible;

            enableDisableView(mRootView, visible == View.VISIBLE ? false : true);
        }
    }

    protected void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;

            for (int idx = 0; idx < group.getChildCount(); idx++) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }


}
