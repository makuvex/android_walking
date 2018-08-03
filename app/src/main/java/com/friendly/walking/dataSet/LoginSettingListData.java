package com.friendly.walking.dataSet;

import com.friendly.walking.adapter.baseInterface.BaseSettingDataSetInterface;

/**
 * Created by Administrator on 2017-10-22.
 */

public class LoginSettingListData implements BaseSettingDataSetInterface {

    private String loginID;
    private boolean autoLogin;
    private String nickName;
    private int walkingCoin;

    public LoginSettingListData() {
    }

    public LoginSettingListData(String loginID, String nickName, boolean autoLogin, int walkingCoin) {
        this.loginID = loginID;
        this.autoLogin = autoLogin;
        this.nickName = nickName;
        this.walkingCoin = walkingCoin;
    }

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean getAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }

    public int getWalkingCoin() {
        return walkingCoin;
    }

    public void setWalkingCoin(int walkingCoin) {
        this.walkingCoin = walkingCoin;
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
            this.nickName = data.nickName;
            this.walkingCoin = data.walkingCoin;
        }
    }
}
