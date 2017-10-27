package com.friendly.walkingout.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class BasePreference {
    
    /**
     * 스트링 값 반환
     * @param cxt
     * @param key
     * @param defValue
     * @return
     */
    protected static String getString(final Context cxt, final String preferenceName, final String key, final String defValue){
        SharedPreferences sp = cxt.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        if(null == sp) {
            return defValue;
        }
        return sp.getString(key, defValue);
    }

    /**
     * 스트링 값 저장
     * @param cxt
     * @param key
     * @param value
     */
    protected static boolean putString(final Context cxt, final String preferenceName, final String key, final String value){
        if(null == cxt) {
            return false;
        }
        
        SharedPreferences sp = cxt.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        if(null == sp) {
            return false;
        }
        Editor et = sp.edit();
        et.putString(key, value);
        if(true == et.commit()){
            return true;
        }
        return false;
    }

    /**
     * 불리언 값 반환
     * @param cxt
     * @param key
     * @param defValue
     * @return
     */
    protected static boolean getBoolean(final Context cxt, final String preferenceName, final String key, final boolean defValue){
        if(null == cxt) {
            return defValue;
        }
        
        SharedPreferences sp = cxt.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        if(null == sp) {
            return defValue;
        }
        return sp.getBoolean(key, defValue);
    }


    /**
     * Boolean 값 저장
     * @param cxt
     * @param key
     * @param value
     */
    protected static boolean putBoolean(final Context cxt, final String preferenceName, final String key, final boolean value){
        if(null == cxt) {
            return false;
        }
        
        SharedPreferences sp = cxt.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        if(null == sp) {
            return false;
        }
        
        Editor et = sp.edit();
        et.putBoolean(key, value);
        if(true == et.commit()){
            return true;
        }
        return false;
    }

    /**
     *
     * @param cxt
     * @param preferenceName
     * @param key
     * @param value
     * @return
     */
    protected static boolean putInt(final Context cxt, final String preferenceName, final String key, final int value){
        if(null == cxt) {
            return false;
        }
        
        SharedPreferences sp = cxt.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        if(null == sp) {
            return false;
        }
        Editor et = sp.edit();
        et.putInt(key, value);
        if(true == et.commit()){
            return true;
        }
        return false;
    }

    /**
     * @param cxt
     * @return
     */
    protected static int getInt(final Context cxt, final String preferenceName, final String key, final int defValue){
        if(null == cxt) {
            return defValue;
        }
        
        SharedPreferences sp = cxt.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        if(null == sp) {
            return defValue;
        }
        return sp.getInt(key, defValue);
    }
    
    /**
     *
     * @param cxt
     * @param preferenceName
     * @param key
     * @param value
     * @return
     */
    protected static boolean putLong(final Context cxt, final String preferenceName, final String key, final long value){
        if(null == cxt) {
            return false;
        }
        
        SharedPreferences sp = cxt.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        if(null == sp) {
            return false;
        }
        Editor et = sp.edit();
        et.putLong(key, value);
        if(true == et.commit()){
            return true;
        }
        return false;
    }

    /**
     * @param cxt
     * @return
     */
    protected static long getLong(final Context cxt, final String preferenceName, final String key, final long defValue){
        if(null == cxt) {
            return defValue;
        }
        
        SharedPreferences sp = cxt.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        if(null == sp) {
            return defValue;
        }
        return sp.getLong(key, defValue);
    }
    
    /**
     *
     * @param cxt
     * @param preferenceName
     * @param key
     * @param value
     * @return
     */
    protected static boolean putFloat(final Context cxt, final String preferenceName, final String key, final float value){
        if(null == cxt) {
            return false;
        }
        
        SharedPreferences sp = cxt.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        if(null == sp) {
            return false;
        }
        Editor et = sp.edit();
        et.putFloat(key, value);
        if(true == et.commit()){
            return true;
        }
        return false;
    }

    /**
     * @param cxt
     * @return
     */
    protected static float getFloat(final Context cxt, final String preferenceName, final String key, final float defValue){
        if(null == cxt) {
            return defValue;
        }
        
        SharedPreferences sp = cxt.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        if(null == sp) {
            return defValue;
        }
        return sp.getFloat(key, defValue);
    }
    
}
