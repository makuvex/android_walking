package com.friendly.walking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.friendly.walking.R;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.UserData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.main.MainActivity;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.Crypto;
import com.friendly.walking.util.JWLog;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.net.URL;

/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText                            mEmailText;
    private EditText                            mPasswordText;
    private CheckBox                            mAutoLoginCheckBox;

    @Override
    public void onCreate(Bundle bundle) {
        JWLog.e("", "@@@ ");
        super.onCreate(bundle);

        setContentView(R.layout.activity_login);
        mEmailText = (EditText)findViewById(R.id.email_id);
        mPasswordText = (EditText)findViewById(R.id.password);
        mAutoLoginCheckBox = (CheckBox)findViewById(R.id.autologin_check);
        findViewById(R.id.find_id).setOnClickListener(this);
        findViewById(R.id.find_password).setOnClickListener(this);
        findViewById(R.id.sign_up).setOnClickListener(this);

        mAutoLoginCheckBox.setChecked(true);

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

                            PreferencePhoneShared.setUserUID(getApplicationContext(), key);
                            PreferencePhoneShared.setLoginID(getApplicationContext(), encryptedEmail);
                            PreferencePhoneShared.setLoginPassword(getApplicationContext(), encryptedPassword);
                            PreferencePhoneShared.setAutoLoginYn(getApplicationContext(), mAutoLoginCheckBox.isChecked());

                            /////////////////////////////
                            String uid = PreferencePhoneShared.getUserUid(getApplicationContext());

                            JWLog.e("","uid :" + uid);
                            String decEmail = CommonUtil.urlDecoding(Crypto.decryptAES(PreferencePhoneShared.getLoginID(getApplicationContext()), key));


                            /////////////////////////////
                            Intent intent = new Intent(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI);
                            intent.putExtra("email", email);
                            intent.putExtra("autoLogin", mAutoLoginCheckBox.isChecked());

                            JWBroadCast.sendBroadcast(getApplicationContext(), intent);
                            finish();
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else if(v.getId() == R.id.autologin_check) {

        } else if(v.getId() == R.id.google_login) {

        } else if(v.getId() == R.id.facebook_login) {

        }
    }

    @Override
    public void onClick(View view) {
        JWLog.e("","");
        if(view.getId() == R.id.sign_up) {
            startActivity(new Intent(this, SignUpActivity.class));
        }
    }
}
