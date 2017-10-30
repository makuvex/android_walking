package com.friendly.walking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.friendly.walking.R;
import com.friendly.walking.util.JWLog;

/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText                            mEmailText;
    private EditText                            mPasswordText;
    private CheckBox                            mAutoLoginCheckBox;

    @Override
    public void onCreate(Bundle bundle) {
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
