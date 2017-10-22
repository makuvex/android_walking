package com.friendly.walkingout.notification;

import android.util.Log;

/**
 * Created by Administrator on 2017-10-22.
 */

public class LoginData extends Object {
    public String loginId;
    public String loginPassword;

    public LoginData() {
        Log.e("","@@@ LoginData constructor @@@");
    }

    public LoginData(String id, String password) {
        this.loginId = id;
        this.loginPassword = password;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String id) {
        this.loginId = id;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String password) {
        this.loginPassword = password;
    }
}
