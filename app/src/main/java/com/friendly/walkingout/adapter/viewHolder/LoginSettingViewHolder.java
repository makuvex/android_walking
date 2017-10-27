package com.friendly.walkingout.adapter.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.friendly.walkingout.R;
import com.friendly.walkingout.activity.LoginActivity;
import com.friendly.walkingout.adapter.baseInterface.BaseSettingViewHolderInterface;
import com.friendly.walkingout.dataSet.LoginSettingListData;
import com.friendly.walkingout.preference.PreferencePhoneShared;
import com.friendly.walkingout.util.JWLog;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

public class LoginSettingViewHolder extends BaseViewHolder implements BaseSettingViewHolderInterface {

    public ImageButton logoutButton;
    public ImageButton autoLoginButton;

    public TextView loginID;
    public boolean isAutoLogin;

    private View.OnClickListener mOnClickListener;

    public LoginSettingViewHolder(Context context, View itemView) {
        super(context, itemView);

        JWLog.e("", "");

        loginID = (TextView) itemView.findViewById(R.id.loginID);
        logoutButton = (ImageButton) itemView.findViewById(R.id.login_out_action);
        autoLoginButton = (ImageButton) itemView.findViewById(R.id.autoLogin);

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.login_out_action) {

                    if(PreferencePhoneShared.getLoginYn(mContext)) {
                        JWLog.e("","로그아웃");
                    } else {
                        JWLog.e("","로그인");
                        Intent intent = new Intent(mContext, LoginActivity.class);


                        mContext.startActivity(intent);
                   }
                } else if(v.getId() == R.id.autoLogin) {
                    JWLog.e("","autoLogin");
                }
            }
        };

        logoutButton.setOnClickListener(mOnClickListener);
        autoLoginButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public LoginSettingViewHolder setLayout(Context context) {
        return new LoginSettingViewHolder(context, mView);
    }

    @Override
    public void setDataSet(Object object) {
        if(object instanceof LoginSettingListData) {
            LoginSettingListData data = (LoginSettingListData)object;

            this.loginID.setText(data.getLoginID());
            this.isAutoLogin = data.getAutoLogin();
            this.itemView.setTag(data);
        }
    }
}
