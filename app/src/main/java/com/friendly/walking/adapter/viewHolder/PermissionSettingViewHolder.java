package com.friendly.walking.adapter.viewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.friendly.walking.R;
import com.friendly.walking.adapter.baseInterface.BaseSettingViewHolderInterface;
import com.friendly.walking.dataSet.NotificationSettingListData;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.JWLog;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

public class PermissionSettingViewHolder extends BaseViewHolder implements BaseSettingViewHolderInterface {

    public ImageButton moreButton;

    private View.OnClickListener mOnClickListener;

    public PermissionSettingViewHolder(Activity activity, View itemView) {
        super(activity, itemView);
        JWLog.e("", "");

        moreButton = (ImageButton) itemView.findViewById(R.id.permission_more);

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JWLog.e("","moreButton");
                boolean loginYn = PreferencePhoneShared.getLoginYn(mActivity);
                if(loginYn) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.parse("package:" + mActivity.getPackageName()));
                    mActivity.startActivity(intent);
                } else {
                    Toast.makeText(mActivity, "로그인 후 설정 가능합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        moreButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public PermissionSettingViewHolder setLayout(Activity activity) {
        return new PermissionSettingViewHolder(mActivity, mView);
    }

    @Override
    public void setDataSet(Object object) {
        if(object instanceof NotificationSettingListData) {

        }
    }
}
