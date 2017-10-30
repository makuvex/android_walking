package com.friendly.walking.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class JWLog
{
    private static final String APP_NAME             = "makuvex";
    private static final int    STACK_NUMBER_CURRENT = 2;
    private static final int    STACK_NUMBER_BEFORE  = 3;

    public static final byte TYPE_NONE        = 0x00;
    public static final byte TYPE_INFO        = 0x01;
    public static final byte TYPE_DEBUG       = 0x02;
    public static final byte TYPE_WARN        = 0x04;
    public static final byte TYPE_ERROR       = 0x08;
    public static final byte TYPE_VERBOSE     = 0x10;
    public static final byte TYPE_STACK_TRACE = 0x20;

    private static final String LOG_TAG             = "walking";

    private static void log(byte type,String tag, String message, int stackNumber) {

        if(TextUtils.isEmpty(tag)){
            tag = APP_NAME;
        }

        String logText;

        try {
            String tagName = "";
            Throwable throwable = new Throwable();

            String temp = throwable.getStackTrace()[stackNumber].getClassName();
            if (temp != null)
            {
                int lastDotPos = temp.lastIndexOf('.');
                tagName = temp.substring(lastDotPos + 1);
            }
            String methodName = throwable.getStackTrace()[stackNumber].getMethodName();
            int lineNumber = throwable.getStackTrace()[stackNumber].getLineNumber();

            logText = "[" + tagName + "] " + methodName + "()" + "[line:" + lineNumber + "]" + " >> " + message;

        } catch (Exception e) {
            e.printStackTrace();
            logText = message;
        }

        if(type == TYPE_VERBOSE){
            Log.v(tag, logText);
        }else if(type == TYPE_INFO){
            Log.i(tag, logText);
        }else if(type == TYPE_WARN){
            Log.w(tag, logText);
        }else if(type == TYPE_ERROR){
            Log.e(tag, logText);
        }else{
            Log.d(tag, logText);
        }
    }

    public static void v(String tag, String message)
    {
        log(TYPE_VERBOSE, tag, message,STACK_NUMBER_CURRENT);
    }

    public static void i(String tag, String message)
    {
        log(TYPE_INFO, tag, message,STACK_NUMBER_CURRENT);
    }

    public static void d(String tag, String message)
    {
        log(TYPE_DEBUG, tag, message,STACK_NUMBER_CURRENT);
    }

    public static void w(String tag, String message)
    {
        log(TYPE_WARN, tag, message,STACK_NUMBER_CURRENT);
    }

    public static void e(String tag, String message)
    {
        log(TYPE_ERROR, tag, message,STACK_NUMBER_CURRENT);
    }

    public static void b(String tag, String message){
        log(TYPE_DEBUG, tag, message,STACK_NUMBER_BEFORE);
    }

    public static void p(Context cxt, String message) {
        if(null == cxt) {
            return;
        }

        log(TYPE_DEBUG, LOG_TAG, message,STACK_NUMBER_CURRENT);
    }

    public static void printStackTrace(String tag, Exception e){
        StackTraceElement[] list = e.getStackTrace();
        if(null != list && list.length > 0){
            StringBuilder message = new StringBuilder();
            message.append("\n ").append(e.toString());
            for(StackTraceElement ee : list){
                message.append("\n at ")
                        .append(ee.getClassName())
                        .append(".")
                        .append(ee.getMethodName())
                        .append("(")
                        .append(ee.getFileName())
                        .append(":")
                        .append(ee.getLineNumber())
                        .append(")");
            }
            log(TYPE_ERROR, tag, message.toString(),STACK_NUMBER_CURRENT);
        }
    }


}
