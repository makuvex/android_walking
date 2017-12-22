package com.friendly.walking.adapter.viewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.friendly.walking.activity.BaseActivity;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.NotificationSettingListData;
import com.friendly.walking.R;
import com.friendly.walking.adapter.baseInterface.BaseSettingViewHolderInterface;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.JWLog;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

public class NotificationSettingViewHolder extends BaseViewHolder implements BaseSettingViewHolderInterface {

    public ImageButton acceptButton;
    public ImageButton acceptGeofenceButton;

    public boolean isAccepted;
    public boolean isGeoAccepted;

    private View.OnClickListener mOnClickListener;

    public NotificationSettingViewHolder(Activity activity, View itemView) {
        super(activity, itemView);
        JWLog.e("", "");

        acceptButton = (ImageButton) itemView.findViewById(R.id.accept_notification);
        acceptGeofenceButton = (ImageButton) itemView.findViewById(R.id.accept_geofence_notification);

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean loginYn = PreferencePhoneShared.getLoginYn(mActivity);
                if(loginYn) {
                    v.setSelected(!v.isSelected());

                    if(v == acceptButton) {
                        PreferencePhoneShared.setNotificationYn(mActivity, v.isSelected());

                        if(mActivity instanceof BaseActivity) {
                            //JWBroadCast.sendBroadcast(mActivity, new Intent(JWBroadCast.BROAD_CAST_SHOW_PROGRESS_BAR));
                        }
                    } else if(v == acceptGeofenceButton) {
                        PreferencePhoneShared.setGeoNotificationYn(mActivity, v.isSelected());
                    }
                } else {
                    Toast.makeText(mActivity, "로그인 후 설정 가능합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        acceptButton.setOnClickListener(mOnClickListener);
        acceptGeofenceButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public NotificationSettingViewHolder setLayout(Activity activity) {
        return new NotificationSettingViewHolder(activity, mView);
    }

    @Override
    public void setDataSet(Object object) {
        if(object instanceof NotificationSettingListData) {
            NotificationSettingListData data = (NotificationSettingListData)object;

            this.isAccepted = data.getAcceptedState();
            this.isGeoAccepted = data.getGeofenceAcceptedState();

            acceptButton.setSelected(isAccepted);
            acceptGeofenceButton.setSelected(isGeoAccepted);

            this.itemView.setTag(data);
        }
    }
}
