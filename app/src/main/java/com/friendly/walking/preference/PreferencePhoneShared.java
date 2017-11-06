package com.friendly.walking.preference;

import android.content.Context;


public class PreferencePhoneShared extends BasePreference {

    private static final String PREFERENCE_NAME                             = "STROLL_PHONE_SHARED_PREFERENCE";

    private static final String KEY_AUTO_LOGIN_YN                           = "KEY_AUTO_LOGIN";
    private static final String KEY_LOGIN_YN                                = "KEY_LOGIN_YN";
    private static final String KEY_LOGIN_ID                                = "KEY_LOGIN_ID";
    private static final String KEY_LOGIN_PASSWORD                          = "KEY_LOGIN_PASSWORD";
    private static final String KEY_USER_UID                                = "KEY_USER_UID";

    private static final String KEY_AUTO_LOGIN_TYPE                         = "KEY_LOGIN_TYPE";     //0 : EMAIL, 1 : GOOGLE, 2 : FACEBOOK

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

}
