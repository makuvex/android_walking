package com.friendly.walkingout.dataSet;

import android.util.Log;

/**
 * Created by Administrator on 2017-10-22.
 */

public class UserData extends Object {
    public String loginEmail;
    public String uid;
    public String petName;

    public UserData() {
        Log.e("","@@@ LoginData constructor @@@");
    }

    public UserData(String email, String uid, String petName) {
        this.loginEmail = email;
        this.uid = uid;
        this.petName = petName;
    }

    public String getLoginEmail() {
        return loginEmail;
    }

    public void setLoginEmail(String email) {
        this.loginEmail = email;
    }

    public String getUID() {
        return uid;
    }

    public void setUID(String uid) {
        this.uid = uid;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }
}
