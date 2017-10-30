package com.friendly.walking.adapter.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import com.friendly.walking.R;
import com.friendly.walking.adapter.baseInterface.BaseSettingViewHolderInterface;
import com.friendly.walking.dataSet.LocationSettingListData;
import com.friendly.walking.util.JWLog;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

public class LocationSettingViewHolder extends BaseViewHolder implements BaseSettingViewHolderInterface {

    public ImageButton locationYnButton;
    public ImageButton geofenceYnButton;

    public boolean locationYn;
    public boolean geofenceYn;

    private View.OnClickListener mOnClickListener;

    public LocationSettingViewHolder(Context context, View itemView) {
        super(context, itemView);
        JWLog.e("", "");

        locationYnButton = (ImageButton) itemView.findViewById(R.id.accept_location);
        geofenceYnButton= (ImageButton) itemView.findViewById(R.id.accept_geofence);

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JWLog.e("","acceptButton");
            }
        };

        locationYnButton.setOnClickListener(mOnClickListener);
        geofenceYnButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public LocationSettingViewHolder setLayout(Context context) {
        return new LocationSettingViewHolder(context, mView);
    }

    @Override
    public void setDataSet(Object object) {
        if(object instanceof LocationSettingListData) {
            LocationSettingListData data = (LocationSettingListData)object;

            this.locationYn = data.getLocationYn();
            this.geofenceYn = data.getGeofenceYn();

            this.itemView.setTag(data);
        }
    }
}
