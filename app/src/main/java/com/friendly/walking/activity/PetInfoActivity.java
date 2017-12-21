package com.friendly.walking.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.friendly.walking.ApplicationPool;
import com.friendly.walking.GlobalConstantID;
import com.friendly.walking.R;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.PetData;
import com.friendly.walking.dataSet.UserData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.JWLog;

import java.io.File;
import java.util.List;


/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class PetInfoActivity extends SignUpPetActivity {

    protected TextView                      mTitleTextView;
    protected Button                        mDoneButton;
    protected PetData                       mPetData;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mEmail = getIntent().getStringExtra("email");
        mTitleTextView = (TextView)findViewById(R.id.title);
        mDoneButton = (Button)findViewById(R.id.sign_up);
        mTitleTextView.setText(R.string.change_pet_info);
        mDoneButton.setText(R.string.complete);

        setProgressBar(View.VISIBLE);
        FireBaseNetworkManager.getInstance(this).readPetData(mEmail, new FireBaseNetworkManager.FireBaseNetworkCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                setProgressBar(View.INVISIBLE);
                JWLog.e("result :"+result);
                if(result) {
                    if(object != null) {
                        List<PetData> list = (List<PetData>)object;
                        mPetData = list.get(0);
                        setPetData();
                    }
                }
            }
        });
    }

    @Override
    public void onClickCallback(View v) {
        if(v == mDoneButton) {
            setProgressBar(View.VISIBLE);

            mPetData.petName = mPetName.getText().toString();
            mPetData.petGender = mMaleCheck.isSelected() ? false : true;

            FireBaseNetworkManager.getInstance(this).updatePetData(mUserData, mPetData, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                @Override
                public void onCompleted(boolean result, Object object) {
                    setProgressBar(View.INVISIBLE);
                    JWLog.e("result :"+result+", object :"+object);

                    if(result) {
                        Toast.makeText(PetInfoActivity.this, "반려견 정보가 변경되었습니다.", Toast.LENGTH_SHORT).show();

                        JWLog.e("mImageCaptureUri :"+mImageCaptureUri);
                        if (mImageCaptureUri != null) {
                            try {
                                FireBaseNetworkManager.getInstance(getApplicationContext()).uploadProfileImage(mImageCaptureUri, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                                    @Override
                                    public void onCompleted(boolean result, Object object) {
                                        if (result) {
                                            Toast.makeText(getApplicationContext(), "프로필 사진 업로드 성공", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "프로필 사진 업로드 실패", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    } else {
                        Toast.makeText(PetInfoActivity.this, "반려견 정보 변경이 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            });
        } else {
            super.onClickCallback(v);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        super.onDateSet(view, year, monthOfYear, dayOfMonth);
        mPetData.birthDay = mPetBirthDate.getText().toString();
    }

    @Override
    public void onPick(int[] selectedValues, int key) {
        super.onPick(selectedValues, key);

        if(key == PICKER_DATA_TYPE_SPECIES) {
            mPetData.petSpecies = mPetSpecies.getText().toString();
        } else if(key == PICKER_DATA_TYPE_RELATION) {
            mPetData.petRelation = mPetRelation.getText().toString();
        }
    }

    protected void setPetData() {
        mPetName.setText(mPetData.petName);
        mPetBirthDate.setText(mPetData.birthDay);
        mPetSpecies.setText(mPetData.petSpecies);
        mPetRelation.setText(mPetData.petRelation);
        if(mPetData.petGender == false) {
            mMaleCheck.setSelected(true);
        } else {
            mFemaleCheck.setSelected(true);
        }

        readPetProfileImage(mPetData.mem_email);
    }

    private void readPetProfileImage(String email) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Uri uri = Uri.parse(path.getAbsolutePath() + "/" + email +"_pet_profile.jpg");
        JWLog.e("","uri :"+uri.toString());

        FireBaseNetworkManager.getInstance(this).downloadProfileImage(uri, new FireBaseNetworkManager.FireBaseNetworkCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                if(result) {
                    Bitmap bitmap = (Bitmap)object;
                    mAddProfile.setBackground(new BitmapDrawable(bitmap));
                }
            }
        });
    }
}
