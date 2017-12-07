package com.friendly.walking.broadcast;

import android.content.Context;
import android.content.Intent;

/**
 * Created by jungjiwon on 2017. 11. 1..
 */

public class JWBroadCast {

    public static final String BROAD_CAST_LOGIN                 = "com.friendly.walking.BROAD_CAST_LOGIN";
    public static final String BROAD_CAST_LOGOUT                = "com.friendly.walking.BROAD_CAST_LOGOUT";
    public static final String BROAD_CAST_UPDATE_SETTING_UI     = "com.friendly.walking.BROAD_CAST_UPDATE_SETTING_UI";

    public static final String BROAD_CAST_FACEBOOK_LOGIN        = "com.friendly.walking.BROAD_CAST_FACEBOOK_LOGIN";
    public static final String BROAD_CAST_GOOGLE_LOGIN          = "com.friendly.walking.BROAD_CAST_GOOGLE_LOGIN";
    public static final String BROAD_CAST_KAKAO_LOGIN           = "com.friendly.walking.BROAD_CAST_KAKAO_LOGIN";


    public JWBroadCast() {}

    public static void sendBroadcast(Context context, Intent intent) {
        if(context != null) {
            intent.setFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
            context.sendBroadcast(intent);
        }
    }
}
