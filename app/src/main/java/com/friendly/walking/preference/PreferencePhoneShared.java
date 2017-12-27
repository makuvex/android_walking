package com.friendly.walking.preference;

import android.content.Context;


public class PreferencePhoneShared extends BasePreference {

    private static final String PREFERENCE_NAME                             = "STROLL_PHONE_SHARED_PREFERENCE";

    private static final String KEY_AUTO_LOGIN_YN                           = "KEY_AUTO_LOGIN";
    private static final String KEY_LOGIN_YN                                = "KEY_LOGIN_YN";
    private static final String KEY_LOGIN_ID                                = "KEY_LOGIN_ID";
    private static final String KEY_LOGIN_PASSWORD                          = "KEY_LOGIN_PASSWORD";
    private static final String KEY_USER_UID                                = "KEY_USER_UID";

    private static final String KEY_AUTO_LOGIN_TYPE                         = "KEY_LOGIN_TYPE";     //0 : EMAIL, 1 : GOOGLE, 2 : FACEBOOK, 3 : KAKAO

    private static final String KEY_AUTO_START_STROLL_TIME                  = "KEY_AUTO_START_STROLL_TIME";
    private static final String KEY_AUTO_END_STROLL_TIME                    = "KEY_AUTO_END_STROLL_TIME";
    private static final String KEY_AUTO_STROLL_MODE                        = "KEY_AUTO_STROLL_MODE";

    private static final String KEY_NOTIFICATION_YN                         = "KEY_NOTIFICATION_YN";
    private static final String KEY_GEO_NOTIFICATION_YN                     = "KEY_GEO_NOTIFICATION_YN";
    private static final String KEY_LOCATION_YN                             = "KEY_LOCATION_YN";

    private static final String KEY_VERSION_INFO                            = "KEY_VERSION_INFO";

    private static final String KEY_FCM_TOKEN                               = "KEY_FCM_TOKEN";

    public static void setAutoLoginYn(Context cxt, boolean autoLoginYn){
        putBoolean(cxt, PREFERENCE_NAME, KEY_AUTO_LOGIN_YN, autoLoginYn);
    }

    public static boolean getAutoLoginYn(Context cxt){
        return getBoolean(cxt, PREFERENCE_NAME, KEY_AUTO_LOGIN_YN, false);
    }

    public static void setLoginYn(Context cxt, boolean loginYn){
        putBoolean(cxt, PREFERENCE_NAME, KEY_LOGIN_YN, loginYn);
    }

    public static boolean getLoginYn(Context cxt){
        return getBoolean(cxt, PREFERENCE_NAME, KEY_LOGIN_YN, false);
    }

    public static void setNotificationYn(Context cxt, boolean notificationYn){
        putBoolean(cxt, PREFERENCE_NAME, KEY_NOTIFICATION_YN, notificationYn);
    }

    public static boolean getNotificationYn(Context cxt){
        return getBoolean(cxt, PREFERENCE_NAME, KEY_NOTIFICATION_YN, false);
    }

    public static void setGeoNotificationYn(Context cxt, boolean geoNotificationYn){
        putBoolean(cxt, PREFERENCE_NAME, KEY_GEO_NOTIFICATION_YN, geoNotificationYn);
    }

    public static boolean getGeoNotificationYn(Context cxt){
        return getBoolean(cxt, PREFERENCE_NAME, KEY_GEO_NOTIFICATION_YN, false);
    }

    public static void setLocationYn(Context cxt, boolean locationYn){
        putBoolean(cxt, PREFERENCE_NAME, KEY_LOCATION_YN, locationYn);
    }

    public static boolean getLocationYn(Context cxt){
        return getBoolean(cxt, PREFERENCE_NAME, KEY_LOCATION_YN, false);
    }

    public static void setLoginID(Context cxt, String id){
        putString(cxt, PREFERENCE_NAME, KEY_LOGIN_ID, id);
    }

    public static String getLoginID(Context cxt){
        return getString(cxt, PREFERENCE_NAME, KEY_LOGIN_ID, "");
    }

    public static void setLoginPassword(Context cxt, String password){
        putString(cxt, PREFERENCE_NAME, KEY_LOGIN_PASSWORD, password);
    }

    public static String getLoginPassword(Context cxt){
        return getString(cxt, PREFERENCE_NAME, KEY_LOGIN_PASSWORD, "");
    }

    public static void setUserUID(Context cxt, String uid){
        putString(cxt, PREFERENCE_NAME, KEY_USER_UID, uid);
    }

    public static String getUserUid(Context cxt){
        return getString(cxt, PREFERENCE_NAME, KEY_USER_UID, "");
    }

    public static void setAutoLoginType(Context cxt, int type){
        putInt(cxt, PREFERENCE_NAME, KEY_AUTO_LOGIN_TYPE, type);
    }

    public static int getAutoLoginType(Context cxt){
        return getInt(cxt, PREFERENCE_NAME, KEY_AUTO_LOGIN_TYPE, -1);
    }

    public static void setStartStrollTime(Context cxt, String time){
        putString(cxt, PREFERENCE_NAME, KEY_AUTO_START_STROLL_TIME, time);
    }

    public static String getStartStrollTime(Context cxt){
        return getString(cxt, PREFERENCE_NAME, KEY_AUTO_START_STROLL_TIME, "");
    }

    public static void setEndStrollTime(Context cxt, String time){
            putString(cxt, PREFERENCE_NAME, KEY_AUTO_END_STROLL_TIME, time);
        }

    public static String getEndStrollTime(Context cxt){
        return getString(cxt, PREFERENCE_NAME, KEY_AUTO_END_STROLL_TIME, "");
    }

    public static void setAutoStrollMode(Context cxt, boolean mode){
        putBoolean(cxt, PREFERENCE_NAME, KEY_AUTO_STROLL_MODE, mode);
    }

    public static boolean getAutoStrollMode(Context cxt){
        return getBoolean(cxt, PREFERENCE_NAME, KEY_AUTO_STROLL_MODE, false);
    }

    public static void setVersionInfo(Context cxt, String version){
        putString(cxt, PREFERENCE_NAME, KEY_VERSION_INFO, version);
    }

    public static String getVersionInfo(Context cxt){
        return getString(cxt, PREFERENCE_NAME, KEY_VERSION_INFO, null);
    }

    public static void setFCMToken(Context cxt, String token){
        putString(cxt, PREFERENCE_NAME, KEY_FCM_TOKEN, token);
    }

    public static String getFCMToken(Context cxt){
        return getString(cxt, PREFERENCE_NAME, KEY_FCM_TOKEN, null);
    }
}
