package com.friendly.walking.activity;

import android.app.Activity;


/**
 * Created by jungjiwon on 2017. 11. 7..
 */

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.friendly.walking.ApplicationPool;
import com.friendly.walking.GlobalConstantID;
import com.friendly.walking.R;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.UserData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.main.MainActivity;
import com.friendly.walking.network.KakaoLoginManager;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.Crypto;
import com.friendly.walking.util.JWLog;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.usermgmt.response.model.UserProfile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.friendly.walking.activity.SignUpActivity.KEY_USER_DATA;

public class KakaoSignupActivity extends BaseActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        JWLog.e("","");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_kakao_signup);
        KakaoLoginManager.getInstance(this).requestMe(new KakaoLoginManager.KakaoLoginManagerCallback() {
            @Override
            public void onCompleted(boolean result, final Object userProfile) {
                JWLog.e("result : "+result);
                if(result) {
                    JWLog.e(""+userProfile);
                    try {
                        UserProfile user = (UserProfile) userProfile;
                        String key = "" + user.getId();
                        String id = user.getEmail();

                        if (TextUtils.isEmpty(id)) {
                            id = user.getNickname();
                        }

                        char fill = 'e';
                        String paddedKey = new String(new char[16 - key.length()]).replace('\0', fill) + key;

                        JWLog.e("@@@ key :"+key+", paddedKey:"+paddedKey);
                        String encryptedId = Crypto.encryptAES(CommonUtil.urlEncoding(id, 0), paddedKey);

                        PreferencePhoneShared.setLoginYn(getApplicationContext(), true);
                        PreferencePhoneShared.setAutoLoginType(getApplicationContext(), GlobalConstantID.LOGIN_TYPE_KAKAO);
                        PreferencePhoneShared.setUserUID(getApplicationContext(), paddedKey);
                        PreferencePhoneShared.setLoginID(getApplicationContext(), encryptedId);
                        PreferencePhoneShared.setAutoLoginYn(getApplicationContext(), true);

                        Intent i = new Intent(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI);
                        i.putExtra("email", id);
                        JWBroadCast.sendBroadcast(KakaoSignupActivity.this, i);

                        final String resultId = id;
                        FireBaseNetworkManager.getInstance(KakaoSignupActivity.this).findUserEmail(id, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                            @Override
                            public void onCompleted(boolean result, Object object) {
                                if(!result) {
                                    startSignUpPet((UserProfile)userProfile);
                                } else {
                                    Toast.makeText(KakaoSignupActivity.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                                    updateUI(resultId);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(KakaoSignupActivity.this, "카톡 로그인 오류 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    JWLog.e("@@@ requestMe failed");
                }
            }
        });
    }

    private void startSignUpPet(UserProfile userProfile) {
        UserData userData = getUserData(userProfile);

        Intent intent = new Intent(this, SignUpPetActivity.class);
        intent.putExtra(GlobalConstantID.SIGN_UP_TYPE, GlobalConstantID.LOGIN_TYPE_KAKAO);

        ApplicationPool pool = (ApplicationPool)getApplicationContext();
        pool.putExtra(KEY_USER_DATA, intent, userData);

        startActivity(intent);
        finish();
    }

    private UserData getUserData(UserProfile userProfile) {
        UserData data = new UserData();

        data.uid = PreferencePhoneShared.getUserUid(getApplicationContext());
        data.mem_email = TextUtils.isEmpty(userProfile.getEmail()) ? userProfile.getNickname() : userProfile.getEmail();
        data.mem_auto_login = true;
        data.mem_notification_yn = true;
        data.mem_location_yn = false;

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy.MM.dd.HH:mm:ss", Locale.KOREA );
        String dateTime = formatter.format(date);
        data.mem_register_datetime = dateTime;
        data.mem_last_login_datetime = dateTime;

        data.mem_auto_stroll_mode = false;

        JWLog.e("","@@@ userData : "+data);
        data.joinBy = "kakao";

        return data;
    }

    public void updateUI(String email) {
        JWLog.e("","");
        Intent intent = new Intent(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI);
        intent.putExtra("email", email);

        JWBroadCast.sendBroadcast(getApplicationContext(), intent);

        KakaoLoginManager.getInstance(this).redirectMainActivity();
    }
}
