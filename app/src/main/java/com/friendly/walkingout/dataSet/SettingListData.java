package com.friendly.walkingout.dataSet;

import android.util.Log;

import com.friendly.walkingout.adapter.BaseSettingDataSetInterface;

/**
 * Created by Administrator on 2017-10-22.
 */

public class SettingListData implements BaseSettingDataSetInterface {
    private String title;
    private String titleDesc;
    private int imageButtonResource;

    public SettingListData() {
    }

    public SettingListData(String title, String titleDesc, int imageButton) {
        this.title = title;
        this.titleDesc = titleDesc;
        this.imageButtonResource = imageButton;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleDesc() {
        return titleDesc;
    }

    public void setTitleDesc(String titleDesc) {
        this.titleDesc = titleDesc;
    }

    public int getImageButtonResource() {
        return imageButtonResource;
    }

    public void setImageButtonResource(int imageButton) {
        this.imageButtonResource = imageButton;
    }

    @Override
    public Object getDataSet() {
        return this;
    }
}
