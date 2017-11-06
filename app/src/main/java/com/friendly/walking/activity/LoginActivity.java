package com.friendly.walking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.widget.LoginButton;
import com.friendly.walking.GlobalConstantID;
import com.friendly.walking.R;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
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


import static com.friendly.walking.firabaseManager.FireBaseNetworkManager.RC_GOOGLE_SIGN_IN;

/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText                            mEmailText;
    private EditText                            mPasswordText;
    private CheckBox                            mAutoLoginCheckBox;

    private SignInButton                        mSignInGoogleButton;
    private LoginButton                         mSignInFacebookButton;

    @Override
    public void onCreate(Bundle bundle) {
        JWLog.e("", "@@@ ");
        super.onCreate(bundle);


        setContentView(R.layout.activity_login);
        mEmailText = (EditText)findViewById(R.id.email_id);
        mPasswordText = (EditText)findViewById(R.id.password);
        mAutoLoginCheckBox = (CheckBox)findViewById(R.id.autologin_check);

        mSignInGoogleButton = (SignInButton)findViewById(R.id.google_login);
        mSignInFacebookButton = (LoginButton)findViewById(R.id.facebook_login);

        findViewById(R.id.find_id).setOnClickListener(this);
        findViewById(R.id.find_password).setOnClickListener(this);
        findViewById(R.id.sign_up).setOnClickListener(this);
        mSignInGoogleButton.setOnClickListener(this);
        mAutoLoginCheckBox.setChecked(true);

        mSignInFacebookButton.setOnClickListener(this);

    }

    public void onClickCallback(View v) {
        JWLog.e("","v : "+v.getId());

        if(v.getId() == R.id.close_button) {

            finish();
        } else if(v.getId() == R.id.login_button) {
            setProgressBar(View.VISIBLE);

            final String email = mEmailText.getText().toString().trim();
            FireBaseNetworkManager.getInstance(this).loginEmailWithPassword(email, mPasswordText.getText().toString(), new FireBaseNetworkManager.FireBaseNetworkCallback() {
                @Override
                public void onCompleted(boolean result, Object object) {
                    setProgressBar(View.INVISIBLE);

                    Task<AuthResult> task = (Task<AuthResult>)object;

                    if(result) {
                        JWLog.e("","email :"+email+", password : "+mPasswordText.getText().toString()+", autoLogin :"+mAutoLoginCheckBox.isChecked());
                        JWLog.e("","task uid :"+task.getResult().getUser().getUid());
                        PreferencePhoneShared.setLoginYn(getApplicationContext(), true);
                        try {
                            String key = task.getResult().getUser().getUid().substring(0, 16);
                            String encryptedEmail = Crypto.encryptAES(CommonUtil.urlEncoding(email, 0), key);
                            String encryptedPassword = Crypto.encryptAES(CommonUtil.urlEncoding(mPasswordText.getText().toString(), 0), key);

                            JWLog.e("","encEmail : ["+encryptedEmail+"]"+", encPassword : ["+encryptedPassword +"]");

                            PreferencePhoneShared.setAutoLoginType(getApplicationContext(), GlobalConstantID.LOGIN_TYPE_EMAIL);
                            PreferencePhoneShared.setUserUID(getApplicationContext(), key);
                            PreferencePhoneShared.setLoginID(getApplicationContext(), encryptedEmail);
                            PreferencePhoneShared.setLoginPassword(getApplicationContext(), encryptedPassword);
                            PreferencePhoneShared.setAutoLoginYn(getApplicationContext(), mAutoLoginCheckBox.isChecked());

                            updateUI(email);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else if(v.getId() == R.id.autologin_check) {

        }
    }

    @Override
    public void onClick(View view) {
        JWLog.e("","");
        if(view.getId() == R.id.sign_up) {
            startActivity(new Intent(this, SignUpActivity.class));
        } else if(view == mSignInGoogleButton) {
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
                                    Task<AuthResult> task = (Task<AuthResult>) object;

                                    JWLog.e("", "email :" + task.getResult().getUser().getEmail() + ", uid :" + task.getResult().getUser().getUid());
                                    JWLog.e("", "" + task.getResult().getUser().getDisplayName() + ", " + task.getResult().getUser().getPhoneNumber() + ", " + task.getResult().getUser().getDisplayName());

                                    String key = task.getResult().getUser().getUid().substring(0, 16);
                                    String id = task.getResult().getUser().getEmail();
                                    if (TextUtils.isEmpty(id)) {
                                        id = task.getResult().getUser().getDisplayName();
                                    }
                                    String encryptedId = Crypto.encryptAES(CommonUtil.urlEncoding(id, 0), key);

                                    PreferencePhoneShared.setLoginYn(getApplicationContext(), true);
                                    PreferencePhoneShared.setAutoLoginType(getApplicationContext(), GlobalConstantID.LOGIN_TYPE_FACEBOOK);
                                    PreferencePhoneShared.setUserUID(getApplicationContext(), key);
                                    PreferencePhoneShared.setLoginID(getApplicationContext(), encryptedId);
                                    PreferencePhoneShared.setAutoLoginYn(getApplicationContext(), mAutoLoginCheckBox.isChecked());

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
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        JWLog.e("", "requestCode :"+requestCode+", resultCode :"+resultCode);
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
                                FirebaseUser user = (FirebaseUser) object;

                                String key = user.getUid().substring(0, 16);
                                String encryptedEmail = Crypto.encryptAES(CommonUtil.urlEncoding(user.getEmail(), 0), key);

                                PreferencePhoneShared.setLoginYn(getApplicationContext(), true);
                                PreferencePhoneShared.setAutoLoginType(getApplicationContext(), GlobalConstantID.LOGIN_TYPE_GOOGLE);
                                PreferencePhoneShared.setUserUID(getApplicationContext(), key);
                                PreferencePhoneShared.setLoginID(getApplicationContext(), encryptedEmail);
                                PreferencePhoneShared.setAutoLoginYn(getApplicationContext(), mAutoLoginCheckBox.isChecked());

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
                    if(user == null) {
                        return ;
                    }
                    String key = user.getUid();
                    String encryptedEmail = Crypto.encryptAES(CommonUtil.urlEncoding(user.getDisplayName(), 0), key);

                    PreferencePhoneShared.setLoginYn(getApplicationContext(), true);
                    PreferencePhoneShared.setAutoLoginType(getApplicationContext(), GlobalConstantID.LOGIN_TYPE_FACEBOOK);
                    PreferencePhoneShared.setAutoLoginYn(getApplicationContext(), true);
                    PreferencePhoneShared.setLoginID(getApplicationContext(), encryptedEmail);

                    updateUI(user.getDisplayName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateUI(String email) {
        Intent intent = new Intent(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI);
        intent.putExtra("email", email);
        intent.putExtra("autoLogin", mAutoLoginCheckBox.isChecked());

        JWBroadCast.sendBroadcast(getApplicationContext(), intent);
        finish();
    }

}
