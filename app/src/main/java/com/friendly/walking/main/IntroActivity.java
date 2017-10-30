package com.friendly.walking.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.friendly.walking.R;

public class IntroActivity extends Activity {

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

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
