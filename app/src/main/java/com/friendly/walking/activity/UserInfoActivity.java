package com.friendly.walking.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import com.friendly.walking.R;
import com.friendly.walking.permission.PermissionManager;
import com.friendly.walking.util.JWLog;

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

    private String                                  mAddress = "";
    private String                                  mLat = "";
    private String                                  mLot = "";

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
        mDoneButton.setEnabled(false);

        mInputAddressText.setOnFocusChangeListener(this);
        mInputStrollStartTimeText.setOnFocusChangeListener(this);
        mInputStrollEndTimeText.setOnFocusChangeListener(this);
    }

    public void onClickCallback(View v) {
        JWLog.e("","next_button");

        if(v == mDoneButton) {

        } else if(v.getId() == R.id.address) {

            showGoogleMap();
        } else if(v == mInputStrollStartTimeText || v == mInputStrollEndTimeText) {
            showTimePickerDialog(v);
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
