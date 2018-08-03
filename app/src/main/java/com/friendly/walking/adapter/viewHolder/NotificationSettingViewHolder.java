package com.friendly.walking.adapter.viewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import com.friendly.walking.util.JWToast;

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

        acceptButton.setSelected(PreferencePhoneShared.getNotificationYn(mActivity));
        acceptGeofenceButton.setSelected(PreferencePhoneShared.getGeoNotificationYn(mActivity));

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean loginYn = PreferencePhoneShared.getLoginYn(mActivity);
                if(loginYn) {
                    v.setSelected(!v.isSelected());

                    if(v == acceptButton) {
                        Intent i = new Intent(JWBroadCast.BROAD_CAST_CHANGE_NOTIFICATION_YN);
                        i.putExtra("value", v.isSelected());
                        JWBroadCast.sendBroadcast(mActivity, i);
                    } else if(v == acceptGeofenceButton) {
                        Intent i = new Intent(JWBroadCast.BROAD_CAST_CHANGE_GEO_NOTIFICATION_YN);
                        i.putExtra("value", v.isSelected());
                        JWBroadCast.sendBroadcast(mActivity, i);
                    }
                } else {
                    JWToast.showToast("로그인 후 설정 가능합니다.");
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
