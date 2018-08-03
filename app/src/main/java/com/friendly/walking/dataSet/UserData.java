package com.friendly.walking.dataSet;

import android.text.TextUtils;
import android.util.Log;

import com.friendly.walking.util.JWLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 2017-10-22.
 */

public class UserData extends Object {

    public String uid = "0";

    public String mem_email = "";
    public String mem_nickname = "";
    public boolean mem_auto_login = false;
    public boolean mem_notification_yn = true;
    public boolean mem_geo_notification_yn = true;
    public boolean mem_location_yn = true;
    public Map<String, String> mem_address = new HashMap<>();
    public String mem_register_datetime = "";
    public long member_index = -1;
    public String mem_last_login_datetime = "";
    public List<PetData> pet_list = new ArrayList<>();
    public boolean mem_auto_stroll_mode = true;
    public Map<String, String> mem_alarm_time = new HashMap<>();
    public Map<String, ArrayList<LocationData>> walking_location_list = new HashMap<>();
    public Map<String, String> walking_time_list = new HashMap<>();
    public int mem_auto_stroll_distance = 100;

    public String joinBy = "";
    public LocationData mem_current_location = new LocationData();

    public boolean mem_is_walking = false;
    public int walking_coin = 0;
    public boolean mem_walking_my_location_yn = true;
    public boolean mem_walking_chatting_yn = true;

    public UserData() {
        Log.e("","@@@ LoginData constructor @@@");
    }

    @Override
    public String toString() {
        return "mem_email :"+mem_email+
                ", uid :"+uid+
                ", mem_auto_login :"+mem_auto_login+
                ", mem_notification_yn :"+mem_notification_yn+
                ", mem_geo_notification_yn :"+mem_geo_notification_yn+
                ", mem_location_yn :"+mem_location_yn+
                ", mem_address :"+mem_address+
                ", mem_register_datetime :"+mem_register_datetime+
                ", mem_last_login_datetime :"+mem_last_login_datetime+
                ", pet_list :"+pet_list+
                ", mem_auto_stroll_mode :"+mem_auto_stroll_mode+
                ", mem_alarm_time :"+mem_alarm_time+
                ", walking_location_list :"+walking_location_list +
                ", mem_auto_stroll_distance :"+mem_auto_stroll_distance +
                ", walking_coin :"+walking_coin;
    }
}
