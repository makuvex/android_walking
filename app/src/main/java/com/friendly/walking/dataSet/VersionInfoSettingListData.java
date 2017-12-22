package com.friendly.walking.dataSet;

import com.friendly.walking.adapter.baseInterface.BaseSettingDataSetInterface;

/**
 * Created by Administrator on 2017-10-22.
 */

public class VersionInfoSettingListData implements BaseSettingDataSetInterface {

    private String currentVersion;
    private String latestVersion;

    public VersionInfoSettingListData() {
    }

    public VersionInfoSettingListData(String currentVersion, String latestVersion) {
        this.currentVersion = currentVersion;
        this.latestVersion = latestVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String loginID) {
        this.currentVersion = loginID;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void getLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    @Override
    public Object getDataSet() {
        return this;
    }

    @Override
    public void setDataSet(Object object) {
        if(object instanceof VersionInfoSettingListData) {
            VersionInfoSettingListData data = (VersionInfoSettingListData)object;
            this.currentVersion = data.currentVersion;
            this.latestVersion = data.latestVersion;
        }
    }
}
