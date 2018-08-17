package com.friendly.walking.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.friendly.walking.R;
import com.friendly.walking.activity.BaseActivity;
import com.friendly.walking.fragment.PopupDialogFragment;
import com.friendly.walking.fragment.PopupDialogFragment.PopupButtonType;
import com.friendly.walking.permission.PermissionManager;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.JWLog;

import static com.friendly.walking.permission.PermissionManager.STORAGE_PERMISSION_REQUEST_CODE;

public class IntroActivity extends BaseActivity implements PopupDialogFragment.DialogButtonClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_main);

        JWLog.e("");
//        View introMotionAnim = findViewById(R.id.intro_motion);
//        AnimationDrawable ad = (AnimationDrawable) introMotionAnim.getBackground();
//        ad.start();

        if(!PreferencePhoneShared.getPermissionCheckOnce(this)) {
            PopupDialogFragment fragment = PopupDialogFragment.newInstance(false);
            fragment.setClickListener(this);
            fragment.show(getSupportFragmentManager(), "blur_sample");
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    moveMainActivity();
                    finish();
                }
            }, 1000);
        }
    }

    @Override
    public void onClicked(PopupButtonType buttonType) {
        if(buttonType == PopupButtonType.TYPE_BUTTON_CANCEL) {

        } else if(buttonType == PopupButtonType.TYPE_BUTTON_CONFIRM) {
            if(!PermissionManager.isAcceptedStoragePermission(this)) {
                PermissionManager.requestStoragePermission(this);
                return;
            }

            PreferencePhoneShared.setPermissionCheckOnce(this, true);
            moveMainActivity();
        }
        finish();
    }

    public void moveMainActivity() {
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        JWLog.e("onRequestPermissionsResult ");
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        if(PermissionManager.isAcceptedStoragePermission(this)) {
            PreferencePhoneShared.setPermissionCheckOnce(this, true);
            moveMainActivity();
            finish();
        }
    }

}
