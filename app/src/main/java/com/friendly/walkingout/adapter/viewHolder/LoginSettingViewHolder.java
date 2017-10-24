package com.friendly.walkingout.adapter.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.friendly.walkingout.R;
import com.friendly.walkingout.adapter.BaseSettingDataSetInterface;
import com.friendly.walkingout.adapter.BaseSettingViewHolderInterface;
import com.friendly.walkingout.dataSet.LoginSettingListData;
import com.friendly.walkingout.dataSet.SettingListData;
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

    public LoginSettingViewHolder(View itemView) {
        super(itemView);
        JWLog.e("", "");

        loginID = (TextView) itemView.findViewById(R.id.loginID);
        logoutButton = (ImageButton) itemView.findViewById(R.id.logout);
        autoLoginButton = (ImageButton) itemView.findViewById(R.id.autoLogin);

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.logout) {
                    JWLog.e("","logout");
                } else if(v.getId() == R.id.autoLogin) {
                    JWLog.e("","autoLogin");
                }
            }
        };

        logoutButton.setOnClickListener(mOnClickListener);
        autoLoginButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public LoginSettingViewHolder setLayout() {
        return new LoginSettingViewHolder(mView);
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
