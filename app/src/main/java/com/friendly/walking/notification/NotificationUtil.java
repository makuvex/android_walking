package com.friendly.walking.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

import com.friendly.walking.ApplicationPool;
import com.friendly.walking.GlobalConstantID;
import com.friendly.walking.Landing.NotificationLanding;
import com.friendly.walking.R;
import com.friendly.walking.dataSet.RemoteNotificationData;
import com.friendly.walking.util.JWLog;

/**
 * Created by JungJiWon on 2017-10-21.
 */

public class NotificationUtil extends Object {

    public static int NOTIFICATION_ID_GEOFENCE = 0;
    public static int NOTIFICATION_ID_GEOFENCE_MODE = 1;
    public static int NOTIFICATION_ID_GEOFENCE_FINISHED = 2;
    public static int NOTIFICATION_ID_GEOFENCE_CANCEL = 3;
    public static int NOTIFICATION_ID_REMOTE_DATA = 4;

    private static NotificationUtil             mSelf;
    private Context                             mContext;
    private RemoteNotificationData              mData;


    public static NotificationUtil getInstance(Context context) {
        if(mSelf == null) {
            mSelf = new NotificationUtil(context);
        }
        mSelf.mData = null;
        return mSelf;
    }

    public NotificationUtil(Context context) {
        mContext = context;
    }

    public void makeNotification(int notificationId, RemoteNotificationData data) {
        mData = data;
        makeNotification(notificationId, data.title, data.title, data.message);
    }

    public void makeNotification(int notificationId, String ticker, String title, String subTitle) {
        Resources res = mContext.getResources();

        Intent notificationIntent = new Intent(mContext, NotificationLanding.class);
        notificationIntent.putExtra("notificationId", notificationId);
        notificationIntent.putExtra("data", mData);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

        builder.setContentTitle(title)
                .setContentText(subTitle)
                .setTicker(ticker)
                .setSmallIcon(R.drawable.ic_stat_tag_faces)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL)
                .setColor(mContext.getResources().getColor(R.color.colorPrimary))
                .build();

        if(notificationId == NOTIFICATION_ID_GEOFENCE_MODE) {
            PendingIntent cancelPending = PendingIntent.getActivity(mContext,
                    0,
                    new Intent(mContext, NotificationLanding.class).putExtra("notificationId", NOTIFICATION_ID_GEOFENCE_CANCEL),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            builder.addAction(R.drawable.stop, "산책 중지", cancelPending).build();
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(GlobalConstantID.NOTIFICATION_ID, builder.build());
    }

}
