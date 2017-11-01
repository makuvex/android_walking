package com.friendly.walking.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.friendly.walking.ApplicationPool;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.preference.PreferencePhoneShared;
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
            String password = intent.getStringExtra("password");

            FireBaseNetworkManager.getInstance(context).loginEmailWithPassword(email, password, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                @Override
                public void onCompleted(boolean result, Task<AuthResult> task) {
                    if(result) {
                        PreferencePhoneShared.setLoginYn(context, true);

                        Intent i = new Intent(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI);
                        i.putExtra("email", email);
                        JWBroadCast.sendBroadcast(context, i);
                    }
                }
            });
        } else if(JWBroadCast.BROAD_CAST_LOGOUT.equals(intent.getAction())) {
            PreferencePhoneShared.setLoginYn(context, false);
        }
    }
}
