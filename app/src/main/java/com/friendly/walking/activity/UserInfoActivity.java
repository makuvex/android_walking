package com.friendly.walking.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;

import com.friendly.walking.GlobalConstantID;
import com.friendly.walking.network.KakaoLoginManager;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.JWToast;

import com.friendly.walking.R;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.UserData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.main.MainActivity;
import com.friendly.walking.permission.PermissionManager;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.JWLog;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kakao.usermgmt.response.model.UserProfile;

import java.util.Calendar;

/**
 * Created by Administrator on 2017-12-19.
 */

public class UserInfoActivity extends BaseActivity implements View.OnFocusChangeListener {

    public static final int                        REQ_CODE_GOOGLE_MAP = 0;
    public static final String                     KEY_USER_DATA = "key_user_data";

    private EditText                                mNickNameText;
    private EditText                                mInputAddressText;
    private EditText                                mInputStrollStartTimeText;
    private EditText                                mInputStrollEndTimeText;

    private Button                                  mDoneButton;
    private CheckBox                                mAutoStroll;

    private SeekBar                                 mDistanceBar;
    private TextView                                mDistanceText;
    private TextView                                mTitleText;

    private String                                  mAddress = "";
    private String                                  mLat = "";
    private String                                  mLot = "";
    private String                                  mEmail = "";
    private UserData                                mUserData;

    private int                                     mStartStrollHour = -1;
    private int                                     mStartStrollMin = -1;

    private int                                     mEndStrollHour = -1;
    private int                                     mEndStrollMin = -1;

    private int                                     mLoginType = -1;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_user_profile);

        mNickNameText = (EditText)findViewById(R.id.nickname_text);
        mInputAddressText = (EditText)findViewById(R.id.address);
        mInputStrollStartTimeText = (EditText)findViewById(R.id.stroll_start_time);
        mInputStrollEndTimeText = (EditText)findViewById(R.id.stroll_end_time);

        mAutoStroll = (CheckBox)findViewById(R.id.auto_stroll_check);
        mDoneButton = (Button)findViewById(R.id.done_button);
        //mDoneButton.setEnabled(false);
        mDistanceBar = (SeekBar) findViewById(R.id.seek_bar);
        mDistanceText = (TextView)findViewById(R.id.distance_text);
        mTitleText = (TextView)findViewById(R.id.title_text);

        mInputAddressText.setOnFocusChangeListener(this);
        mInputStrollStartTimeText.setOnFocusChangeListener(this);
        mInputStrollEndTimeText.setOnFocusChangeListener(this);

        mDistanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                JWLog.e("progress :"+progress);
                progressChanged = progress;
                mUserData.mem_auto_stroll_distance = progressChanged == 0 ? 100 : progressChanged  * 100;
                mDistanceText.setText(mUserData.mem_auto_stroll_distance + " m");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                JWLog.e("");
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                JWLog.e("");
                mUserData.mem_auto_stroll_distance = progressChanged == 0 ? 100 : progressChanged  * 100;
                mDistanceText.setText(mUserData.mem_auto_stroll_distance + " m");
            }
        });

        mEmail = getIntent().getStringExtra("email");
        mLoginType = getIntent().getIntExtra(GlobalConstantID.SIGN_UP_TYPE, -1);

        JWLog.e("mLoginType "+mLoginType+", mEmail "+mEmail);

        setProgressBar(View.VISIBLE);
        FireBaseNetworkManager.getInstance(this).readUserData(mEmail, new FireBaseNetworkManager.FireBaseNetworkCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                setProgressBar(View.INVISIBLE);

                if(result) {
                    mUserData = (UserData) object;

                    mNickNameText.setText(mUserData.mem_nickname);
                    mAddress = mUserData.mem_address.get("address");
                    mLat = mUserData.mem_address.get("lat");
                    mLot = mUserData.mem_address.get("lot");
                    mInputAddressText.setText(TextUtils.isEmpty(mAddress) ? "" : mAddress);

                    String startTime = mUserData.mem_alarm_time.get("start");
                    JWLog.e("strtTime : "+startTime);
                    if(startTime != null) {
                        mInputStrollStartTimeText.setText(TextUtils.isEmpty(startTime) ? "" : "시작 "+startTime.substring(0, 2) +":"+startTime.substring(2, 4));
                        mStartStrollHour = Integer.parseInt(startTime.substring(0, 2));
                        mStartStrollMin = Integer.parseInt(startTime.substring(2, 4));
                    }
                    String endTime = mUserData.mem_alarm_time.get("end");
                    if(endTime != null) {
                        mInputStrollEndTimeText.setText(TextUtils.isEmpty(endTime) ? "" : "종료 " + endTime.substring(0, 2) + ":" + endTime.substring(2, 4));
                        mEndStrollHour = Integer.parseInt(endTime.substring(0, 2));
                        mEndStrollMin = Integer.parseInt(endTime.substring(2, 4));
                    }
                    mAutoStroll.setChecked(mUserData.mem_auto_stroll_mode);
                    mDistanceBar.setProgress(mUserData.mem_auto_stroll_distance/100);
                    mDistanceText.setText(mUserData.mem_auto_stroll_distance + " m");
                } else {
                    JWToast.showToast("유저 데이터 로드 실패");
                }
            }
        });
    }

    public void onClickCallback(View v) {
        JWLog.e("","next_button");

        if(v == mDoneButton) {
            if(TextUtils.isEmpty(mNickNameText.getText().toString())) {
                JWToast.showToast("닉네임을 입력해 주세요.");
                return;
            }
            if(mStartStrollHour != -1 && mStartStrollMin != -1) {
                if((mStartStrollHour*100 + mStartStrollMin) > (mEndStrollHour*100 + mEndStrollMin)) {
                    JWToast.showToast("산책 종료 시간은 시작 시간 보다 커야 합니다.");
                    showTimePickerDialog(mInputStrollEndTimeText);
                    return;
                }
            }

            mUserData.mem_nickname = mNickNameText.getText().toString();
            PreferencePhoneShared.setNickName(UserInfoActivity.this, mUserData.mem_nickname);

            setProgressBar(View.VISIBLE);
            FireBaseNetworkManager.getInstance(this).updateUserData(mUserData.mem_email, mUserData, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                @Override
                public void onCompleted(boolean result, Object object) {
                    setProgressBar(View.INVISIBLE);

                    Intent i = new Intent(JWBroadCast.BROAD_CAST_REFRESH_USER_DATA);
                    i.putExtra("email", mEmail);
                    JWBroadCast.sendBroadcast(UserInfoActivity.this, i);

                    JWLog.e(result ? "사용자 정보 변경 성공" : "사용자 정보 변경 실패");
                    JWToast.showToast(result ? "사용자 정보 변경 성공" : "사용자 정보 변경 실패");
                    finish();
                }
            });
        } else if(v.getId() == R.id.address) {

            showGoogleMap();
        } else if(v == mInputStrollStartTimeText || v == mInputStrollEndTimeText) {
            showTimePickerDialog(v);
        } else if(v == mAutoStroll) {
            JWLog.e("mAutoStroll check :"+mAutoStroll.isChecked());
            mUserData.mem_auto_stroll_mode = mAutoStroll.isChecked();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus) {
            if (v == mInputAddressText) {
                showGoogleMap();
            } else if (v == mInputStrollStartTimeText || v == mInputStrollEndTimeText) {
                showTimePickerDialog(v);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            return;
        }

        switch(requestCode) {
            case REQ_CODE_GOOGLE_MAP :
                mAddress = data.getExtras().getString("address");
                mInputAddressText.setText(mAddress);

                mLat = data.getExtras().getString("lat");
                mLot = data.getExtras().getString("lot");

                mUserData.mem_address.put("address", mAddress);
                mUserData.mem_address.put("lat", mLat);
                mUserData.mem_address.put("lot", mLot);
                break;
        }
    }

    private void showTimePickerDialog(final View view) {
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {

                if(view == mInputStrollStartTimeText) {
                    mStartStrollHour = hour;
                    mStartStrollMin = min;

                    String sHour = String.format("%02d", hour);
                    String sMin = String.format("%02d", min);

                    mInputStrollStartTimeText.setText("시작 "+sHour+":"+sMin);
                    mUserData.mem_alarm_time.put("start",""+sHour+sMin);
                } else {
                    mEndStrollHour = hour;
                    mEndStrollMin = min;

                    String sHour = String.format("%02d", hour);
                    String sMin = String.format("%02d", min);

                    mInputStrollEndTimeText.setText("종료 "+sHour+":"+sMin);
                    mUserData.mem_alarm_time.put("end",""+sHour+sMin);
                }
            }
        }, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);

        dialog.show();
    }

    private void showGoogleMap() {
        if(!CommonUtil.isLocationEnabled(this)) {
            JWToast.showToastLong("위치 정보를 사용할 수 없습니다. GPS를 확인해 주세요.");
            return;
        }
        if(PermissionManager.isAcceptedLocationPermission(this)) {
            Intent intent = new Intent(this, GoogleMapActivity.class);
            intent.putExtra("address", mInputAddressText.getText().toString());
            intent.putExtra("lat", mLat);
            intent.putExtra("lot", mLot);
            startActivityForResult(intent, REQ_CODE_GOOGLE_MAP);
        } else {
            PermissionManager.requestLocationPermission(this);
        }
    }

    @Override
    public void onBackPressed() {
        if(isGoogleJoinState()) {
            CommonUtil.alertDialogShow(this, "회원가입", "회원 가입을 취소 하시겠습니까?", new CommonUtil.CompleteCallback() {
                @Override
                public void onCompleted(boolean result, Object object) {
                    if(result) {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
                        FireBaseNetworkManager.getInstance(getApplicationContext()).deleteFireBaseUser(new FireBaseNetworkManager.FireBaseNetworkCallback() {
                            @Override
                            public void onCompleted(boolean result, Object object) {
                                FireBaseNetworkManager.getInstance(UserInfoActivity.this).logoutAccount();
                                updateUIForLogout();
                                finish();
                            }
                        });
                    }
                }
            });
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void backIconPressed() {
        if(isGoogleJoinState()) {
            onBackPressed();
            return;
        }
        super.backIconPressed();
    }

    private boolean isGoogleJoinState() {
        if(mLoginType == GlobalConstantID.LOGIN_TYPE_GOOGLE) {
            return true;
        }
        return false;
    }
}
