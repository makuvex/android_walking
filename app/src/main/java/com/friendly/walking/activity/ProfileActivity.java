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

import com.friendly.walking.ApplicationPool;
import com.friendly.walking.GlobalConstantID;
import com.friendly.walking.R;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.PetData;
import com.friendly.walking.dataSet.UserData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.network.KakaoLoginManager;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.Crypto;
import com.friendly.walking.util.JWLog;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.friendly.walking.activity.SignUpActivity.KEY_USER_DATA;

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
    private ImageButton                         mChangeUserInfoButton;
    private ImageButton                         mChangePetInfoButton;
    private ImageButton                         mQuitServiceButton;
    private View                                mChangePassworLayout;
    private ImageView                           mLoginTypeImage;

    private UserData                            mUserData;

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
        mChangeUserInfoButton = (ImageButton)findViewById(R.id.change_user_info);
        mChangePetInfoButton = (ImageButton)findViewById(R.id.change_pet_info);
        mQuitServiceButton = (ImageButton)findViewById(R.id.quit_service);
        mChangePassworLayout = findViewById(R.id.change_password_layout);
        mLoginTypeImage = (ImageView)findViewById(R.id.loginImage);

        mClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == mLogoutButton) {
                    JWLog.e("", "로그아웃");
                    requestLogout();
                } else if(v == mChangePasswordButton) {
                    startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));

                } else if(v == mChangePetInfoButton) {
                    Intent i = new Intent(ProfileActivity.this, PetInfoActivity.class);
                    i.putExtra("email", mEmailText.getText());
                    ApplicationPool pool = (ApplicationPool)getApplicationContext();
                    pool.putExtra(KEY_USER_DATA, i, mUserData);

                    startActivity(i);
                } else if(v == mQuitServiceButton) {
                    JWLog.e("", "탈퇴");
                    quitService();
                } else if(v == mChangeUserInfoButton) {
                    Intent i = new Intent(ProfileActivity.this, UserInfoActivity.class);
                    i.putExtra("email", mEmailText.getText());
                    startActivity(i);
                }
            }
        };

        mLogoutButton.setOnClickListener(mClickListener);
        mChangePasswordButton.setOnClickListener(mClickListener);
        mChangePetInfoButton.setOnClickListener(mClickListener);
        mQuitServiceButton.setOnClickListener(mClickListener);
        mChangeUserInfoButton.setOnClickListener(mClickListener);

        if(PreferencePhoneShared.getAutoLoginType(this) == GlobalConstantID.LOGIN_TYPE_KAKAO) {
            mChangePassworLayout.setVisibility(View.GONE);
        }
        String email = getIntent().getStringExtra("email");
        mEmailText.setText(email);

        readUserData(email);
        readPetProfileImage(email);

        if(PreferencePhoneShared.getAutoLoginType(this) == GlobalConstantID.LOGIN_TYPE_KAKAO) {
            mLoginTypeImage.setImageResource(R.drawable.k);
        } else if(PreferencePhoneShared.getAutoLoginType(this) == GlobalConstantID.LOGIN_TYPE_FACEBOOK) {
            mLoginTypeImage.setImageResource(R.drawable.f);
        } else if(PreferencePhoneShared.getAutoLoginType(this) == GlobalConstantID.LOGIN_TYPE_GOOGLE) {
            mLoginTypeImage.setImageResource(R.drawable.g);
        } else {
            mLoginTypeImage.setImageResource(R.drawable.e);
        }
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
                mUserData = (UserData) object;
                if(mUserData != null) {
                    PetData petData = mUserData.pet_list.get(0);

                    mPetName.setText(petData.petName);
                    mGenderImage.setImageResource(petData.petGender == false ? R.drawable.male : R.drawable.female);
                }
            }
        });
    }

    private void quitService() {

        CommonUtil.alertDialogShow(ProfileActivity.this, "알림", "서비스를 탈퇴 하시겠습니까?\n탈퇴 시 모든 정보가 삭제 됩니다.", new CommonUtil.CompleteCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                if(result) {

                    FireBaseNetworkManager.getInstance(ProfileActivity.this).deleteUserData(new FireBaseNetworkManager.FireBaseNetworkCallback() {
                        @Override
                        public void onCompleted(boolean result, Object object) {
                            //Toast.makeText(getApplicationContext(), "정상적으로 탈퇴 되었습니다", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "유저 데이터 삭제 성공", Toast.LENGTH_SHORT).show();

                            if(result) {
                                FirebaseMessaging.getInstance().unsubscribeFromTopic("news");

                                if (PreferencePhoneShared.getAutoLoginType(getApplicationContext()) == GlobalConstantID.LOGIN_TYPE_KAKAO) {
                                    KakaoLoginManager.getInstance(ProfileActivity.this).unlinkApp(new KakaoLoginManager.KakaoLoginManagerCallback() {
                                        @Override
                                        public void onCompleted(boolean result, Object object) {
                                            if (result) {
                                                Toast.makeText(getApplicationContext(), "카카오 계정 삭제 성공", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "계정 삭제 실패", Toast.LENGTH_SHORT).show();
                                            }
                                            updateUI("");
                                            finish();
                                        }
                                    });
                                } else {
                                    FireBaseNetworkManager.getInstance(getApplicationContext()).deleteFireBaseUser(new FireBaseNetworkManager.FireBaseNetworkCallback() {
                                        @Override
                                        public void onCompleted(boolean result, Object object) {
                                            if (result) {
                                                Toast.makeText(getApplicationContext(), "계정 삭제 성공", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "계정 삭제 실패", Toast.LENGTH_SHORT).show();
                                            }
                                            updateUI("");
                                            finish();
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "유저 데이터 삭제 실패", Toast.LENGTH_SHORT).show();
                            }
                            FireBaseNetworkManager.getInstance(ProfileActivity.this).deleteUserImage(new FireBaseNetworkManager.FireBaseNetworkCallback() {
                                @Override
                                public void onCompleted(boolean result, Object object) {
                                    JWLog.e("result :"+result);

                                    if(result) {
                                        Toast.makeText(getApplicationContext(), "펫 이미지 삭제 성공", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "펫 이미지 삭제 실패", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });


                }
            }
        });
    }

    private void requestLogout() {
        CommonUtil.alertDialogShow(ProfileActivity.this, "알림", "로그아웃 하시겠습니까?", new CommonUtil.CompleteCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                if(result) {
                    if(PreferencePhoneShared.getAutoLoginType(getApplicationContext()) == GlobalConstantID.LOGIN_TYPE_KAKAO) {
                        FireBaseNetworkManager.getInstance(ProfileActivity.this).reset();
                        KakaoLoginManager.getInstance(ProfileActivity.this).requestLogout(new KakaoLoginManager.KakaoLoginManagerCallback() {
                            @Override
                            public void onCompleted(boolean result, Object object) {
                                if(result) {
                                    Toast.makeText(getApplicationContext(), "정상적으로 로그아웃 되었습니다", Toast.LENGTH_SHORT).show();

                                    PreferencePhoneShared.setAutoLoginYn(ProfileActivity.this, false);
                                    PreferencePhoneShared.setLoginYn(ProfileActivity.this, false);
                                    PreferencePhoneShared.setUserUID(ProfileActivity.this, "");
                                    PreferencePhoneShared.setLoginPassword(ProfileActivity.this, "");
                                    PreferencePhoneShared.setAutoLoginType(ProfileActivity.this, GlobalConstantID.LOGIN_TYPE_NONE);
                                } else {
                                    Toast.makeText(getApplicationContext(), "로그아웃 실패 되었습니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        FireBaseNetworkManager.getInstance(ProfileActivity.this).logoutAccount();
                        Toast.makeText(ProfileActivity.this, "정상적으로 로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    }


                    updateUIForLogout();
                    finish();
                }
            }
        });
    }

}
