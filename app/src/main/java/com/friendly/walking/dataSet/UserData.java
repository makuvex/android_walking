package com.friendly.walking.dataSet;

import android.util.Log;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017-10-22.
 */

public class UserData extends Object {

    public String uid;

    public String mem_email;
    public boolean mem_auto_login;
    public boolean mem_notification_yn = true;
    public boolean mem_location_yn = false;
    public Map<String, String> mem_address;
    public String mem_register_datetime;
    public long member_index;
    public String mem_last_login_datetime;
    public List<PetData> pet_list;
    public boolean mem_auto_stroll_mode = true;


    public UserData() {
        Log.e("","@@@ LoginData constructor @@@");
    }

//    public String getLoginEmail() {
//        return loginEmail;
//    }
//
//    public void setLoginEmail(String email) {
//        this.loginEmail = email;
//    }
//
//    public String getUID() {
//        return uid;
//    }
//
//    public void setUID(String uid) {
//        this.uid = uid;
//    }
//
//    public String getPetName() {
//        return petName;
//    }
//
//    public void setPetName(String petName) {
//        this.petName = petName;
//    }
}
