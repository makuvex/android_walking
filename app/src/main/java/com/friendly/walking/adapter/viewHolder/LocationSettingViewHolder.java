package com.friendly.walking.adapter.viewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import com.friendly.walking.util.JWToast;

import com.friendly.walking.R;
import com.friendly.walking.adapter.baseInterface.BaseSettingViewHolderInterface;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.LocationSettingListData;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.JWLog;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

public class LocationSettingViewHolder extends BaseViewHolder implements BaseSettingViewHolderInterface {

    public ImageButton locationYnButton;
    //public ImageButton geofenceYnButton;

    public boolean locationYn;
    public boolean geofenceYn;

    private View.OnClickListener mOnClickListener;

    public LocationSettingViewHolder(Activity activity, View itemView) {
        super(activity, itemView);
        JWLog.e("", "");

        locationYnButton = (ImageButton) itemView.findViewById(R.id.accept_location);
        //geofenceYnButton= (ImageButton) itemView.findViewById(R.id.accept_geofence);

        JWLog.e("getLocationYn "+PreferencePhoneShared.getLocationYn(mActivity));
        if(PreferencePhoneShared.getLoginYn(mActivity)) {
            locationYnButton.setSelected(PreferencePhoneShared.getLocationYn(mActivity));
        } else {
            locationYnButton.setSelected(false);
        }
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JWLog.e("","acceptButton");

                if(PreferencePhoneShared.getLoginYn(mActivity)) {
                    if(v == locationYnButton) {
                        v.setSelected(!v.isSelected());
                        Intent i = new Intent(JWBroadCast.BROAD_CAST_CHANGE_LOCATION_YN);
                        i.putExtra("value", v.isSelected());
                        JWBroadCast.sendBroadcast(mActivity, i);
                    }
                } else {
                    JWToast.showToast("로그인 후 설정 가능합니다.");
                }
            }
        };

        locationYnButton.setOnClickListener(mOnClickListener);
        //geofenceYnButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public LocationSettingViewHolder setLayout(Activity activity) {
        return new LocationSettingViewHolder(activity, mView);
    }

    @Override
    public void setDataSet(Object object) {
        if(object instanceof LocationSettingListData) {
            LocationSettingListData data = (LocationSettingListData)object;

            this.locationYn = data.getLocationYn();
            this.geofenceYn = data.getGeofenceYn();

            locationYnButton.setSelected(this.locationYn);

            this.itemView.setTag(data);
        }
    }
}
