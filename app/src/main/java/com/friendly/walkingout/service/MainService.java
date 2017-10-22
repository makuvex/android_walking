package com.friendly.walkingout.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.friendly.walkingout.GlobalConstantID;
import com.friendly.walkingout.notification.NotificationUtil;
//import com.google.android.gms.location.Geofence;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-10-22.
 */

public class MainService extends Service {
    ServiceThread thread;
//    ArrayList<Geofence> mGeofenceList = new ArrayList<Geofence>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("","@@@ onStartCommand @@@");
        ServiceHandler handler = new ServiceHandler();
//        thread = new ServiceThread(handler);
//        thread.start();



        return START_STICKY;
    }

    public void onDestroy() {
        thread.stopForever();
        thread = null;//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
    }

    class ServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            NotificationUtil.getInstance(getApplicationContext()).makeNotification(1,
                    "우리 은비와 함께 산책할 시간입니다.",
                    "우리 은비와 함께 산책할 시간입니다.",
                    "어서 같이 나가셔야죠!");
        }
    };

}
