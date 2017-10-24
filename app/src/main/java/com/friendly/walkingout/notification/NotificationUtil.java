package com.friendly.walkingout.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

import com.friendly.walkingout.GlobalConstantID;
import com.friendly.walkingout.landing.NotificationSomething;
import com.friendly.walkingout.R;

/**
 * Created by JungJiWon on 2017-10-21.
 */

public class NotificationUtil extends Object {

    private static NotificationUtil         mSelf;
    private Context                          mContext;

    public static NotificationUtil getInstance(Context context) {
        if(mSelf == null) {
            mSelf = new NotificationUtil(context);
        }
        return mSelf;
    }

    public NotificationUtil(Context context) {
        mContext = context;
    }

    public void makeNotification(int notificationId, String ticker, String title, String subTitle) {
        Resources res = mContext.getResources();

        Intent notificationIntent = new Intent(mContext, NotificationSomething.class);
        notificationIntent.putExtra("notificationId", notificationId);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

        builder.setContentTitle(title)
                .setContentText(subTitle)
                .setTicker(ticker)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL)
                .addAction(R.mipmap.ic_launcher, "산책 출발", contentIntent)
                .build();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(GlobalConstantID.NOTIFICATION_ID, builder.build());
    }

}
