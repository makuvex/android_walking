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
import android.widget.Toast;

import com.friendly.walking.R;
import com.friendly.walking.dataSet.UserData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.main.MainActivity;
import com.friendly.walking.permission.PermissionManager;
import com.friendly.walking.util.JWLog;
import com.kakao.usermgmt.response.model.UserProfile;

import java.util.Calendar;

/**
 * Created by Administrator on 2017-12-19.
 */

public class UserInfoActivity extends BaseActivity implements View.OnFocusChangeListener {

    public static final int                        REQ_CODE_GOOGLE_MAP = 0;
    public static final String                     KEY_USER_DATA = "key_user_data";

    private EditText                                mInputAddressText;
    private EditText                                mInputStrollStartTimeText;
    private EditText                                mInputStrollEndTimeText;

    private Button                                  mDoneButton;
    private CheckBox                                mAutoStroll;

    private SeekBar                                 mDistanceBar;
    private TextView                                mDistanceText;

    private String                                  mAddress = "";
    private String                                  mLat = "";
    private String                                  mLot = "";
    private String                                  mEmail = "";
    private UserData                                mUserData;

    private int                                     mStartStrollHour = -1;
    private int                                     mStartStrollMin = -1;

    private int                                     mEndStrollHour = -1;
    private int                                     mEndStrollMin = -1;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_user_profile);

        mInputAddressText = (EditText)findViewById(R.id.address);
        mInputStrollStartTimeText = (EditText)findViewById(R.id.stroll_start_time);
        mInputStrollEndTimeText = (EditText)findViewById(R.id.stroll_end_time);

        mAutoStroll = (CheckBox)findViewById(R.id.auto_stroll_check);
        mDoneButton = (Button)findViewById(R.id.done_button);
        //mDoneButton.setEnabled(false);
        mDistanceBar = (SeekBar) findViewById(R.id.seek_bar);
        mDistanceText = (TextView)findViewById(R.id.distance_text);

        mInputAddressText.setOnFocusChangeListener(this);
        mInputStrollStartTimeText.setOnFocusChangeListener(this);
        mInputStrollEndTimeText.setOnFocusChangeListener(this);

        mDistanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                JWLog.e("progress :"+progress);
                progressChanged = progress;
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

        setProgressBar(View.VISIBLE);
        FireBaseNetworkManager.getInstance(this).readUserData(mEmail, new FireBaseNetworkManager.FireBaseNetworkCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                setProgressBar(View.INVISIBLE);

                if(result) {
                    mUserData = (UserData) object;

                    mAddress = mUserData.mem_address.get("address");
                    mLat = mUserData.mem_address.get("lat");
                    mLot = mUserData.mem_address.get("lot");
                    mInputAddressText.setText(TextUtils.isEmpty(mAddress) ? "" : mAddress);

                    String startTime = mUserData.mem_alarm_time.get("start");
                    mInputStrollStartTimeText.setText(TextUtils.isEmpty(startTime) ? "" : "시작 "+startTime.substring(0, 2) +":"+startTime.substring(2, 4));

                    String endTime = mUserData.mem_alarm_time.get("end");
                    mInputStrollEndTimeText.setText(TextUtils.isEmpty(endTime) ? "" : "종료 " +endTime.substring(0, 2) +":"+endTime.substring(2, 4));

                    mAutoStroll.setChecked(mUserData.mem_auto_stroll_mode);
                    mDistanceBar.setProgress(mUserData.mem_auto_stroll_distance/100);
                    mDistanceText.setText(mUserData.mem_auto_stroll_distance + " m");
                } else {
                    Toast.makeText(UserInfoActivity.this, "유저 데이터 로드 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onClickCallback(View v) {
        JWLog.e("","next_button");

        if(v == mDoneButton) {
            setProgressBar(View.VISIBLE);
            FireBaseNetworkManager.getInstance(this).updateUserData(mUserData.mem_email, mUserData, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                @Override
                public void onCompleted(boolean result, Object object) {
                    setProgressBar(View.INVISIBLE);
                    JWLog.e(result ? "사용자 정보 변경 성공" : "사용자 정보 변경 실패");
                    Toast.makeText(getApplicationContext(), result ? "사용자 정보 변경 성공" : "사용자 정보 변경 실패", Toast.LENGTH_SHORT).show();
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

                    mInputStrollStartTimeText.setText("시작 "+mStartStrollHour+":"+mStartStrollMin);
                    mUserData.mem_alarm_time.put("start",""+mStartStrollHour+mStartStrollMin);
                } else {
                    mEndStrollHour = hour;
                    mEndStrollMin = min;

                    mInputStrollEndTimeText.setText("종료 "+mEndStrollHour+":"+mEndStrollMin);
                    mUserData.mem_alarm_time.put("end",""+mEndStrollHour+mEndStrollMin);
                }
            }
        }, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);

        dialog.show();
    }

    private void showGoogleMap() {
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
}
