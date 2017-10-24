package com.friendly.walkingout.adapter.viewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.friendly.walkingout.R;
import com.friendly.walkingout.adapter.BaseSettingViewHolderInterface;
import com.friendly.walkingout.dataSet.LoginSettingListData;
import com.friendly.walkingout.dataSet.NotificationSettingListData;
import com.friendly.walkingout.util.JWLog;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

public class NotificationSettingViewHolder extends BaseViewHolder implements BaseSettingViewHolderInterface {

    public ImageButton acceptButton;

    public boolean isAccepted;

    private View.OnClickListener mOnClickListener;

    public NotificationSettingViewHolder(View itemView) {
        super(itemView);
        JWLog.e("", "");

        acceptButton = (ImageButton) itemView.findViewById(R.id.accept_notification);

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JWLog.e("","acceptButton");
            }
        };

        acceptButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public NotificationSettingViewHolder setLayout() {
        return new NotificationSettingViewHolder(mView);
    }

    @Override
    public void setDataSet(Object object) {
        if(object instanceof NotificationSettingListData) {
            NotificationSettingListData data = (NotificationSettingListData)object;

            this.isAccepted = data.getAcceptedState();
            this.itemView.setTag(data);
        }
    }
}
