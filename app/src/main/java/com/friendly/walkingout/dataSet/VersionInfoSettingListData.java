package com.friendly.walkingout.dataSet;

import com.friendly.walkingout.adapter.baseInterface.BaseSettingDataSetInterface;

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
}
