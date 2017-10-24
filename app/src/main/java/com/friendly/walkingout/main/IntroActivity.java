package com.friendly.walkingout.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.friendly.walkingout.R;

public class IntroActivity extends Activity {

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_main);

//        View introMotionAnim = findViewById(R.id.intro_motion);
//        AnimationDrawable ad = (AnimationDrawable) introMotionAnim.getBackground();
//        ad.start();

        handler.sendEmptyMessageDelayed(0, 1000);
    }
}
