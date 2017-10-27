package com.friendly.walkingout.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.friendly.walkingout.R;
import com.friendly.walkingout.util.JWLog;

/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class SignUpActivity extends Activity {

    private EditText                            mEmailText;
    private EditText                            mPasswordText;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_signup);

        mEmailText = (EditText)findViewById(R.id.email_id);
        mPasswordText = (EditText)findViewById(R.id.password);
    }

    public void onClickCallback(View v) {
        JWLog.e("","v : "+v.getId());

        if(v.getId() == R.id.next_button) {

            JWLog.e("","next_button");
            startActivity(new Intent(this, SignUpPetActivity.class));
        }
    }

}
