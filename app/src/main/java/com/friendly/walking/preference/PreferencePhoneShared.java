package com.friendly.walking.preference;

import android.content.Context;


public class PreferencePhoneShared extends BasePreference {

    private static final String PREFERENCE_NAME                             = "STROLL_PHONE_SHARED_PREFERENCE";

    private static final String KEY_LOGIN_YN                                = "KEY_LOGIN_YN";
    private static final String KEY_LOGIN_ID                                = "KEY_LOGIN_ID";
    private static final String KEY_LOGIN_PASSWORD                          = "KEY_LOGIN_PASSWORD";

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
}
