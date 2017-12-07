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
import android.widget.Toast;

import com.friendly.walking.ApplicationPool;
import com.friendly.walking.GlobalConstantID;
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

    private EditText                            mEmailText;
    private EditText                            mPasswordText;
    private EditText                            mConfirmPasswordText;
    private EditText                            mInputAddressText;
    private EditText                            mInputStrollStartTimeText;
    private EditText                            mInputStrollEndTimeText;

    private Button                              mCheckDuplicationButton;
    private Button                              mNextButton;

    private CheckBox                            mAutoLogin;
    private CheckBox                            mAutoStroll;

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

        mEmailText = (EditText)findViewById(R.id.email_id);
        mPasswordText = (EditText)findViewById(R.id.password);
        mConfirmPasswordText = (EditText)findViewById(R.id.confirm_password);
        mInputAddressText = (EditText)findViewById(R.id.address);
        mInputStrollStartTimeText = (EditText)findViewById(R.id.stroll_start_time);
        mInputStrollEndTimeText = (EditText)findViewById(R.id.stroll_end_time);

        mAutoLogin = (CheckBox)findViewById(R.id.autologin_check);
        mAutoStroll = (CheckBox)findViewById(R.id.auto_stroll_check);

        mCheckDuplicationButton = (Button) findViewById(R.id.check_duplication_button);
        mNextButton = (Button)findViewById(R.id.next_button);
        mNextButton.setEnabled(false);

        mInputAddressText.setOnFocusChangeListener(this);
        mInputStrollStartTimeText.setOnFocusChangeListener(this);
        mInputStrollEndTimeText.setOnFocusChangeListener(this);
    }

    public void onClickCallback(View v) {
        JWLog.e("","next_button");

        if(v == mNextButton) {
            if(mCheckCompletedEmail == null || TextUtils.isEmpty(mCheckCompletedEmail) || !mCheckCompletedEmail.equals(mEmailText.getText().toString())) {
                Toast.makeText(this, R.string.require_check_email, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(this, R.string.password_error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, R.string.password_compare_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.email_error, Toast.LENGTH_SHORT).show();
            }
        } else if(v == mCheckDuplicationButton) {
            if(TextUtils.isEmpty(mEmailText.getText().toString())) {
                Toast.makeText(getApplicationContext(), R.string.empty_email, Toast.LENGTH_SHORT).show();
                return;
            }
            setProgressBar(View.VISIBLE);

            FireBaseNetworkManager.getInstance(this).findUserEmail(mEmailText.getText().toString(), new FireBaseNetworkManager.FireBaseNetworkCallback() {
                @Override
                public void onCompleted(boolean result, Object object) {
                    setProgressBar(View.INVISIBLE);

                    if(result) {
                        Toast.makeText(getApplicationContext(), R.string.unavailable_id, Toast.LENGTH_SHORT).show();
                        mNextButton.setEnabled(false);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.available_id, Toast.LENGTH_SHORT).show();
                        mNextButton.setEnabled(true);
                    }
               }
           });

            mCheckCompletedEmail = mEmailText.getText().toString();
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

        data.mem_email = mEmailText.getText().toString();
        data.mem_auto_login = mAutoLogin.isChecked();
        data.mem_notification_yn = true;
        data.mem_location_yn = false;

        if(!TextUtils.isEmpty(mAddress)) {
            data.mem_address.put("address", mAddress);
            data.mem_address.put("lat", mLat);
            data.mem_address.put("lot", mLot);
        }
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy.MM.dd.HH:mm:ss", Locale.KOREA );
        String dateTime = formatter.format(date);
        data.mem_register_datetime = dateTime;

        data.mem_last_login_datetime = "";
        data.mem_auto_stroll_mode = mAutoStroll.isChecked();

        if(mStartStrollHour != -1 && mEndStrollHour != -1) {
            data.mem_alarm_time.put("start", ""+mStartStrollHour+":"+mStartStrollMin);
            data.mem_alarm_time.put("end", ""+mEndStrollHour+":"+mEndStrollMin);
        }

        data.joinBy = "email";
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
