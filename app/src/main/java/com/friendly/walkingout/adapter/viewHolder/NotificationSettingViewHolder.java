package com.friendly.walkingout.adapter.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import com.friendly.walkingout.R;
import com.friendly.walkingout.adapter.baseInterface.BaseSettingViewHolderInterface;
import com.friendly.walkingout.dataSet.NotificationSettingListData;
import com.friendly.walkingout.util.JWLog;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

public class NotificationSettingViewHolder extends BaseViewHolder implements BaseSettingViewHolderInterface {

    public ImageButton acceptButton;
    public ImageButton acceptGeofenceButton;

    public boolean isAccepted;
    public boolean isGeoAccepted;

    private View.OnClickListener mOnClickListener;

    public NotificationSettingViewHolder(Context context, View itemView) {
        super(context, itemView);
        JWLog.e("", "");

        acceptButton = (ImageButton) itemView.findViewById(R.id.accept_notification);
        acceptGeofenceButton = (ImageButton) itemView.findViewById(R.id.accept_geofence_notification);

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
            }
        };

        acceptButton.setOnClickListener(mOnClickListener);
        acceptGeofenceButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public NotificationSettingViewHolder setLayout(Context context) {
        return new NotificationSettingViewHolder(context, mView);
    }

    @Override
    public void setDataSet(Object object) {
        if(object instanceof NotificationSettingListData) {
            NotificationSettingListData data = (NotificationSettingListData)object;

            this.isAccepted = data.getAcceptedState();
            this.isGeoAccepted = data.getGeofenceAcceptedState();

            acceptButton.setSelected(isAccepted);
            acceptGeofenceButton.setSelected(isGeoAccepted);

            this.itemView.setTag(data);
        }
    }
}
