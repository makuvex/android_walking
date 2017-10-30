package com.friendly.walking.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.friendly.walking.GlobalConstantID;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.util.JWLog;
import com.friendly.walking.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.regex.Pattern;

/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class SignUpActivity extends BaseActivity implements View.OnFocusChangeListener {

    private EditText                            mEmailText;

    private EditText                            mPasswordText;
    private EditText                            mConfirmPasswordText;
    private EditText                            mInputAddressText;
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
        mInputAddressText = (EditText)findViewById(R.id.address);

        mCheckDuplicationButton = (Button) findViewById(R.id.check_duplication_button);
        mNextButton = (Button)findViewById(R.id.next_button);
        mNextButton.setEnabled(false);

        mInputAddressText.setOnFocusChangeListener(this);
//        BaseActivity.setProgressBar(View.VISIBLE);
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
            setProgressBar(View.VISIBLE);

            String email = mEmailText.getText().toString();
            FireBaseNetworkManager.getInstance(this).findUserEmail(email, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                @Override
                public void onCompleted(boolean result, Task<AuthResult> task) {
                    setProgressBar(View.INVISIBLE);

                    if(result) {
                        Toast.makeText(getApplicationContext(), R.string.unavailable_id, Toast.LENGTH_SHORT).show();
                        mNextButton.setEnabled(false);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.available_id, Toast.LENGTH_SHORT).show();
                        mNextButton.setEnabled(true);
                    }
               }
           });

            mCheckCompletedEmail = mEmailText.getText().toString();
        } else if(v.getId() == R.id.address) {

            Intent intent = new Intent(this, GoogleMapActivity.class);
            intent.putExtra(GlobalConstantID.HOME_ADDRESS, "능동 1065-3");

            startActivity(intent);

//            Uri gmmIntentUri = Uri.parse("geo:0,0?q=능동 1065-3");
//            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//            mapIntent.setPackage("com.google.android.apps.maps");
//            startActivity(mapIntent);

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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus && v == mInputAddressText) {
            Intent intent = new Intent(this, GoogleMapActivity.class);
            if(!TextUtils.isEmpty(mInputAddressText.getText().toString())) {
                intent.putExtra(GlobalConstantID.HOME_ADDRESS, mInputAddressText.getText().toString());
            }
            startActivity(intent);
        }
    }
}
