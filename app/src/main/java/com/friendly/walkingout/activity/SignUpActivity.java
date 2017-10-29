package com.friendly.walkingout.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.friendly.walkingout.GlobalConstantID;
import com.friendly.walkingout.R;
import com.friendly.walkingout.firabaseManager.FireBaseNetworkManager;
import com.friendly.walkingout.util.JWLog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class SignUpActivity extends BaseActivity {

    private EditText                            mEmailText;

    private EditText                            mPasswordText;
    private EditText                            mConfirmPasswordText;
    private Button                              mCheckDuplicationButton;
    private Button                              mNextButton;

    private String                              mCheckCompletedEmail;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_signup);

        mEmailText = (EditText)findViewById(R.id.email_id);
        mPasswordText = (EditText)findViewById(R.id.password);
        mConfirmPasswordText = (EditText)findViewById(R.id.confirm_password);
        mCheckDuplicationButton = (Button) findViewById(R.id.check_duplication_button);
        mNextButton = (Button)findViewById(R.id.next_button);
    }

    public void onClickCallback(View v) {
        JWLog.e("","next_button");

        if(v == mNextButton) {
            if(mCheckCompletedEmail == null || TextUtils.isEmpty(mCheckCompletedEmail) || !mCheckCompletedEmail.equals(mEmailText.getText().toString())) {
                Toast.makeText(this, R.string.require_check_email, Toast.LENGTH_SHORT).show();
                return ;
            }

            if(isValidEmail(mEmailText.getText().toString())) {
                if(mPasswordText.getText().toString().equals(mConfirmPasswordText.getText().toString())) {
                    if(isValidPasswd(mPasswordText.getText().toString())) {


                        Intent intent = new Intent(this, SignUpPetActivity.class);
                        intent.putExtra(GlobalConstantID.SIGN_UP_EMAIL, mEmailText.getText().toString());
                        intent.putExtra(GlobalConstantID.SIGN_UP_PASSWORD, mPasswordText.getText().toString());

                        startActivity(intent);
                    } else {
                        Toast.makeText(this, R.string.password_error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, R.string.password_compare_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.email_error, Toast.LENGTH_SHORT).show();
            }
        } else if(v == mCheckDuplicationButton) {

           FireBaseNetworkManager.getInstance(this).findUserEmail(mEmailText.getText().toString(), new FireBaseNetworkManager.FireBaseNetworkCallback() {
               @Override
               public void onCompleted(boolean result) {
                   if(result) {

                   } else {

                   }
               }
           });

            mCheckCompletedEmail = mEmailText.getText().toString();
        }
    }

    private boolean isValidPasswd(String target) {
        Pattern p = Pattern.compile("(^.*(?=.{6,100})(?=.*[0–9])(?=.*[a-zA-Z]).*$)");

        return true;
//        Matcher m = p.matcher(target);
//        if (m.find() && !target.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")){
//            return true;
//        }else{
//            return false;
//        }
    }

    private boolean isValidEmail(String target) {
        if (target == null || TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}
