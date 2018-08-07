package com.friendly.walking.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import com.friendly.walking.BuildConfig;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.util.JWToast;

import com.friendly.walking.ApplicationPool;
import com.friendly.walking.GlobalConstantID;
import com.friendly.walking.dataSet.LocationData;
import com.friendly.walking.dataSet.PetData;
import com.friendly.walking.dataSet.UserData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.permission.PermissionManager;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.JWLog;
import com.friendly.walking.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class SignUpActivity extends BaseActivity implements View.OnFocusChangeListener {

    public static final int                     REQ_CODE_GOOGLE_MAP = 0;
    public static final String                  KEY_USER_DATA = "key_user_data";

    private EditText                            mNickNameText;
    private EditText                            mEmailText;
    private EditText                            mPasswordText;
    private EditText                            mConfirmPasswordText;
    private EditText                            mInputAddressText;
    private EditText                            mInputStrollStartTimeText;
    private EditText                            mInputStrollEndTimeText;

    private Button                              mCheckNickNameDuplicationButton;
    private Button                              mCheckDuplicationButton;
    private Button                              mNextButton;

    private CheckBox                            mAutoLogin;
    private CheckBox                            mAutoStroll;

    private String                              mCheckCompletedNickName;
    private String                              mCheckCompletedEmail;
    private String                              mAddress = "";
    private String                              mLat = "";
    private String                              mLot = "";

    private int                                 mStartStrollHour = -1;
    private int                                 mStartStrollMin = -1;

    private int                                 mEndStrollHour = -1;
    private int                                 mEndStrollMin = -1;

    @Override
    public void onCreate(Bundle bundle) {
        JWLog.e("", "@@@ ");
        super.onCreate(bundle);
        setContentView(R.layout.activity_signup);

        mNickNameText = (EditText)findViewById(R.id.nickname);
        mEmailText = (EditText)findViewById(R.id.email_id);
        mPasswordText = (EditText)findViewById(R.id.password);
        mConfirmPasswordText = (EditText)findViewById(R.id.confirm_password);
        mInputAddressText = (EditText)findViewById(R.id.address);
        mInputStrollStartTimeText = (EditText)findViewById(R.id.stroll_start_time);
        mInputStrollEndTimeText = (EditText)findViewById(R.id.stroll_end_time);

        mAutoLogin = (CheckBox)findViewById(R.id.autologin_check);
        mAutoStroll = (CheckBox)findViewById(R.id.auto_stroll_check);

        mCheckNickNameDuplicationButton = (Button) findViewById(R.id.check_duplication_nickname_button);
        mCheckDuplicationButton = (Button) findViewById(R.id.check_duplication_button);
        mNextButton = (Button)findViewById(R.id.next_button);

        if(BuildConfig.IS_DEBUG) {
            mNextButton.setEnabled(true);
        } else {
            mNextButton.setEnabled(false);
        }

        mInputAddressText.setOnFocusChangeListener(this);
        mInputStrollStartTimeText.setOnFocusChangeListener(this);
        mInputStrollEndTimeText.setOnFocusChangeListener(this);
    }

    public void onClickCallback(View v) {
        JWLog.e("","next_button");

        if(v == mNextButton) {
            if(BuildConfig.IS_DEBUG) {

                Intent intent = new Intent(this, SignUpPetActivity.class);
                //intent.putExtra(GlobalConstantID.SIGN_UP_EMAIL, mEmailText.getText().toString());
                intent.putExtra(GlobalConstantID.SIGN_UP_PASSWORD, "Malice77$$");

                ApplicationPool pool = (ApplicationPool)getApplicationContext();
                pool.putExtra(KEY_USER_DATA, intent, getUserData());

                startActivity(intent);
                return;
            }
            if(TextUtils.isEmpty(mNickNameText.getText())) {
                JWToast.showToast(R.string.empty_nickname);
                return ;
            }
            if(mCheckCompletedNickName == null || TextUtils.isEmpty(mCheckCompletedNickName) || !mCheckCompletedNickName.equals(mNickNameText.getText().toString())) {
                JWToast.showToast(R.string.require_check_email);
                return ;
            }
            if(mCheckCompletedEmail == null || TextUtils.isEmpty(mCheckCompletedEmail) || !mCheckCompletedEmail.equals(mEmailText.getText().toString())) {
                JWToast.showToast(R.string.require_check_email);
                return ;
            }

            if(isValidEmail(mEmailText.getText().toString())) {
                if(mPasswordText.getText().toString().equals(mConfirmPasswordText.getText().toString())) {
                    if(CommonUtil.isValidPassword(mPasswordText.getText().toString())) {

                        Intent intent = new Intent(this, SignUpPetActivity.class);
                        //intent.putExtra(GlobalConstantID.SIGN_UP_EMAIL, mEmailText.getText().toString());
                        intent.putExtra(GlobalConstantID.SIGN_UP_PASSWORD, mPasswordText.getText().toString());

                        ApplicationPool pool = (ApplicationPool)getApplicationContext();
                        pool.putExtra(KEY_USER_DATA, intent, getUserData());

                        startActivity(intent);
                    } else {
                        JWToast.showToast(R.string.password_error);
                    }
                } else {
                    JWToast.showToast(R.string.password_compare_error);
                }
            } else {
                JWToast.showToast(R.string.email_error);
            }
        } else if(v == mCheckDuplicationButton) {
            if(TextUtils.isEmpty(mEmailText.getText().toString())) {
                JWToast.showToast(R.string.empty_email);
                return;
            }
            setProgressBar(View.VISIBLE);

            FireBaseNetworkManager.getInstance(this).findUserEmail(mEmailText.getText().toString(), new FireBaseNetworkManager.FireBaseNetworkCallback() {
                @Override
                public void onCompleted(boolean result, Object object) {
                    setProgressBar(View.INVISIBLE);

                    if(result) {
                        JWToast.showToast(R.string.unavailable_id);
                        mNextButton.setEnabled(false);
                    } else {
                        mCheckCompletedEmail = mEmailText.getText().toString();
                        JWToast.showToast(R.string.available_id);
                        if(!TextUtils.isEmpty(mCheckCompletedNickName)) {
                            mNextButton.setEnabled(true);
                        } else {
                            mNextButton.setEnabled(false);
                        }
                    }
               }
           });

        } else if(v == mCheckNickNameDuplicationButton) {
            if(TextUtils.isEmpty(mNickNameText.getText().toString())) {
                JWToast.showToast(R.string.empty_nickname);
                return;
            }
            setProgressBar(View.VISIBLE);

            FireBaseNetworkManager.getInstance(this).findNickName(mNickNameText.getText().toString(), new FireBaseNetworkManager.FireBaseNetworkCallback() {
                @Override
                public void onCompleted(boolean result, Object object) {
                    setProgressBar(View.INVISIBLE);

                    if(result) {
                        JWToast.showToast(R.string.unavailable_nicknme);
                        mNextButton.setEnabled(false);
                    } else {
                        mCheckCompletedNickName = mNickNameText.getText().toString();
                        JWToast.showToast(R.string.available_nickname);
                        if(!TextUtils.isEmpty(mCheckCompletedEmail)) {
                            mNextButton.setEnabled(true);
                        } else {
                            mNextButton.setEnabled(false);
                        }
                    }
                }
            });
        } else if(v.getId() == R.id.address) {

            showGoogleMap();
        } else if(v == mInputStrollStartTimeText || v == mInputStrollEndTimeText) {
            showTimePickerDialog(v);
        }
    }

    private boolean isValidEmail(String target) {
        if (target == null || TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
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
                break;
        }
    }

    private UserData getUserData() {
        UserData data = new UserData();

        data.mem_nickname = mNickNameText.getText().toString();
        data.mem_email = mEmailText.getText().toString();
        data.mem_auto_login = mAutoLogin.isChecked();
        data.mem_notification_yn = true;
        data.mem_geo_notification_yn = true;
        data.mem_location_yn = true;

        if(BuildConfig.IS_DEBUG) {
            data.mem_nickname = "새우버거맛님";
            data.mem_email = "axur@naver.com";
            data.mem_auto_login = true;
        }
        if(!TextUtils.isEmpty(mAddress)) {
            data.mem_address.put("address", mAddress);
            data.mem_address.put("lat", mLat);
            data.mem_address.put("lot", mLot);
        }
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd:HH:mm:ss", Locale.KOREA );
        String dateTime = formatter.format(date);
        data.mem_register_datetime = dateTime;

        data.mem_last_login_datetime = "";
        data.mem_auto_stroll_mode = mAutoStroll.isChecked();

        if(mStartStrollHour != -1 && mEndStrollHour != -1) {
            data.mem_alarm_time.put("start", ""+mStartStrollHour+mStartStrollMin);
            data.mem_alarm_time.put("end", ""+mEndStrollHour+mEndStrollMin);
        }

        ArrayList<LocationData> list = new ArrayList<>();
        list.add(new LocationData());
        SimpleDateFormat formatter2 = new SimpleDateFormat( "yyyy-MM-dd", Locale.KOREA );
        String dateTime2 = formatter2.format(date);

        data.walking_location_list.put(dateTime2, list);
        data.walking_time_list.put(dateTime2, "0");
        data.mem_auto_stroll_distance = 100;

        data.joinBy = "email";
        data.walking_coin = 3;
        JWLog.e("","@@@ userData : "+data);

        return data;
    }

    private void showTimePickerDialog(final View view) {
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                if(view == mInputStrollStartTimeText) {
                    mStartStrollHour = hour;
                    mStartStrollMin = min;

                    mInputStrollStartTimeText.setText("시작 "+mStartStrollHour+":"+mStartStrollMin);
                } else {
                    mEndStrollHour = hour;
                    mEndStrollMin = min;

                    mInputStrollEndTimeText.setText("종료 "+mEndStrollHour+":"+mEndStrollMin);
                }
            }
        }, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);

        dialog.show();
    }

    private void showGoogleMap() {
        if(PermissionManager.isAcceptedLocationPermission(this)) {
            Intent intent = new Intent(this, GoogleMapActivity.class);
            startActivityForResult(intent, REQ_CODE_GOOGLE_MAP);
        } else {
            PermissionManager.requestLocationPermission(this);
        }
    }
}
