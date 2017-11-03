package com.friendly.walking.adapter.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.friendly.walking.R;
import com.friendly.walking.activity.LoginActivity;
import com.friendly.walking.activity.ProfileActivity;
import com.friendly.walking.adapter.baseInterface.BaseSettingViewHolderInterface;
import com.friendly.walking.dataSet.LoginSettingListData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.JWLog;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

public class LoginSettingViewHolder extends BaseViewHolder implements BaseSettingViewHolderInterface {

    public ImageButton logoutButton;
    public ImageButton autoLoginButton;

    public TextView loginID;
    public boolean isAutoLogin;

    private View.OnClickListener mOnClickListener;

    public LoginSettingViewHolder(final Context context, View itemView) {
        super(context, itemView);

        JWLog.e("", "");

        loginID = (TextView) itemView.findViewById(R.id.loginID);
        logoutButton = (ImageButton) itemView.findViewById(R.id.login_out_action);
        autoLoginButton = (ImageButton) itemView.findViewById(R.id.autoLogin);

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.login_out_action)
                    if (PreferencePhoneShared.getLoginYn(mContext)) {
                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        intent.putExtra("email", loginID.getText());
//                        intent.putExtra("petName", loginID.getText());
//                        intent.putExtra("gender", loginID.getText());

                        // 펫네임 젠더
                        mContext.startActivity(intent);
                    } else {
                        JWLog.e("", "로그인");
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        mContext.startActivity(intent);
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
