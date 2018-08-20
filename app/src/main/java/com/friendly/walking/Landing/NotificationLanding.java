package com.friendly.walking.Landing;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.friendly.walking.util.JWToast;

import com.friendly.walking.ApplicationPool;
import com.friendly.walking.R;
import com.friendly.walking.activity.SignUpActivity;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.RemoteNotificationData;
import com.friendly.walking.dataSet.UserData;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.JWLog;

import static com.friendly.walking.notification.NotificationUtil.NOTIFICATION_ID_GEOFENCE_CANCEL;

/**
 * Created by Administrator on 2017-10-21.
 */
public class NotificationLanding extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_something);

        final int id;

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            id = -1;
        } else {
            id = extras.getInt("notificationId");
        }
        JWLog.e("id :"+id);

        RemoteNotificationData data = extras.getParcelable("data");

        JWLog.e("data :"+data);

        if(id == NOTIFICATION_ID_GEOFENCE_CANCEL) {
            CommonUtil.alertDialogShow(NotificationLanding.this, "알림", "산책을 그만 하시려구요?.", new CommonUtil.CompleteCallback() {
                @Override
                public void onCompleted(boolean result, Object object) {
                    if (result) {
                        JWBroadCast.sendBroadcast(NotificationLanding.this, new Intent(JWBroadCast.BROAD_CAST_GEOFENCE_IN_DETECTED));
                        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        nm.cancel(id);
                        JWToast.showToast("산책 모드를 종료했습니다.");
                    } else {

                    }
                    finish();
                    overridePendingTransition(0,0);
                }
            });
        } else {
            JWToast.showToast("NotificationLanding id :"+id);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
