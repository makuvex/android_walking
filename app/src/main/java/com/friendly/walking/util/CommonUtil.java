package com.friendly.walking.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.net.URLDecoder;
import java.net.URLEncoder;

public class CommonUtil {

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String urlEncoding(String temp,int lengthlimit){
        String url = "";
        try{
            if(temp != null){
                if(lengthlimit > 0) {
                    if (temp.length() > lengthlimit) {
                        temp = temp.substring(0, lengthlimit);
                    }
                }
                url = URLEncoder.encode(temp, "euc-kr");
            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return url;
    }

    public static String urlDecoding(String temp){
        String url = "";
        try{
            if(null != temp){
                url = URLDecoder.decode(temp,"euc-kr");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
}
