package com.friendly.walking.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.friendly.walking.R;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.PetData;
import com.friendly.walking.dataSet.UserData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.Crypto;
import com.friendly.walking.util.JWLog;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class ProfileActivity extends BaseActivity {

    private ImageView                           mProfileBackgroundImage;
    private ImageButton                         mSettingButton;
    private CircleImageView                     mProfileImage;
    private TextView                            mPetName;
    private ImageView                           mGenderImage;
    private TextView                            mEmailText;
    private ImageButton                         mLogoutButton;
    private ImageButton                         mChangePasswordButton;
    private ImageButton                         mChangePetInfoButton;
    private ImageButton                         mQuitServiceButton;

    private OnClickListener                     mClickListener;

    @Override
    public void onCreate(Bundle bundle) {
        JWLog.e("", "@@@ ");
        super.onCreate(bundle);

        setContentView(R.layout.activity_profile);

        mProfileBackgroundImage = (ImageView)findViewById(R.id.profileBackgroundImageView);
        mSettingButton = (ImageButton) findViewById(R.id.setting);
        mProfileImage = (CircleImageView)findViewById(R.id.profileImageView);
        mPetName = (TextView) findViewById(R.id.pet_name);
        mGenderImage = (ImageView)findViewById(R.id.gender);
        mEmailText = (TextView)findViewById(R.id.email);
        mLogoutButton = (ImageButton)findViewById(R.id.logout);
        mChangePasswordButton = (ImageButton)findViewById(R.id.change_password);
        mChangePetInfoButton = (ImageButton)findViewById(R.id.change_pet_info);
        mQuitServiceButton = (ImageButton)findViewById(R.id.quit_service);

        mClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == mLogoutButton) {
                    JWLog.e("", "로그아웃");
                    CommonUtil.alertDialogShow(ProfileActivity.this, "알림", "로그아웃 하시겠습니까?", new CommonUtil.CompleteCallback() {
                        @Override
                        public void onCompleted(boolean result, Object object) {
                            if(result) {

                                FireBaseNetworkManager.getInstance(ProfileActivity.this).logoutAccount(ProfileActivity.this);
                                Toast.makeText(ProfileActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

                                FireBaseNetworkManager.getInstance(ProfileActivity.this).deleteUserData(new FireBaseNetworkManager.FireBaseNetworkCallback() {
                                    @Override
                                    public void onCompleted(boolean result, Object object) {

                                        Intent i = new Intent(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI);
                                        i.putExtra("email", "");
                                        JWBroadCast.sendBroadcast(ProfileActivity.this, i);

                                        finish();
                                    }
                                });
                            }
                        }
                    });
                } else if(v == mChangePasswordButton) {
                    startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));

                } else if(v == mChangePetInfoButton) {

                } else if(v == mQuitServiceButton) {

                }
            }
        };

        mLogoutButton.setOnClickListener(mClickListener);
        mChangePasswordButton.setOnClickListener(mClickListener);
        mChangePetInfoButton.setOnClickListener(mClickListener);
        mQuitServiceButton.setOnClickListener(mClickListener);

        String email = getIntent().getStringExtra("email");
        mEmailText.setText(email);

        readUserData(email);
        readPetProfileImage(email);
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

                    Bitmap blurred = CommonUtil.blurRenderScript(getApplicationContext(), bitmap, 25);//second parametre is radius
                    //imageview.setImageBitmap(blurred);
                    mProfileImage.setImageBitmap(blurred);
                }
            }
        });
    }

    private void readUserData(String email) {
        FireBaseNetworkManager.getInstance(this).readUserData(email, new FireBaseNetworkManager.FireBaseNetworkCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                JWLog.e("", "result :"+result);
                UserData userData = (UserData) object;
                PetData petData = userData.pet_list.get(0);

                mPetName.setText(petData.petName);
                mGenderImage.setImageResource(petData.petGender == false ? R.drawable.male : R.drawable.female);
            }
        });
    }


}
