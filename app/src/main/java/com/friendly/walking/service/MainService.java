package com.friendly.walking.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import com.friendly.walking.util.JWToast;

import com.friendly.walking.GlobalConstantID;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.LocationData;
import com.friendly.walking.dataSet.WalkingData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.geofence.Constants;
import com.friendly.walking.geofence.GeofenceManager;
import com.friendly.walking.notification.NotificationUtil;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.Crypto;
import com.friendly.walking.util.JWLog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.friendly.walking.notification.NotificationUtil.NOTIFICATION_ID_GEOFENCE;
import static com.friendly.walking.notification.NotificationUtil.NOTIFICATION_ID_GEOFENCE_FINISHED;
import static com.friendly.walking.notification.NotificationUtil.NOTIFICATION_ID_GEOFENCE_MODE;
//import com.google.android.gms.location.Geofence;


/**
 * Created by Administrator on 2017-10-22.
 */

public class MainService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int                        UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int                        FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초
    // 최소 GPS 정보 업데이트 거리 10미터
    private static final float                      MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 1분
    private static final long                       MIN_TIME_BW_UPDATES = 1000 * 60 * 1;


    private BroadcastReceiver                       mReceiver = null;
    private IntentFilter                            mIntentFilter = null;
    private boolean                                 mIsRegisterdReceiver = false;


    private ServiceThread                           mThread;
    private Handler                                 mHandler = new ServiceHandler();

    // 현재 GPS 사용유무
    boolean                                         isGPSEnabled = false;

    // 네트워크 사용유무
    boolean                                         isNetworkEnabled = false;

    // GPS 상태값
    boolean                                         isGetLocation = false;

    Location                                        location;
    double                                          lat; // 위도
    double                                          lon; // 경도

    LocationListener                                locationListener;
    protected LocationManager                       locationManager;
    //private GoogleApiClient                         mGoogleApiClient = null;
    LocationRequest                                 locationRequest = null;
    private ArrayList<LocationData>                 mLocationArray = null;
    private FireBaseAsyncTask                       mAsyncTask = null;
    private long                                    mStartStrollTime = 0;

    class ServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            JWLog.e("msg :"+msg);
            getLocation();

            JWLog.e("@@@@ lat :"+getLatitude()+", lot :"+getLongitude());
            JWLog.e("@@@@2 lat :"+lat+", lot :"+lon);
            LocationData prevData = null;

            if(mLocationArray.size() > 0) {
                prevData = mLocationArray.get(mLocationArray.size()-1);
            }
            if(prevData != null) {
                if(prevData.longtitude != getLongitude() || prevData.latitude != getLatitude()) {
                    mLocationArray.add(new LocationData(System.currentTimeMillis(), getLatitude(), getLongitude()));
                } else {
                    JWLog.e("동일한 좌표여서 어레이에 애드 안함.");
                }
            } else {
                mLocationArray.add(new LocationData(System.currentTimeMillis(), getLatitude(), getLongitude()));
            }

            /*
            NotificationUtil.getInstance(getApplicationContext()).makeNotification(1,
                    "우리 은비와 함께 산책할 시간입니다.",
                    "우리 은비와 함께 산책할 시간입니다.",
                    "어서 같이 나가셔야죠!");
                    */
        }
    };

    static class FireBaseAsyncTask extends AsyncTask<String, String, Boolean> {

        private Context                         context;
        private ArrayList<LocationData>         list;
        private long                            startTime;

        public FireBaseAsyncTask(Context context, ArrayList<LocationData> list, long start) {
            this.context = context;
            this.list = list;
            this.startTime = start;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                if(list != null && list.size() == 0) {
                    return false;
                } else {
                    int autoLoginType = PreferencePhoneShared.getAutoLoginType(context);
                    String email = null;
                    if(autoLoginType == GlobalConstantID.LOGIN_TYPE_EMAIL) {
                        String key = PreferencePhoneShared.getUserUid(context);
                        String paddedKey = key.substring(0, 16);

                        JWLog.e("", "uid :" + paddedKey);
                        email = CommonUtil.urlDecoding(Crypto.decryptAES(PreferencePhoneShared.getLoginID(context), paddedKey));
                    }

                    JWLog.e("email :"+email);
                    if(email != null) {
                        long min = (System.currentTimeMillis() - startTime) / 60000;

                        FireBaseNetworkManager.getInstance(context).deleteWalkingData(new FireBaseNetworkManager.FireBaseNetworkCallback() {
                            @Override
                            public void onCompleted(boolean result, Object object) {
                                JWLog.e("result :"+result);
                            }
                        });
                        FireBaseNetworkManager.getInstance(context).updateWalkingTimeList(email, min, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                            @Override
                            public void onCompleted(boolean result, Object object) {
                                JWToast.showToast("산책 시간 업데이트 "+(result ? "성공" :"실패"));
                            }
                        });

                        FireBaseNetworkManager.getInstance(context).updateWalkingLocationList(email, list, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                            @Override
                            public void onCompleted(boolean result, Object object) {
                                if(result) {
                                    NotificationUtil.getInstance(context).makeNotification(NOTIFICATION_ID_GEOFENCE_FINISHED,
                                            "자동 산책 완료",
                                            "산책기록을 성공적으로 업로드 했습니다.",
                                            "자동 산책 완료");
                                }
                            }
                        });
                    } else {
                        return false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            JWLog.e("result :"+result);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("","@@@ onStartCommand @@@");

        //ServiceHandler mHandler = new ServiceHandler();


        //mThread = new ServiceThread(mHandler);
        //thread.start();

        initReceiver();
        registerReceiverMain();
        mLocationArray = new ArrayList<>();

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();

        locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                JWLog.e("onLocationChanged lat :"+location.getLatitude()+ ", lot : "+location.getLongitude());
                lat = location.getLatitude();
                lon = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                JWLog.e("onStatusChanged s:"+s+", i:"+i);
            }

            @Override
            public void onProviderEnabled(String s) {
                JWLog.e("onProviderEnabled s:"+s);
            }

            @Override
            public void onProviderDisabled(String s) {
                JWLog.e("onProviderDisabled s :"+s);
            }
        };
        return START_STICKY;
    }

    public void onDestroy() {
        if(mThread != null) {
            mThread.stopForever();
            mThread = null;
        }
//        if (mGoogleApiClient != null &&  mGoogleApiClient.isConnected()) {
//            JWLog.e("mGoogleApiClient disconnect");
//            mGoogleApiClient.disconnect();
//        }

        unregisterReceiverMain();

    }

    private void initReceiver() {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_GEOFENCE_OUT_DETECTED);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_GEOFENCE_IN_DETECTED);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_ADD_GEOFENCE);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_REMOVE_GEOFENCE);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                JWLog.e("","action :"+intent.getAction());

                if(JWBroadCast.BROAD_CAST_ADD_GEOFENCE.equals(intent.getAction())) {
                    String address = intent.getStringExtra("address");
                    String lat = intent.getStringExtra("lat");
                    String lot = intent.getStringExtra("lot");

                    GeofenceManager.getInstance(MainService.this).populateGeofenceList(address, lat, lot);
                    GeofenceManager.getInstance(MainService.this).addGeofences();
                } else if(JWBroadCast.BROAD_CAST_REMOVE_GEOFENCE.equals(intent.getAction())) {
                    GeofenceManager.getInstance(MainService.this).removeGeofences();
                } else if(JWBroadCast.BROAD_CAST_GEOFENCE_IN_DETECTED.equals(intent.getAction())) {
                    JWLog.e("thread :"+(mThread != null ? mThread: "null"));

                    if(mThread != null) {
                        JWLog.e("thread stop");
                        mThread.stopForever();
                        mThread = null;

                        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(NOTIFICATION_ID_GEOFENCE);

                        for(LocationData data : mLocationArray) {
                            JWLog.e("data :"+data.toString());
                        }
                        if(mAsyncTask != null) {
                            mAsyncTask.cancel(true);
                            mAsyncTask = null;
                        }
                        mAsyncTask = new FireBaseAsyncTask(MainService.this, mLocationArray, mStartStrollTime);
                        mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        NotificationUtil.getInstance(getApplicationContext()).makeNotification(NOTIFICATION_ID_GEOFENCE,
                                "IN NOTI",
                                "IN NOTI",
                                "IN NOTI");
                    }
                } else if(JWBroadCast.BROAD_CAST_GEOFENCE_OUT_DETECTED.equals(intent.getAction())) {
                    //setProgressBar(View.INVISIBLE);
                    int transition = intent.getIntExtra("transition", -1);
                    JWLog.e("","@@@@ transition :"+(transition == Geofence.GEOFENCE_TRANSITION_ENTER ? "enter" : "exit"));

                    if(transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                        if(mThread != null) {
                            JWLog.e("@@@ thread is already running @@@");
                            return;
                        }

                        Date date = new Date(System.currentTimeMillis());
                        SimpleDateFormat formatter = new SimpleDateFormat( "HHmm", Locale.KOREA );
                        String dateTime = formatter.format(date);

                        String startTime = PreferencePhoneShared.getStartStrollTime(context);
                        String endTime = PreferencePhoneShared.getEndStrollTime(context);

                        int start = 0;//Integer.parseInt(startTime);
                        int end = 0;//Integer.parseInt(endTime);

                        try {
                            start = Integer.parseInt(startTime);
                            end = Integer.parseInt(endTime);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(Integer.parseInt(dateTime) >= start && Integer.parseInt(dateTime) <= end) {
                            mStartStrollTime = System.currentTimeMillis();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    NotificationUtil.getInstance(getApplicationContext()).makeNotification(NOTIFICATION_ID_GEOFENCE_MODE,
                                            "자동으로 산책모드 실행 중 입니다.",
                                            "자동으로 산책모드 실행 중 입니다.",
                                            "경로를 기록중 입니다.");

                                    writeWalkingData();

                                    mLocationArray.clear();
                                    mThread = new ServiceThread(mHandler);
                                    mThread.start();
                                }
                            }, 5000);
                        } else {
                            JWLog.e("자동 산책 시간이 아닙니다.");
                            //JWToast.showToast("자동 산책 시간이 아닙니다.");
                        }
                    } else if(transition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                        mStartStrollTime = System.currentTimeMillis();

                        NotificationUtil.getInstance(getApplicationContext()).makeNotification(NOTIFICATION_ID_GEOFENCE_MODE,
                                "자동으로 산책모드 실행 중 입니다.",
                                "자동으로 산책모드 실행 중 입니다.",
                                "경로를 기록중 입니다.");

                        writeWalkingData();

                        mLocationArray.clear();
                        mThread = new ServiceThread(mHandler);
                        mThread.start();

                    }
                }
            }
        };
    }

    private void registerReceiverMain() {
        if(mIsRegisterdReceiver != true) {
            registerReceiver(mReceiver, mIntentFilter);
            mIsRegisterdReceiver = true;
        }
    }

    private void unregisterReceiverMain() {
        if(mIsRegisterdReceiver == true) {
            unregisterReceiver(mReceiver);
            mIsRegisterdReceiver = false;
        }
    }

    public Location getLocation() {
        try {
            if(locationManager == null) {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            }
            // GPS 정보 가져오기
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // 현재 네트워크 상태 값 알아오기
            //isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            isNetworkEnabled = false;

            if (!isGPSEnabled && !isNetworkEnabled) {
                // GPS 와 네트워크사용이 가능하지 않을때 소스 구현
                JWToast.showToast("GPS와 네트워크 사용이 불가능 합니다.");
            } else {
                this.isGetLocation = true;

                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }

                // 네트워크 정보로 부터 위치값 가져오기
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            // 위도 경도 저장
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * GPS 종료
     * */
    public void stopUsingGPS(){
        if(locationManager != null && locationListener != null){
            locationManager.removeUpdates(locationListener);
        }
    }

    /**
     * 위도값을 가져옵니다.
     * */
    public double getLatitude(){
        if(location != null){
            lat = location.getLatitude();
        }
        return lat;
    }

    /**
     * 경도값을 가져옵니다.
     * */
    public double getLongitude(){
        if(location != null){
            lon = location.getLongitude();
        }
        return lon;
    }

    /**
     * GPS 나 wifi 정보가 켜져있는지 확인합니다.
     * */
    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    /**
     * GPS 정보를 가져오지 못했을때
     * 설정값으로 갈지 물어보는 alert 창
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("GPS 사용설정");
        alertDialog.setMessage("GPS 설정이 되지 않았을수도 있습니다.\n설정 화면으로 가시겠습니까?");

                // OK 를 누르게 되면 설정창으로 이동합니다.
                alertDialog.setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void writeWalkingData() {
        int autoLoginType = PreferencePhoneShared.getAutoLoginType(MainService.this);
        String email = null;
        //if(autoLoginType == GlobalConstantID.LOGIN_TYPE_EMAIL) {
            String key = PreferencePhoneShared.getUserUid(MainService.this);
            String paddedKey = key.substring(0, 16);

            JWLog.e("", "uid :" + paddedKey);
            try {
                email = CommonUtil.urlDecoding(Crypto.decryptAES(PreferencePhoneShared.getLoginID(MainService.this), paddedKey));
            } catch (Exception e) {
                e.printStackTrace();
            }
        //}

        JWLog.e("email :"+email);
        if(email != null) {

            /*
            public String mem_email = "";
            public String mem_nickname = "";
            public boolean mem_walking_my_location_yn = true;
            public boolean mem_walking_chatting_yn = true;
            public String lat = "0";
            public String lot = "0";
            */

            WalkingData data = new WalkingData();
            data.uid = PreferencePhoneShared.getUserUid(MainService.this);
            data.mem_email = email;
            data.mem_nickname = PreferencePhoneShared.getNickName(MainService.this);
            data.mem_walking_my_location_yn = PreferencePhoneShared.getMyLocationAcceptedYn(MainService.this);
            data.mem_walking_chatting_yn = PreferencePhoneShared.getChattingAcceptYn(MainService.this);

            data.lat = ""+getLatitude();
            data.lot = ""+getLongitude();

            JWLog.e("data :"+data);

            FireBaseNetworkManager.getInstance(getApplicationContext()).updteWalkingData(data, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                @Override
                public void onCompleted(boolean result, Object object) {
                    JWLog.e("result :"+result+", object :"+object);
                }
            });
        }
    }
}
