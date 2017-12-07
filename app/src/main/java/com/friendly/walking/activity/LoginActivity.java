package com.friendly.walking.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.pm.PackageInstaller;

import com.facebook.FacebookSdk;
import com.facebook.internal.CallbackManagerImpl;

import com.friendly.walking.ApplicationPool;
import com.friendly.walking.GlobalConstantID;
import com.friendly.walking.R;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.UserData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.network.KakaoLoginManager;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.Crypto;
import com.friendly.walking.util.JWLog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.friendly.walking.firabaseManager.FireBaseNetworkManager.RC_GOOGLE_SIGN_IN;

/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    public static final String                          KEY_USER_DATA = "key_user_data";

    private EditText                                    mEmailText;
    private EditText                                    mPasswordText;
    private CheckBox                                    mAutoLoginCheckBox;

    private SignInButton                                mSignInGoogleButton;
    private com.facebook.login.widget.LoginButton       mSignInFacebookButton;
    private com.kakao.usermgmt.LoginButton              mSignInKakaoButton;

    private FirebaseUser                                mFirebaseUser;

    @Override
    public void onCreate(Bundle bundle) {
        JWLog.e("", "@@@ ");
        super.onCreate(bundle);

        ApplicationPool.setCurrentActivity(this);

        setContentView(R.layout.activity_login);
        mEmailText = (EditText)findViewById(R.id.email_id);
        mPasswordText = (EditText)findViewById(R.id.password);
        mAutoLoginCheckBox = (CheckBox)findViewById(R.id.autologin_check);

        mSignInGoogleButton = (SignInButton)findViewById(R.id.google_login);
        mSignInFacebookButton = (com.facebook.login.widget.LoginButton)findViewById(R.id.facebook_login);
        mSignInKakaoButton = (com.kakao.usermgmt.LoginButton)findViewById(R.id.kakao_login);

        findViewById(R.id.find_id).setOnClickListener(this);
        findViewById(R.id.find_password).setOnClickListener(this);
        findViewById(R.id.sign_up).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);

        mSignInGoogleButton.setOnClickListener(this);
        mAutoLoginCheckBox.setChecked(true);

        mSignInFacebookButton.setOnClickListener(this);
        //mSignInKakaoButton.setOnClickListener(this);

        KakaoLoginManager.getInstance(this);

//        KakaoLoginManager.getInstance(this).requestMe(new KakaoLoginManager.KakaoLoginManagerCallback() {
//            @Override
//            public void onCompleted(boolean result, Object object) {
//
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KakaoLoginManager.getInstance(this).terminate();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        JWLog.e(""+intent.getAction());
    }

    @Override
    public void onClick(View view) {
        JWLog.e("","");
        if (view.getId() == R.id.close_button) {
            finish();
        } else if(view.getId() == R.id.sign_up) {
            startActivity(new Intent(this, SignUpActivity.class));
        } else if(view == mSignInGoogleButton) {
            PreferencePhoneShared.setAutoLoginYn(getApplicationContext(), true);
            PreferencePhoneShared.setAutoLoginType(getApplication(), GlobalConstantID.LOGIN_TYPE_GOOGLE);
            FireBaseNetworkManager.getInstance(this).googleSignIn(this);
        } else if(view == mSignInFacebookButton) {
            setProgressBar(View.VISIBLE);

            JWLog.e("","currentUser : "+FireBaseNetworkManager.getInstance(this).getCurrentUser());
            if(FireBaseNetworkManager.getInstance(this).getCurrentUser() == null) {
                FireBaseNetworkManager.getInstance(this).facebookSignIn(this, mSignInFacebookButton, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                    @Override
                    public void onCompleted(boolean result, Object object) {
                        setProgressBar(View.INVISIBLE);

                        if (result) {
                            if (object instanceof Task<?>) {
                                try {
                                    final Task<AuthResult> task = (Task<AuthResult>) object;

                                    JWLog.e("", "email :" + task.getResult().getUser().getEmail() + ", uid :" + task.getResult().getUser().getUid());
                                    JWLog.e("", "" + task.getResult().getUser().getDisplayName() + ", " + task.getResult().getUser().getPhoneNumber() + ", " + task.getResult().getUser().getDisplayName());

                                    PreferencePhoneShared.setAutoLoginYn(getApplicationContext(), true);
                                    PreferencePhoneShared.setAutoLoginType(getApplication(), GlobalConstantID.LOGIN_TYPE_FACEBOOK);

//                                    String key = task.getResult().getUser().getUid().substring(0, 16);
                                    String id = task.getResult().getUser().getEmail();
                                    if(TextUtils.isEmpty(id)) {
                                        id = task.getResult().getUser().getDisplayName();
                                    }
                                    final String displayName = id;

                                    JWLog.e("displayName :"+displayName+", email :"+task.getResult().getUser().getEmail());

                                    FireBaseNetworkManager.getInstance(LoginActivity.this).findUserEmail(displayName, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                                        @Override
                                        public void onCompleted(boolean result, Object object) {
                                            setProgressBar(View.INVISIBLE);

                                            if(result) {
                                                Toast.makeText(getApplicationContext(), "아이디가 디비에 있음", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(JWBroadCast.BROAD_CAST_FACEBOOK_LOGIN);
                                                intent.putExtra("email", displayName);
                                                JWBroadCast.sendBroadcast(LoginActivity.this, intent);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "아이디가 디비에 없음", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(LoginActivity.this, SignUpPetActivity.class);
                                                intent.putExtra(GlobalConstantID.SIGN_UP_TYPE, GlobalConstantID.LOGIN_TYPE_FACEBOOK);
                                                //intent.putExtra(GlobalConstantID.SIGN_UP_PASSWORD, mPasswordText.getText().toString());

                                                ApplicationPool pool = (ApplicationPool)getApplicationContext();
                                                pool.putExtra(KEY_USER_DATA, intent, getUserData(displayName, task.getResult().getUser().getUid(), "facebook"));

                                                startActivity(intent);
                                            }
                                        }
                                    });

                                    updateUI(id);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            } else {
                Toast.makeText(LoginActivity.this, "로그인 안됨" , Toast.LENGTH_SHORT).show();
            }
        } else if(view == mSignInKakaoButton) {
            //kakaoRequestMe();
            PreferencePhoneShared.setAutoLoginYn(getApplicationContext(), true);
            PreferencePhoneShared.setAutoLoginType(getApplication(), GlobalConstantID.LOGIN_TYPE_KAKAO);
        } else if(view.getId() == R.id.login_button) {
            setProgressBar(View.VISIBLE);

            final String email = mEmailText.getText().toString().trim();
            if(TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "이메일을 입력해 주세요", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(mPasswordText.getText().toString())) {
                Toast.makeText(getApplicationContext(), "비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
            } else {
                FireBaseNetworkManager.getInstance(this).loginEmailWithPassword(email, mPasswordText.getText().toString(), new FireBaseNetworkManager.FireBaseNetworkCallback() {
                    @Override
                    public void onCompleted(boolean result, Object object) {
                        setProgressBar(View.INVISIBLE);

                        Task<AuthResult> task = (Task<AuthResult>) object;

                        if (result) {
                            JWLog.e("", "email :" + email + ", password : " + mPasswordText.getText().toString() + ", autoLogin :" + mAutoLoginCheckBox.isChecked());
                            JWLog.e("", "task uid :" + task.getResult().getUser().getUid());
                            PreferencePhoneShared.setLoginYn(getApplicationContext(), true);
                            try {
//                            String key = task.getResult().getUser().getUid().substring(0, 16);
//                            String encryptedEmail = Crypto.encryptAES(CommonUtil.urlEncoding(email, 0), key);
//                            String encryptedPassword = Crypto.encryptAES(CommonUtil.urlEncoding(mPasswordText.getText().toString(), 0), key);
//
//                            JWLog.e("","encEmail : ["+encryptedEmail+"]"+", encPassword : ["+encryptedPassword +"]");
//
//                            PreferencePhoneShared.setAutoLoginType(getApplicationContext(), GlobalConstantID.LOGIN_TYPE_EMAIL);
//                            PreferencePhoneShared.setUserUID(getApplicationContext(), key);
//                            PreferencePhoneShared.setLoginID(getApplicationContext(), encryptedEmail);
//                            PreferencePhoneShared.setLoginPassword(getApplicationContext(), encryptedPassword);
//                            PreferencePhoneShared.setAutoLoginYn(getApplicationContext(), mAutoLoginCheckBox.isChecked());

                                updateUI(email);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        JWLog.e("", "onActivityResult requestCode :"+requestCode+", resultCode :"+resultCode);
        if(resultCode != RESULT_OK) {
            return;
        }
        if ( requestCode == RC_GOOGLE_SIGN_IN ) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if ( result.isSuccess() ) {
                JWLog.e("", "Google Login succeed." + result.getStatus());
                GoogleSignInAccount account = result.getSignInAccount();

                JWLog.e("",""+account.getId()+", "+account.getEmail()+", "+account.getDisplayName()+", "+account.getServerAuthCode());

                setProgressBar(View.VISIBLE);
                FireBaseNetworkManager.getInstance(this).firebaseAuthWithGoogle(account,new  FireBaseNetworkManager.FireBaseNetworkCallback() {
                    @Override
                    public void onCompleted(boolean result, Object object) {
                        setProgressBar(View.INVISIBLE);
                        if(object instanceof FirebaseUser) {
                            try {
                                final FirebaseUser user = (FirebaseUser) object;
                                mFirebaseUser = user;
                                setProgressBar(View.VISIBLE);

                                FireBaseNetworkManager.getInstance(LoginActivity.this).findUserEmail(user.getEmail(), new FireBaseNetworkManager.FireBaseNetworkCallback() {
                                    @Override
                                    public void onCompleted(boolean result, Object object) {
                                        setProgressBar(View.INVISIBLE);

                                        if(result) {
                                            Toast.makeText(getApplicationContext(), "아이디가 디비에 있음", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(JWBroadCast.BROAD_CAST_GOOGLE_LOGIN);
                                            intent.putExtra("email", user.getEmail());
                                            JWBroadCast.sendBroadcast(LoginActivity.this, intent);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "아이디가 디비에 없음", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(LoginActivity.this, SignUpPetActivity.class);
                                            intent.putExtra(GlobalConstantID.SIGN_UP_TYPE, GlobalConstantID.LOGIN_TYPE_GOOGLE);
                                            //intent.putExtra(GlobalConstantID.SIGN_UP_PASSWORD, mPasswordText.getText().toString());

                                            ApplicationPool pool = (ApplicationPool)getApplicationContext();
                                            String email = mFirebaseUser.getEmail();
                                            if(TextUtils.isEmpty(email)) {
                                                email = mFirebaseUser.getDisplayName();
                                            }
                                            pool.putExtra(KEY_USER_DATA, intent, getUserData(email, mFirebaseUser.getUid(), "google"));

                                            startActivity(intent);
                                        }
                                    }
                                });

                                updateUI(user.getEmail());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else {
                JWLog.e("", "Google Login Failed." + result.getStatus());
            }
        } else if(FacebookSdk.isFacebookRequestCode(requestCode)) {
            if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
                try {
                    JWLog.e("", "facebook onActivityResult");
                    FireBaseNetworkManager.getInstance(this).getFacebookCallback().onActivityResult(requestCode, resultCode, data);

                    FirebaseUser user = FireBaseNetworkManager.getInstance(this).getCurrentUser();

                    JWLog.e("user :"+user);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (KakaoLoginManager.getInstance(this).handleKakaoActivityResult(requestCode, resultCode, data)) {
            JWLog.e("","");
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void updateUI(String email) {
        JWLog.e("","");
        Intent intent = new Intent(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI);
        intent.putExtra("email", email);
        intent.putExtra("autoLogin", mAutoLoginCheckBox.isChecked());

        JWBroadCast.sendBroadcast(getApplicationContext(), intent);
        finish();
    }

    private UserData getUserData(String email, String uid, String joinBy) {
        UserData data = new UserData();

        data.mem_email = email;
        data.mem_auto_login = true;
        data.mem_notification_yn = true;
        data.mem_location_yn = false;

        data.mem_address.put("address", "");
        data.mem_address.put("lat", "");
        data.mem_address.put("lot", "");

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy.MM.dd.HH:mm:ss", Locale.KOREA );
        String dateTime = formatter.format(date);
        data.mem_register_datetime = dateTime;

        data.mem_last_login_datetime = "";
        data.mem_auto_stroll_mode = false;
        data.uid = uid;
        data.joinBy = joinBy;

        JWLog.e("","@@@ userData : "+data);

        return data;
    }

}
