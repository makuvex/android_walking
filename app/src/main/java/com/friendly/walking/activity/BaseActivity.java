package com.friendly.walking.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.friendly.walking.fragment.SettingFragment;
import com.friendly.walking.fragment.WalkingChartFragment;
import com.friendly.walking.main.IntroActivity;
import com.friendly.walking.main.MainActivity;
import com.friendly.walking.receiver.LoginOutReceiver;
import com.friendly.walking.util.JWLog;
import com.friendly.walking.util.JWToast;

import com.friendly.walking.R;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.permission.PermissionManager;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;

/**
 * Created by Administrator on 2017-10-28.
 */

public class BaseActivity extends AppCompatActivity {

    protected static int                       mVisibleState;

    protected ProgressBar                       mProgressBar;
    protected ViewGroup                         mRootView;

    protected ImageButton                       mBackButton;
    protected boolean                          mIsAcceptedPermission = false;

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

        if(!(this instanceof IntroActivity)) {
            if(!PermissionManager.isAcceptedStoragePermission(this)) {
                PermissionManager.requestStoragePermission(this);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mBackButton == null) {
            mBackButton = (ImageButton) findViewById(R.id.back);
            if(mBackButton != null) {
                mBackButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backIconPressed();
                    }
                });
            }
        }
        //FireBaseNetworkManager.getInstance(this).onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //FireBaseNetworkManager.getInstance(this).onStop();
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

            JWLog.e("visible "+visible);

            enableDisableView(mRootView, visible == View.VISIBLE ? false : true);
        }
    }

    protected void backIconPressed() {
        finish();
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

    @Override
    public void onRequestPermissionsResult(final int requestCode, String permissions[], int[] grantResults) {
        boolean isAcceptedPermission = true;
        String permissionName = null;

        JWLog.e("requestCode "+requestCode+", grantResults "+grantResults);
        switch (requestCode) {
            case PermissionManager.CAMERA_PERMISSION_REQUEST_CODE :
                permissionName = "카메라";
            case PermissionManager.STORAGE_PERMISSION_REQUEST_CODE :
                if(permissionName == null) {
                    permissionName = "저장공간";
                }
            case PermissionManager.LOCATION_PERMISSION_REQUEST_CODE :
                if(permissionName == null) {
                    permissionName = "위치";
                }

                for (int i = 0; permissions != null && i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        boolean showRationale = shouldShowRequestPermissionRationale(permissions[i]);
                        isAcceptedPermission = false;

                        JWLog.e("showRationale "+showRationale);

                        if (!showRationale) {
                            CommonUtil.alertDialogShow(this, permissionName, "권한이 허용되지 않았습니다.\n사용을 위해 설정 메뉴로 이동 하시겠습니까?", new CommonUtil.CompleteCallback() {
                                @Override
                                public void onCompleted(boolean result, Object object) {
                                    if(result) {
                                        PermissionManager.goPermissionSettingMenu(BaseActivity.this);
                                    } else {
                                        if(requestCode == PermissionManager.STORAGE_PERMISSION_REQUEST_CODE) {
                                            JWToast.showToastLong("저장공간(필수 권한)이 허용 되지 않아 앱을 종료 합니다.");
                                        } else {
                                            JWToast.showToast("취소 되었습니다.");
                                        }
                                    }
                                    if(requestCode == PermissionManager.STORAGE_PERMISSION_REQUEST_CODE) {
                                        finish();
                                    }
                                }
                            });
                            break;
                        } else {
                            if(requestCode == PermissionManager.STORAGE_PERMISSION_REQUEST_CODE) {
                                JWToast.showToastLong("저장공간(필수 권한)이 허용 되지 않아 앱을 종료 합니다.");
                                finish();
                            }
                        }
                    }
                }
                break;
        }

        JWLog.e("isAcceptedPermission "+isAcceptedPermission);
    }

    public void updateUI(String email) {
        Intent intent = new Intent(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI);
        intent.putExtra("email", email);
        intent.putExtra("autoLogin", PreferencePhoneShared.getAutoLoginYn(this));

        JWBroadCast.sendBroadcast(getApplicationContext(), intent);
    }

    public void refreshUserDta(String email) {
        Intent intent = new Intent(JWBroadCast.BROAD_CAST_REFRESH_USER_DATA);
        intent.putExtra("email", email);
        intent.putExtra("autoLogin", PreferencePhoneShared.getAutoLoginYn(this));

        JWBroadCast.sendBroadcast(getApplicationContext(), intent);
    }


    public void updateUIForLogout() {
        Intent intent = new Intent(JWBroadCast.BROAD_CAST_LOGOUT);
        intent.setPackage(getPackageName());
        //intent.setComponent(new ComponentName(getApplicationContext(), LoginOutReceiver.class));
        //intent.setComponent(new ComponentName(getApplicationContext(), SettingFragment.class));
        //intent.setComponent(new ComponentName(getApplicationContext(), MainActivity.class));
        //intent.setComponent(new ComponentName(getApplicationContext(), WalkingChartFragment.class));

        JWBroadCast.sendBroadcast(getApplicationContext(), intent);
    }

}
