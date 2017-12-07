package com.friendly.walking.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.friendly.walking.R;
import com.friendly.walking.dataSet.PetData;
import com.friendly.walking.dataSet.UserData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.Crypto;
import com.friendly.walking.util.JWLog;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class ChangePasswordActivity extends BaseActivity {

    private EditText                            mCurrentPasswordEdit;
    private EditText                            mNewPasswordEdit;
    private EditText                            mNewPasswordConfirmEdit;

    private Button                              mChangeButton;

    private OnClickListener                     mClickListener;

    @Override
    public void onCreate(Bundle bundle) {
        JWLog.e("", "@@@ ");
        super.onCreate(bundle);

        setContentView(R.layout.activity_change_password);

        mCurrentPasswordEdit = (EditText)findViewById(R.id.current_password);
        mNewPasswordEdit = (EditText)findViewById(R.id.new_password);
        mNewPasswordConfirmEdit = (EditText)findViewById(R.id.new_password_confirm);
        mChangeButton = (Button)findViewById(R.id.change_password_button);

        mNewPasswordConfirmEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    doChangePassword();
                }
                return true;
            }
        });

        mClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == mChangeButton) {
                    doChangePassword();
                }
            }
        };

        mChangeButton.setOnClickListener(mClickListener);
    }

    private void doChangePassword() {
        if(!checkEmptyFields()) {
            Toast.makeText(this, R.string.input_password, Toast.LENGTH_SHORT).show();
            return;
        }
        final String currentPassword = mCurrentPasswordEdit.getText().toString();
        final String newPassword = mNewPasswordEdit.getText().toString();
        final String newPasswordConfirm = mNewPasswordConfirmEdit.getText().toString();

        if(currentPassword.equals(newPassword)) {
            Toast.makeText(this, R.string.same_password, Toast.LENGTH_SHORT).show();
            return;
        }

        if(!newPassword.equals(newPasswordConfirm)) {
            Toast.makeText(this, R.string.password_compare_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if(!CommonUtil.isValidPassword(newPassword)) {
            Toast.makeText(this, R.string.password_error, Toast.LENGTH_SHORT).show();
            return;
        }

        setProgressBar(View.VISIBLE);
        FireBaseNetworkManager.getInstance(this).changePassword(newPassword, new FireBaseNetworkManager.FireBaseNetworkCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                setProgressBar(View.INVISIBLE);
                if(result) {
                    try {
                        String key = PreferencePhoneShared.getUserUid(ChangePasswordActivity.this);
                        String paddedKey = key.substring(0, 16);

                        String encryptedPassword = Crypto.encryptAES(CommonUtil.urlEncoding(newPassword, 0), paddedKey);

                        PreferencePhoneShared.setLoginPassword(getApplicationContext(), encryptedPassword);
                        Toast.makeText(ChangePasswordActivity.this, R.string.succeed_change_password, Toast.LENGTH_SHORT).show();
                    } catch(Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ChangePasswordActivity.this, R.string.failed_change_password, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, R.string.failed_change_password, Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    private boolean checkEmptyFields() {
        if(TextUtils.isEmpty(mCurrentPasswordEdit.getText().toString())
                || TextUtils.isEmpty(mNewPasswordEdit.getText().toString())
                || TextUtils.isEmpty(mNewPasswordConfirmEdit.getText().toString())) {

            return false;
        }
        return true;
    }

}
