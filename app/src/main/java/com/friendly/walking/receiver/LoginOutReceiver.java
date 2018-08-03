package com.friendly.walking.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.friendly.walking.util.JWToast;

import com.friendly.walking.ApplicationPool;
import com.friendly.walking.GlobalConstantID;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.Crypto;
import com.friendly.walking.util.JWLog;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

/**
 * Created by jungjiwon on 2017. 11. 1..
 */

public class LoginOutReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        JWLog.e("","action :"+intent.getAction());

        if(JWBroadCast.BROAD_CAST_LOGIN.equals(intent.getAction())) {
            final String email = intent.getStringExtra("email");
            final String password = intent.getStringExtra("password");


            JWLog.e("","email :"+email+", password : "+password);

            FireBaseNetworkManager.getInstance(context).loginEmailWithPassword(email, password, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                @Override
                public void onCompleted(boolean result, Object object) {
                    JWLog.e("result :"+result);
                    if(result) {
                        PreferencePhoneShared.setLoginYn(context, true);

                        Intent i = new Intent(JWBroadCast.BROAD_CAST_EMAIL_LOGIN);
                        i.putExtra("email", email);
                        JWBroadCast.sendBroadcast(context, i);
                    } else {
                        JWToast.showToast("로그인 실패");
                    }
                }
            });
        } else if(JWBroadCast.BROAD_CAST_LOGOUT.equals(intent.getAction())) {
            PreferencePhoneShared.setLoginYn(context, false);
            PreferencePhoneShared.setAutoLoginType(context, GlobalConstantID.LOGIN_TYPE_NONE);
            PreferencePhoneShared.setAutoLoginYn(context, false);
            PreferencePhoneShared.setLoginID(context, "");
            PreferencePhoneShared.setUserUID(context, "");
            PreferencePhoneShared.setNickName(context, "");
            PreferencePhoneShared.setWalkingCoin(context, 0);
            PreferencePhoneShared.setMyLocationAcceptedYn(context, false);
            PreferencePhoneShared.setChattingAcceptYn(context, false);

            PreferencePhoneShared.setNotificationYn(context, false);
            PreferencePhoneShared.setGeoNotificationYn(context, false);
            PreferencePhoneShared.setLocationYn(context, false);
        }
    }
}
