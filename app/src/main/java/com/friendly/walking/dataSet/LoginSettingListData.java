package com.friendly.walking.dataSet;

import com.friendly.walking.adapter.baseInterface.BaseSettingDataSetInterface;

/**
 * Created by Administrator on 2017-10-22.
 */

public class LoginSettingListData implements BaseSettingDataSetInterface {

    private String loginID;
    private boolean autoLogin;

    public LoginSettingListData() {
    }

    public LoginSettingListData(String loginID, boolean autoLogin) {
        this.loginID = loginID;
        this.autoLogin = autoLogin;
    }

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    public boolean getAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }

    @Override
    public Object getDataSet() {
        return this;
    }

    @Override
    public void setDataSet(Object object) {
        if(object instanceof LoginSettingListData) {
            LoginSettingListData data = (LoginSettingListData)object;
            this.loginID = data.loginID;
            this.autoLogin = data.autoLogin;
        }
    }
}
