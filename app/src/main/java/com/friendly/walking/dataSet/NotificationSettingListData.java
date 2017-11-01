package com.friendly.walking.dataSet;

import com.friendly.walking.adapter.baseInterface.BaseSettingDataSetInterface;

/**
 * Created by Administrator on 2017-10-22.
 */

public class NotificationSettingListData implements BaseSettingDataSetInterface {

    private boolean isAccepted;
    private boolean isGeofenceAccepted;


    public NotificationSettingListData() {
    }

    public NotificationSettingListData(boolean isAccepted, boolean isGeofenceAccepted) {
        this.isAccepted = isAccepted;
        this.isGeofenceAccepted = isGeofenceAccepted;
    }

    public boolean getAcceptedState() {
        return isAccepted;
    }

    public void setAcceptedState(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    public boolean getGeofenceAcceptedState() {
        return isGeofenceAccepted;
    }

    public void setGeofenceAcceptedState(boolean geofenceAccepted) {
        this.isGeofenceAccepted = geofenceAccepted;
    }

    @Override
    public Object getDataSet() {
        return this;
    }

    @Override
    public void setDataSet(Object object) {

    }
}
