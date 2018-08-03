package com.friendly.walking.dataSet;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017-10-22.
 */

public class WalkingData extends Object {

    public String uid = "";
    public String mem_email = "";
    public String mem_nickname = "";
    public boolean mem_walking_my_location_yn = true;
    public boolean mem_walking_chatting_yn = true;
    public String lat = "0";
    public String lot = "0";

    public WalkingData() {}

    @Override
    public String toString() {
        return "uid :"+uid+
                ", mem_email :"+mem_email+
                ", mem_nickname :"+mem_nickname+
                ", mem_walking_my_location_yn :"+mem_walking_my_location_yn+
                ", mem_walking_chatting_yn :"+mem_walking_chatting_yn+
                ", lat :"+lat+
                ", lot :"+lot;
    }
}
