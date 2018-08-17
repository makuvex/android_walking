package com.friendly.walking.dataSet;

import com.friendly.walking.adapter.baseInterface.BaseSettingDataSetInterface;

/**
 * Created by Administrator on 2017-10-22.
 */

public class WalkingSettingListData implements BaseSettingDataSetInterface {

    private boolean isMyLocationAccepted;
    private boolean isChattingAcepted;


    public WalkingSettingListData() {
    }

    public WalkingSettingListData(boolean isMyLocationAccepted, boolean isChattingAcepted) {
        this.isMyLocationAccepted = isMyLocationAccepted;
        this.isChattingAcepted = isChattingAcepted;
    }

    public boolean getMyLocationAcceptedState() {
        return isMyLocationAccepted;
    }

    public void setMyLocationAcceptedState(boolean isMyLocationAccepted) {
        this.isMyLocationAccepted = isMyLocationAccepted;
    }

    public boolean getChattingAccepted() {
        return isChattingAcepted;
    }

    public void setChattingAccepted(boolean isChattingAcepted) {
        this.isChattingAcepted = isChattingAcepted;
    }

    @Override
    public Object getDataSet() {
        return this;
    }

    @Override
    public void setDataSet(Object object) {
        if(object instanceof WalkingSettingListData) {
            WalkingSettingListData data = (WalkingSettingListData)object;
            this.isMyLocationAccepted = data.isMyLocationAccepted;
            this.isChattingAcepted = data.isChattingAcepted;
        }
    }
}
