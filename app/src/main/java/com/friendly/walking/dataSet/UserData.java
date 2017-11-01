package com.friendly.walking.dataSet;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017-10-22.
 */

public class UserData extends Object {

    public String uid = "0";

    public String mem_email = "";
    public boolean mem_auto_login = false;
    public boolean mem_notification_yn = true;
    public boolean mem_location_yn = false;
    public Map<String, String> mem_address = new HashMap<>();
    public String mem_register_datetime = "";
    public long member_index = -1;
    public String mem_last_login_datetime = "";
    public List<PetData> pet_list = new ArrayList<>();
    public boolean mem_auto_stroll_mode = true;
    public Map<String, String> mem_alarm_time = new HashMap<>();

    public UserData() {
        Log.e("","@@@ LoginData constructor @@@");
    }

}
