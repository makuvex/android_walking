package com.friendly.walking.adapter.viewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.friendly.walking.util.JWToast;

import com.friendly.walking.GlobalConstantID;
import com.friendly.walking.R;
import com.friendly.walking.activity.LoginActivity;
import com.friendly.walking.activity.ProfileActivity;
import com.friendly.walking.adapter.baseInterface.BaseSettingViewHolderInterface;
import com.friendly.walking.dataSet.LoginSettingListData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.JWLog;

import org.w3c.dom.Text;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

public class LoginSettingViewHolder extends BaseViewHolder implements BaseSettingViewHolderInterface {

    public ImageButton logoutButton;
    public ImageButton autoLoginButton;
    public ImageView loginTypeImage;

    public TextView loginID;
    public TextView nickName;
    public TextView walking_coin;
    public boolean isAutoLogin;

    private View.OnClickListener mOnClickListener;

    public LoginSettingViewHolder(final Activity activity, View itemView) {
        super(activity, itemView);

        JWLog.e("", "");

        loginID = (TextView) itemView.findViewById(R.id.loginID);
        nickName = (TextView) itemView.findViewById(R.id.nickname);
        walking_coin = (TextView) itemView.findViewById(R.id.walking_coin);
        logoutButton = (ImageButton) itemView.findViewById(R.id.login_out_action);
        autoLoginButton = (ImageButton) itemView.findViewById(R.id.autoLogin);
        loginTypeImage = (ImageView) itemView.findViewById(R.id.loginTypeImage);

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.login_out_action)
                    if (PreferencePhoneShared.getLoginYn(mActivity)) {
                        Intent intent = new Intent(mActivity, ProfileActivity.class);
                        intent.putExtra("email", loginID.getText());
                        // 펫네임 젠더
                        mActivity.startActivity(intent);
                    } else {
                        JWLog.e("", "로그인");
                        Intent intent = new Intent(mActivity, LoginActivity.class);
                        mActivity.startActivity(intent);
                    }
                else if(v.getId() == R.id.autoLogin) {
                    JWLog.e("","autoLogin");
                }
            }
        };

        logoutButton.setOnClickListener(mOnClickListener);
        autoLoginButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public LoginSettingViewHolder setLayout(Activity activity) {
        return new LoginSettingViewHolder(activity, mView);
    }

    @Override
    public void setDataSet(Object object) {
        if(object instanceof LoginSettingListData) {
            LoginSettingListData data = (LoginSettingListData)object;

            this.loginID.setText(data.getLoginID());
            this.nickName.setText(data.getNickName());
            this.isAutoLogin = data.getAutoLogin();
            this.walking_coin.setText(""+data.getWalkingCoin()+"개");
            this.itemView.setTag(data);

            loginTypeImage.setVisibility(View.VISIBLE);
            if(PreferencePhoneShared.getAutoLoginType(mActivity) == GlobalConstantID.LOGIN_TYPE_KAKAO) {
                loginTypeImage.setImageResource(R.drawable.k);
            } else if(PreferencePhoneShared.getAutoLoginType(mActivity) == GlobalConstantID.LOGIN_TYPE_FACEBOOK) {
                loginTypeImage.setImageResource(R.drawable.f);
            } else if(PreferencePhoneShared.getAutoLoginType(mActivity) == GlobalConstantID.LOGIN_TYPE_GOOGLE) {
                loginTypeImage.setImageResource(R.drawable.g);
            } else if(PreferencePhoneShared.getAutoLoginType(mActivity) == GlobalConstantID.LOGIN_TYPE_EMAIL) {
                loginTypeImage.setImageResource(R.drawable.e);
            } else {
                loginTypeImage.setVisibility(View.GONE);
            }
        }
    }
}
