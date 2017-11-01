package com.friendly.walking.dataSet;

import com.friendly.walking.adapter.baseInterface.BaseSettingDataSetInterface;

/**
 * Created by Administrator on 2017-10-22.
 */

public class LocationSettingListData implements BaseSettingDataSetInterface {

    private boolean locationYn;
    private boolean geofenceYn;

    public LocationSettingListData() {
    }

    public LocationSettingListData(boolean locationYn, boolean geofenceYn) {
        this.locationYn = locationYn;
        this.geofenceYn = geofenceYn;
    }

    public boolean getLocationYn() {
        return locationYn;
    }

    public void setLocationYn(boolean locationYn) {
        this.locationYn = locationYn;
    }

    public boolean getGeofenceYn() {
        return geofenceYn;
    }

    public void setGeofenceYn(boolean geofenceYn) {
        this.geofenceYn = geofenceYn;
    }

    @Override
    public Object getDataSet() {
        return this;
    }

    @Override
    public void setDataSet(Object object) {

    }
}
