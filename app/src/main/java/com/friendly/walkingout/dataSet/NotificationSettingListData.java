package com.friendly.walkingout.dataSet;

import com.friendly.walkingout.adapter.BaseSettingDataSetInterface;

/**
 * Created by Administrator on 2017-10-22.
 */

public class NotificationSettingListData implements BaseSettingDataSetInterface {

    private boolean isAccepted;

    public NotificationSettingListData() {
    }

    public NotificationSettingListData(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    public boolean getAcceptedState() {
        return isAccepted;
    }

    public void setAcceptedState(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    @Override
    public Object getDataSet() {
        return this;
    }
}
