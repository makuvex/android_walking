package com.friendly.walking.broadcast;

import android.content.Context;
import android.content.Intent;

import com.friendly.walking.util.JWLog;

/**
 * Created by jungjiwon on 2017. 11. 1..
 */

public class JWBroadCast {

    public static final String BROAD_CAST_LOGIN                 = "com.friendly.walking.BROAD_CAST_LOGIN";
    public static final String BROAD_CAST_LOGOUT                = "com.friendly.walking.BROAD_CAST_LOGOUT";
    public static final String BROAD_CAST_WITHDRAW              = "com.friendly.walking.BROAD_CAST_WITHDRAW";
    public static final String BROAD_CAST_UPDATE_SETTING_UI     = "com.friendly.walking.BROAD_CAST_UPDATE_SETTING_UI";

    public static final String BROAD_CAST_EMAIL_LOGIN           = "com.friendly.walking.BROAD_CAST_EMAIL_LOGIN";
    public static final String BROAD_CAST_FACEBOOK_LOGIN        = "com.friendly.walking.BROAD_CAST_FACEBOOK_LOGIN";
    public static final String BROAD_CAST_GOOGLE_LOGIN          = "com.friendly.walking.BROAD_CAST_GOOGLE_LOGIN";
    public static final String BROAD_CAST_KAKAO_LOGIN           = "com.friendly.walking.BROAD_CAST_KAKAO_LOGIN";

    public static final String BROAD_CAST_GEOFENCE_OUT_DETECTED = "com.friendly.walking.BROAD_CAST_BROAD_CAST_GEOFENCE_OUT_DETECTED";
    public static final String BROAD_CAST_GEOFENCE_IN_DETECTED  = "com.friendly.walking.BROAD_CAST_BROAD_CAST_GEOFENCE_IN_DETECTED";

    public static final String BROAD_CAST_ADD_GEOFENCE          = "com.friendly.walking.BROAD_CAST_ADD_GEOFENCE";
    public static final String BROAD_CAST_REMOVE_GEOFENCE       = "com.friendly.walking.BROAD_CAST_REMOVE_GEOFENCE";

    public static final String BROAD_CAST_SHOW_PROGRESS_BAR     = "com.friendly.walking.BROAD_CAST_SHOW_PROGRESS_BAR";
    public static final String BROAD_CAST_HIDE_PROGRESS_BAR     = "com.friendly.walking.BROAD_CAST_HIDE_PROGRESS_BAR";

    public static final String BROAD_CAST_CHANGE_NOTIFICATION_YN        = "com.friendly.walking.BROAD_CAST_CHANGE_NOTIFICATION_YN";
    public static final String BROAD_CAST_CHANGE_GEO_NOTIFICATION_YN    = "com.friendly.walking.BROAD_CAST_CHANGE_GEO_NOTIFICATION_YN";
    public static final String BROAD_CAST_CHANGE_LOCATION_YN            = "com.friendly.walking.BROAD_CAST_CHANGE_LOCATION_YN";

    public static final String BROAD_CAST_CHANGE_WALKING_MY_LOCATION_YN         = "com.friendly.BROAD_CAST_CHANGE_WALKING_MY_LOCATION_YN.BROAD_CAST_CHANGE_NOTIFICATION_YN";
    public static final String BROAD_CAST_CHANGE_WALKING_CHATTING_YN            = "com.friendly.walking.BROAD_CAST_CHANGE_WALKING_CHATTING_YN";

    public static final String BROAD_CAST_REFRESH_USER_DATA             = "com.friendly.walking.BROAD_CAST_REFRESH_USER_DATA";
    public static final String BROAD_CAST_UPDATE_PROFILE                = "com.friendly.walking.BROAD_CAST_UPDATE_PROFILE";

    public static final String BROAD_CAST_REQUEST_LOCATION             = "com.friendly.walking.BROAD_CAST_REQUEST_LOCATION";
    public static final String BROAD_CAST_RESPONSE_LOCATION            = "com.friendly.walking.BROAD_CAST_RESPONSE_LOCATION";


    public JWBroadCast() {}

    public static void sendBroadcast(Context context, Intent intent) {
        if(context != null) {
            JWLog.e("intent "+intent);
            if(intent.getAction().equals("com.friendly.walking.BROAD_CAST_HIDE_PROGRESS_BAR")) {
                JWLog.e("");
            }
            intent.setFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
            context.sendBroadcast(intent);
        }
    }
}
