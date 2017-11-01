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
import com.friendly.walking.util.JWLog;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

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
                public void onCompleted(boolean result, Task<AuthResult> task) {
                    setProgressBar(View.INVISIBLE);

                    if(result) {
                        Intent intent = new Intent(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI);
                        intent.putExtra("email", email);

                        JWBroadCast.sendBroadcast(getApplicationContext(), intent);
                        finish();
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
